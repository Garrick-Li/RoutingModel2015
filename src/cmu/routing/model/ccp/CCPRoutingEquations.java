package cmu.routing.model.ccp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import cmu.routing.model.RoutingData;
import cmu.routing.model.RoutingEquations;

public class CCPRoutingEquations implements RoutingEquations
{
    private IloNumVar[] x = null;
    private IloNumVar[] y = null;
    private IloNumVar[] p = null;

    public CCPRoutingEquations(CCPRoutingData data) throws IloException
    {
        IloCplex cplex = new IloCplex();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();
        int flightLegs = data.getFlightLegs();
        x = new IloNumVar[strings];
        y = new IloNumVar[groundArcs];
        p = new IloNumVar[flightLegs];

        for (int i = 0; i < strings; i++)
        {
            x[i] = cplex.numVar(0, 1, IloNumVarType.Int);
        }

        for (int i = 0; i < groundArcs; i++)
        {
            y[i] = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
        }

        for (int i = 0; i < flightLegs; i++)
        {
            p[i] = cplex.numVar(0, 1, IloNumVarType.Float);
        }
    }

    @Override
    public void setObjectiveFunction(IloCplex cplex, RoutingData data)
            throws IloException
    {
        try
        {
            CCPRoutingData nomData = (CCPRoutingData) data;
            IloLinearNumExpr expr = cplex.linearNumExpr();
            IloObjective obj = cplex.addMinimize();
            double[][] propDelay = nomData.getPropDelay();
            int strings = nomData.getStrings();

            for (int i = 0; i < strings; i++)
            {
                expr.addTerm(propDelay[0][i], x[i]);
                //expr.addTerm(0, x[i]);
            }

            obj.setExpr(expr);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding objective function");
            throw ioe;
        }
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println("Finish setting objective function: " + dateFormat.format(date));
    }

    @Override
    public void setConstraints(IloCplex cplex, RoutingData data)
            throws IloException
    {
        CCPRoutingData ccpData = (CCPRoutingData) data;

        /* Add the cover failure probability constraint */
        try
        {
            setCoverFailureProbConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out
                    .println("Error in adding cover failure probability constraint");
            throw ioe;
        }
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println("Finish setting CoverFailureProbConstraint: " + dateFormat.format(date));

        /* Add the cover constraint */
        try
        {
            setCoverConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding cover constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting cover constraint: " + dateFormat.format(date));

        /* Add the flights in constraint */
        try
        {
            setFlightsInMaintConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding flights in constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting FlightsInMaintConstraint: " + dateFormat.format(date));

        /* Add the flights out constraint */
        try
        {
            setFlightsOutMaintConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding flights out constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting FlightsOutMaintConstraint: " + dateFormat.format(date));

        /* Add the count constraint */
        try
        {
            setCountConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding count constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting Count Constraint: " + dateFormat.format(date));

        /* Add the variable constraint */
        try
        {
            setVariableConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding variable constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting variable Constraint: " + dateFormat.format(date));
        
        /* Add the decision string constraint */
        try
        {
            setDecisionStringsConstraint(cplex, ccpData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding decision strings constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting decision strings constraint: " + dateFormat.format(date));
    }

    private void setCoverConstraint(IloCplex cplex, CCPRoutingData data)
            throws IloException
    {
        IloNumExpr constraint = null;
        int flightLegs = data.getFlightLegs();
        double[][] cover = data.getCover();
        int strings = data.getStrings();

        for (int i = 0; i < flightLegs; i++)
        {
            constraint = cplex.prod(cover[i][0], x[0]);

            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(cover[i][j], x[j]));
            }
            cplex.addEq(constraint, 1);
        }
    }

    private void setCoverFailureProbConstraint(IloCplex cplex,
            CCPRoutingData data) throws IloException
    {
        IloNumExpr constraint = null;
        int flightLegs = data.getFlightLegs();
        double[][] coverFailProb = data.getCoverFailureProb();
        int strings = data.getStrings();

        for (int i = 0; i < flightLegs; i++)
        {
            constraint = cplex.prod(coverFailProb[i][0], x[0]);

            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(coverFailProb[i][j], x[j]));
            }
            cplex.addLe(constraint, p[i]);
        }
    }

    private void setFlightsInMaintConstraint(IloCplex cplex, CCPRoutingData data)
            throws IloException
    {
        IloNumExpr constraint = null;
        int maintLoc = data.getMaintLocations();
        double[][] flightMaintStringsIn = data.getFlightMaintStringsIn();
        double[][] flightMaintGroundIn = data.getFlightMaintGroundIn();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();

        for (int i = 0; i < maintLoc; i++)
        {
            constraint = cplex.prod(flightMaintStringsIn[i][0], x[0]);

            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintStringsIn[i][j], x[j]));
            }

            for (int j = 0; j < groundArcs; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintGroundIn[i][j], y[j]));
            }
            cplex.addEq(constraint, 0);
        }
    }

    private void setFlightsOutMaintConstraint(IloCplex cplex,
            CCPRoutingData data) throws IloException
    {
        IloNumExpr constraint = null;
        int maintLoc = data.getMaintLocations();
        double[][] flightMaintStringsOut = data.getFlightMaintStringsOut();
        double[][] flightMaintGroundOut = data.getFlightMaintGroundOut();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();

        for (int i = 0; i < maintLoc; i++)
        {
            constraint = cplex.prod(flightMaintStringsOut[i][0], x[0]);

            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintStringsOut[i][j], x[j]));
            }

            for (int j = 0; j < groundArcs; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintGroundOut[i][j], y[j]));
            }
            cplex.addEq(constraint, 0);
        }
    }

    private void setCountConstraint(IloCplex cplex, CCPRoutingData data)
            throws IloException
    {
        IloNumExpr constraint = null;
        double[][] countStrings = data.getCountStrings();
        double[][] countGround = data.getCountGround();
        int airCrafts = data.getAirCrafts();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();
        constraint = cplex.prod(countStrings[0][0], x[0]);
        for (int j = 1; j < strings; j++)
        {
            constraint = cplex.sum(constraint,
                    cplex.prod(countStrings[0][j], x[j]));
        }
        for (int j = 0; j < groundArcs; j++)
        {
            constraint = cplex.sum(constraint,
                    cplex.prod(countGround[0][j], y[j]));
        }
        cplex.addLe(constraint, airCrafts);
    }

    private void setVariableConstraint(IloCplex cplex, CCPRoutingData data)
            throws IloException
    {
        int flightLegs = data.getFlightLegs();

        for (int i = 0; i < flightLegs; i++)
        {
            cplex.addLe(p[i], 0.52); //0.1 means 10% probability of delay exceeding 90 minutes.
        }
    }
    
    private void setDecisionStringsConstraint(IloCplex cplex,
            CCPRoutingData data) throws IloException
    {
        /*double[][] stringsInSolution = data.getStringsInSolution();
        int strings = data.getStrings();

        for (int i = 1; i < strings; i++)
        {
            if (stringsInSolution[0][i] == -1)
                break;
            if (stringsInSolution[0][i] - stringsInSolution[0][i - 1] > 1)
            {
                for (int j = (int) stringsInSolution[0][i - 1]; j < (int) stringsInSolution[0][i] - 1; j++)
                {
                    cplex.addEq(x[j], 0);
                }
            }
        }*/
    }
    public IloNumVar[] getX()
    {
        return x;
    }

	@Override
	public IloRange[] getCoverConstraints1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getCoverConstraints2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getFlightsInConstraints1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getFlightsInConstraints2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getFlightsOutConstraints1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getFlightsOutConstraints2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getCountConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConstraints(IloCplex cplex, RoutingData data,
			int[][] stringsInSolution) throws IloException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IloRange[] getBoundX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getCoverConstraintsEqual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getFlightsInConstraintsEqual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getFlightsOutConstraintsEqual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getConstraints233() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getConstraints235() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IloRange[] getConstraints237() {
		// TODO Auto-generated method stub
		return null;
	}
}
