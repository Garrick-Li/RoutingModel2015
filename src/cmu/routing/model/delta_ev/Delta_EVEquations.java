package cmu.routing.model.delta_ev;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import cmu.routing.model.RoutingData;
import cmu.routing.model.RoutingEquations;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
 * 
 * This class performs the routing functionality of adding objective function
 * and constraint equations
 * 
 */
public class Delta_EVEquations implements RoutingEquations
{
    private IloNumVar[] x = null;
    private IloNumVar[] y = null;

    /**
     * 
     * The constructor creates the variables and sets the range of each variable
     * 
     * @param data
     *            Object containing the routing data
     * @throws IloException
     *             Thrown when any error creating IloCplex object
     */
    public Delta_EVEquations(Delta_EVData data) throws IloException
    {
        IloCplex cplex = new IloCplex();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();
        int flightLegs = data.getFlightLegs();
        x = new IloNumVar[strings];
        y = new IloNumVar[groundArcs];
        
        for (int i = 0; i < strings; i++)
        {
            x[i] = cplex.numVar(0, 1, IloNumVarType.Int);
        }

        for (int i = 0; i < groundArcs; i++)
        {
            y[i] = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
        }
    }

    @Override
    public void setObjectiveFunction(IloCplex cplex, RoutingData data)
            throws IloException
    {
        try
        {
        	Delta_EVData nomData = (Delta_EVData) data;
            IloLinearNumExpr expr = cplex.linearNumExpr();
            //IloObjective obj = cplex.addMinimize();
            IloObjective obj = cplex.addMaximize();
            int strings = nomData.getStrings();
            int flightLegs = nomData.getFlightLegs();
            double coverHatValues[][] = nomData.getCoverHatValue();

            for (int i = 0; i < strings; i++)
            {
            	 for (int j = 0; j < flightLegs; j++)
                 {
                 expr.addTerm(coverHatValues[j][i], x[i]);
                 }
            }

            obj.setExpr(expr);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding objective function");
            throw ioe;
        }
    }

    @Override
    public void setConstraints(IloCplex cplex, RoutingData data)
            throws IloException
    {
    	Delta_EVData nomData = (Delta_EVData) data;
        /* Add the cover constraint */
        addnewcostraints(cplex, nomData);

        try
        {
            setCoverConstraint(cplex, nomData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding cover constraint");
            throw ioe;
        }

        /* Add the flights in constraint */
        try
        {
            setFlightsInMaintConstraint(cplex, nomData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding flights in constraint");
            throw ioe;
        }

        /* Add the flights out constraint */
        try
        {
            setFlightsOutMaintConstraint(cplex, nomData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding flights out constraint");
            throw ioe;
        }

        /* Add the count constraint */
        try
        {
            setCountConstraint(cplex, nomData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding count constraint");
            throw ioe;
        }

        /* Add the decision string constraint */
//        try
//        {
//            setDecisionStringsConstraint(cplex, nomData);
//        } catch (IloException ioe)
//        {
//            System.out.println("Error in adding decision strings constraint");
//            throw ioe;
//        }

    }

    private void setCoverConstraint(IloCplex cplex, Delta_EVData data)
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
            cplex.addGe(constraint, 1);
        }
    }

    private void setFlightsInMaintConstraint(IloCplex cplex,
    		Delta_EVData data) throws IloException
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
    		Delta_EVData data) throws IloException
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

    private void setCountConstraint(IloCplex cplex, Delta_EVData data)
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
    
    private void addnewcostraints(IloCplex cplex, Delta_EVData data) throws IloException{

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
