package cmu.routing.model.delta_obj_ev;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import ilog.concert.*;
import ilog.cplex.*;
import cmu.routing.model.RoutingData;
import cmu.routing.model.RoutingEquations;

public class Delta_OBJ_EVEquations implements RoutingEquations
{
    private IloNumVar[] x = null;
    private IloNumVar[] y = null;
    private IloNumVar[] v = null;
    private IloNumVar[] w = null;
    private IloNumVar[] DELTA = null;
    ArrayList<IloRange> constraints233 = new ArrayList<IloRange>();
    ArrayList<IloRange> constraints234 = new ArrayList<IloRange>();
    ArrayList<IloRange> constraints235 = new ArrayList<IloRange>();
    ArrayList<IloRange> constraints236 = new ArrayList<IloRange>();
    ArrayList<IloRange> constraints237 = new ArrayList<IloRange>();
    ArrayList<IloRange> constraints238 = new ArrayList<IloRange>();
    ArrayList<IloRange> coverConstraints1 = new ArrayList<IloRange>();
    ArrayList<IloRange> coverConstraints2 = new ArrayList<IloRange>();
    ArrayList<IloRange> coverConstraintsEqual = new ArrayList<IloRange>();
    ArrayList<IloRange> flightsInConstraints1 = new ArrayList<IloRange>();
    ArrayList<IloRange> flightsInConstraints2 = new ArrayList<IloRange>();
    ArrayList<IloRange> flightsInConstraintsEqual = new ArrayList<IloRange>();
    ArrayList<IloRange> flightsOutConstraints1 = new ArrayList<IloRange>();
    ArrayList<IloRange> flightsOutConstraints2 = new ArrayList<IloRange>();
    ArrayList<IloRange> flightsOutConstraintsEqual = new ArrayList<IloRange>();
    ArrayList<IloRange> countConstraints = new ArrayList<IloRange>();
    ArrayList<IloRange> boundX = new ArrayList<IloRange>();

    /**
     * 
     * The constructor creates the variables and sets the range of each variable
     * 
     * @param dobjevData
     *            Object containing the routing data
     * @throws IloException
     *             Thrown when any error creating IloCplex object
     */
    public Delta_OBJ_EVEquations(Delta_OBJ_EVData dobjevData) throws IloException
    {
        
        IloCplex cplex = new IloCplex();
        int strings = dobjevData.getStrings();
        int groundArcs = dobjevData.getGroundArcs();
        x = new IloNumVar[strings];
        y = new IloNumVar[groundArcs];
        v = new IloNumVar[strings];
        w = new IloNumVar[strings];
        DELTA = new IloNumVar[1];

        for (int i = 0; i < strings; i++)
        {
            //x[i] = cplex.numVar(0, 1, IloNumVarType.Int);
            x[i] = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Int);
        }

        for (int i = 0; i < groundArcs; i++)
        {
            y[i] = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
        }
        
        for (int i = 0; i < strings; i++)
        {
            v[i] = cplex.numVar(0, 1, IloNumVarType.Int);
        }
        
        for (int i = 0; i < strings; i++)
        {
            //w[i] = cplex.numVar(0, 1, IloNumVarType.Int);
            w[i] = cplex.numVar(0, 1, IloNumVarType.Float);
        }
        
        DELTA[0] = cplex.numVar(-Double.MAX_VALUE, Double.MAX_VALUE, IloNumVarType.Float);
    }

    @Override
    public void setObjectiveFunction(IloCplex cplex, RoutingData data)
            throws IloException
    {
        try
        {
            IloLinearNumExpr expr = cplex.linearNumExpr();
            IloObjective obj = cplex.addMinimize();
            expr.addTerm(1, DELTA[0]);
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
    public void setConstraints(IloCplex cplex, RoutingData data, int[][] stringsInSolution)
            throws IloException
    {
        Delta_OBJ_EVData devData = (Delta_OBJ_EVData) data;
        
        setObjectiveFunction(cplex, devData);
        
        
        addnewcostraints(cplex, devData);
        /* Add the flights in constraint */
        try
        {
            setFlightsInMaintConstraint(cplex, devData, stringsInSolution);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding flights in constraint");
            throw ioe;
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Finish setting FlightsInMaintConstraint: " + dateFormat.format(date));

        /* Add the flights out constraint */
        try
        {
            setFlightsOutMaintConstraint(cplex, devData, stringsInSolution);
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
            setCountConstraint(cplex, devData, stringsInSolution);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding count constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting count constraint: " + dateFormat.format(date));

        /* Add the cover hat value constraint */
        try
        {
            setCoverConstraint(cplex, devData, stringsInSolution);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding cover constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting Cover Constraint: " + dateFormat.format(date));
        
        try
        {
            set233Constraint(cplex, devData, stringsInSolution);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding 2.33 constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting 2.33 Constraint: " + dateFormat.format(date));
        
        try
        {
            set234Constraint(cplex, devData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding 2.34 constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting 2.34 Constraint: " + dateFormat.format(date));
        
        try
        {
            set235Constraint(cplex, devData, stringsInSolution);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding 2.35 constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting 2.35 Constraint: " + dateFormat.format(date));
        
        try
        {
            set236Constraint(cplex, devData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding 2.36 constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting 2.36 Constraint: " + dateFormat.format(date));
        
        try
        {
            set237Constraint(cplex, devData, stringsInSolution);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding 2.37 constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting 2.37 Constraint: " + dateFormat.format(date));
        
        try
        {
            set238Constraint(cplex, devData);
        } catch (IloException ioe)
        {
            System.out.println("Error in adding 2.38 constraint");
            throw ioe;
        }
        date = new Date();
        System.out.println("Finish setting 2.38 Constraint: " + dateFormat.format(date));

    }

    private void setFlightsInMaintConstraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        int maintLoc = data.getMaintLocations();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        double[][] flightMaintStringsIn = data.getFlightMaintStringsIn();
        double[][] flightMaintGroundIn = data.getFlightMaintGroundIn();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();

        for (int i = 0; i < maintLoc; i++)
        {
            constraint = cplex.prod(flightMaintStringsIn[i][(int)stringsSortedAsHats[0][0]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][0]-1], x[(int)stringsSortedAsHats[0][0]-1]);

            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintStringsIn[i][(int)stringsSortedAsHats[0][j]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][j]-1], x[(int)stringsSortedAsHats[0][j]-1]));
            }

            for (int j = 0; j < groundArcs; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintGroundIn[i][j], y[j]));
            }
            flightsInConstraintsEqual.add(cplex.addEq(constraint, 0));
        }
    }

    private void setFlightsOutMaintConstraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        int maintLoc = data.getMaintLocations();
        double[][] flightMaintStringsOut = data.getFlightMaintStringsOut();
        double[][] flightMaintGroundOut = data.getFlightMaintGroundOut();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();

        for (int i = 0; i < maintLoc; i++)
        {
            constraint = cplex.prod(flightMaintStringsOut[i][(int)stringsSortedAsHats[0][0]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][0]-1], x[(int)stringsSortedAsHats[0][0]-1]);

            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintStringsOut[i][(int)stringsSortedAsHats[0][j]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][j]-1], x[(int)stringsSortedAsHats[0][j]-1]));
            }

            for (int j = 0; j < groundArcs; j++)
            {
                constraint = cplex.sum(constraint,
                        cplex.prod(flightMaintGroundOut[i][j], y[j]));
            }
            flightsOutConstraintsEqual.add(cplex.addEq(constraint, 0));
        }
    }

    private void setCountConstraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        double[][] countStrings = data.getCountStrings();
        double[][] countGround = data.getCountGround();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        int airCrafts = data.getAirCrafts();
        int strings = data.getStrings();
        int groundArcs = data.getGroundArcs();

        constraint = cplex.prod(countStrings[0][(int)stringsSortedAsHats[0][0]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][0]-1], x[(int)stringsSortedAsHats[0][0]-1]);
        for (int j = 1; j < strings; j++)
        {
            constraint = cplex.sum(constraint,
                    cplex.prod(countStrings[0][(int)stringsSortedAsHats[0][j]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][j]-1], x[(int)stringsSortedAsHats[0][j]-1]));
        }
        for (int j = 0; j < groundArcs; j++)
        {
            constraint = cplex.sum(constraint,
                    cplex.prod(countGround[0][j], y[j]));
        }
        countConstraints.add(cplex.addLe(constraint, airCrafts));
    }

    private void setCoverConstraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        double[][] cover = data.getCover();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        int strings = data.getStrings();
        int flightLegs = data.getFlightLegs();

        for (int i = 0; i < flightLegs; i++)
        {
            constraint = cplex.prod(cover[i][(int)stringsSortedAsHats[0][0]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][0]-1], x[(int)stringsSortedAsHats[0][0]-1]);
            for (int j = 1; j < strings; j++)
            {
                constraint = cplex.sum(
                        constraint, cplex.prod(cover[i][(int)stringsSortedAsHats[0][j]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][j]-1], x[(int)stringsSortedAsHats[0][j]-1]));
            }
            coverConstraintsEqual.add(cplex.addEq(constraint, 1));
        }
    }
    
    private void set233Constraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        IloNumExpr constraint2 = null;
        double[][] extremeValues = data.getExtremeValues();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        int strings = data.getStrings();

        constraint = 
                cplex.prod ((int) extremeValues[0][(int)stringsSortedAsHats[0][0]-1], x[(int)stringsSortedAsHats[0][0]-1]);
        
        constraint2 =
                cplex.prod ((int) - extremeValues[0][(int)stringsSortedAsHats[0][0]-1], v[(int)stringsSortedAsHats[0][0]-1]);
        for (int i = 0; i < strings; i++)
        {
            constraint = cplex.sum(constraint, cplex.prod ((int) extremeValues[0][(int)stringsSortedAsHats[0][i]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][i]-1], x[(int)stringsSortedAsHats[0][i]-1]));
            constraint2 = cplex.sum(constraint2, cplex.prod ((int) - extremeValues[0][(int)stringsSortedAsHats[0][i]-1] * stringsInSolution[0][(int)stringsSortedAsHats[0][i]-1], v[(int)stringsSortedAsHats[0][i]-1]));
        }
        constraint = cplex.sum(constraint, constraint2);
        constraints233.add(cplex.addLe(constraint, 90));
    }
    
    private void set234Constraint(IloCplex cplex, Delta_OBJ_EVData data)
            throws IloException
    {
        IloNumExpr constraint = null;
        int strings = data.getStrings();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        constraint = cplex.prod(1, DELTA[0]);
        for (int i = 0; i < strings; i++)
        {
            constraint = cplex.diff(constraint, v[(int)stringsSortedAsHats[0][i]-1]);
        }
        constraints234.add(cplex.addGe(constraint, 0));
    }
    
    private void set235Constraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        int strings = data.getStrings();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        double[][] extremeValues = data.getExtremeValues();
        for (int i = 0; i < strings; i++)
        {
            if (extremeValues[0][(int)stringsSortedAsHats[0][i]-1] != 0){
                constraint = cplex.diff( cplex.prod(stringsInSolution[0][(int)stringsSortedAsHats[0][i]-1], x[(int)stringsSortedAsHats[0][i]-1]), v[(int)stringsSortedAsHats[0][i]-1]);
                constraints235.add(cplex.addGe(constraint, 0));
            }
        }
    }
    
    private void set236Constraint(IloCplex cplex, Delta_OBJ_EVData data)
            throws IloException
    {
        IloNumExpr constraint = null;
        int strings = data.getStrings();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        double[][] extremeValues = data.getExtremeValues();
        for (int i = 0; i < strings; i++)
        {
            if (extremeValues[0][(int)stringsSortedAsHats[0][i]-1] != 0){
                constraint = cplex.diff(w[(int)stringsSortedAsHats[0][i]-1], v[(int)stringsSortedAsHats[0][i]-1]);
                constraints236.add(cplex.addGe(constraint, 0));
            }
        }
    }
    
    private void set237Constraint(IloCplex cplex, Delta_OBJ_EVData data, int[][] stringsInSolution)
            throws IloException
    {
        IloNumExpr constraint = null;
        int strings = data.getStrings();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        double[][] extremeValues = data.getExtremeValues();
        for (int i = 0; i < strings; i++)
        {
            if (extremeValues[0][(int)stringsSortedAsHats[0][i]-1] != 0){
                constraint = cplex.diff(cplex.prod(stringsInSolution[0][(int)stringsSortedAsHats[0][i]-1], x[(int)stringsSortedAsHats[0][i]-1]), v[(int)stringsSortedAsHats[0][i]-1]);
                constraint = cplex.sum(constraint, w[(int)stringsSortedAsHats[0][i]-1]);
                constraints237.add(cplex.addLe(constraint, 1));
            }
        }
    }
    
    private void set238Constraint(IloCplex cplex, Delta_OBJ_EVData data)
            throws IloException
    {
        IloNumExpr constraint = null;
        int strings = data.getStrings();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        double[][] extremeValues = data.getExtremeValues();
        int sHat = 0;
        for (int i = 0; i < strings; i++)
        {
            if (extremeValues[0][(int)stringsSortedAsHats[0][i]-1] != 0){
                sHat++;
            }
        }
        
        for (int i = strings - sHat + 1; i < strings - 1; i++)
        {
            constraint = cplex.diff(w[(int)stringsSortedAsHats[0][i]-1], w[(int)stringsSortedAsHats[0][i+1]-1]); 
            constraints238.add(cplex.addGe(constraint, 0));
        }
    }
    
    private void addnewcostraints(IloCplex cplex, Delta_OBJ_EVData data) throws IloException{
    	int strings = data.getStrings();
        double[][] stringsSortedAsHats = data.getStringsSortedAsHats();
        double[][] stringsInSolution = data.getStringsInSolution();
        for (int i = 0; i < strings; i++)
        {
            boundX.add(cplex.addLe(x[i], 1));
            
        }
        System.out.println("size of boundX: " + boundX.size());
        int [] route_days = new int [878207];
    	File file = new File ("B735_Condensed_Real_Strings_modified.dat");
        Scanner inputStreamroute;
		try {
			inputStreamroute = new Scanner(file).useDelimiter("\n|\\t");
			 System.out.println(inputStreamroute.next());
				String titleline = inputStreamroute.next();
				titleline = inputStreamroute.next();
				titleline = inputStreamroute.next();
				titleline = inputStreamroute.next();
				titleline = inputStreamroute.next();
				titleline = inputStreamroute.next();
				int days = 0;
				while (inputStreamroute.hasNext()) {
					System.out.println(inputStreamroute.next());
					String days_data = inputStreamroute.next();
					days_data = inputStreamroute.next();
					days_data = inputStreamroute.next();
					days_data = inputStreamroute.next();
					days_data = inputStreamroute.next();
					days_data = inputStreamroute.next();
					
					
					route_days[days] = Integer.parseInt(days_data);
					days_data = inputStreamroute.next();
					days_data = inputStreamroute.next();
					days++;
				}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        for (int i = 0; i < strings; i++)
        {
        	if (route_days[i] == 3){
        		cplex.addEq(x[i], 0);
        	}
        }
//        for (int i = 1; i < strings; i++)
//        {
//            if (stringsInSolution[0][i] == -1)
//                break;
//            if (stringsInSolution[0][i] - stringsInSolution[0][i - 1] > 1)
//            {
//                for (int j = (int) stringsInSolution[0][i - 1]; j < (int) stringsInSolution[0][i] - 1; j++)
//                {
//                    cplex.addEq(x[j], 0);
//                }
//            }
//        }
    }
    public IloNumVar[] getX()
    {
        return x;
    }
    public IloRange[] getCoverConstraints1()
    {
        IloRange[] temp = new IloRange [coverConstraints1.size()];
        for (int i=0; i< coverConstraints1.size();i++) {
            temp[i] = coverConstraints1.get(i);
        }
        return temp;
    }
    public IloRange[] getCoverConstraints2()
    {
        IloRange[] temp = new IloRange [coverConstraints2.size()];
        for (int i=0; i< coverConstraints2.size();i++) {
            temp[i] = coverConstraints2.get(i);
        }
        return temp;
    }
    public IloRange[] getFlightsInConstraints1()
    {
        IloRange[] temp = new IloRange [flightsInConstraints1.size()];
        for (int i=0; i< flightsInConstraints1.size();i++) {
            temp[i] = flightsInConstraints1.get(i);
        }
        return temp;
    }
    public IloRange[] getFlightsInConstraints2()
    {
        IloRange[] temp = new IloRange [flightsInConstraints2.size()];
        for (int i=0; i< flightsInConstraints2.size();i++) {
            temp[i] = flightsInConstraints2.get(i);
        }
        return temp;
    }
    public IloRange[] getFlightsOutConstraints1()
    {
        IloRange[] temp = new IloRange [flightsOutConstraints1.size()];
        for (int i=0; i< flightsOutConstraints1.size();i++) {
            temp[i] = flightsOutConstraints1.get(i);
        }
        return temp;
    }
    public IloRange[] getFlightsOutConstraints2()
    {
        IloRange[] temp = new IloRange [flightsOutConstraints2.size()];
        for (int i=0; i< flightsOutConstraints2.size();i++) {
            temp[i] = flightsOutConstraints2.get(i);
        }
        return temp;
    }
    public IloRange[] getCountConstraints()
    {
        IloRange[] temp = new IloRange [countConstraints.size()];
        for (int i=0; i< countConstraints.size();i++) {
            temp[i] = countConstraints.get(i);
        }
        return temp;
    }
    public IloRange[] getConstraints233()
    {
        IloRange[] temp = new IloRange [constraints233.size()];
        for (int i=0; i< constraints233.size();i++) {
            temp[i] = constraints233.get(i);
        }
        return temp;
    }
    public IloRange[] getConstraints234()
    {
        IloRange[] temp = new IloRange [constraints233.size()];
        for (int i=0; i< constraints234.size();i++) {
            temp[i] = constraints234.get(i);
        }
        return temp;
    }
    public IloRange[] getConstraints235()
    {
        IloRange[] temp = new IloRange [constraints235.size()];
        for (int i=0; i< constraints235.size();i++) {
            temp[i] = constraints235.get(i);
        }
        return temp;
    }
    public IloRange[] getConstraints237()
    {
        IloRange[] temp = new IloRange [constraints237.size()];
        for (int i=0; i< constraints237.size();i++) {
            temp[i] = constraints237.get(i);
        }
        return temp;
    }
    public IloRange[] getBoundX()
    {
        IloRange[] temp = new IloRange [boundX.size()];
        for (int i=0; i< boundX.size();i++) {
            temp[i] = boundX.get(i);
        }
        return temp;
    }

	@Override
	public void setConstraints(IloCplex cplex, RoutingData data)
			throws IloException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IloRange[] getCoverConstraintsEqual() {
		IloRange[] temp = new IloRange [coverConstraintsEqual.size()];
        for (int i=0; i< coverConstraintsEqual.size();i++) {
            temp[i] = coverConstraintsEqual.get(i);
        }
        return temp;
	}

	@Override
	public IloRange[] getFlightsInConstraintsEqual() {
		IloRange[] temp = new IloRange [flightsInConstraintsEqual.size()];
        for (int i=0; i< flightsInConstraintsEqual.size();i++) {
            temp[i] = flightsInConstraintsEqual.get(i);
        }
        return temp;
	}

	@Override
	public IloRange[] getFlightsOutConstraintsEqual() {
		IloRange[] temp = new IloRange [flightsOutConstraintsEqual.size()];
        for (int i=0; i< flightsOutConstraintsEqual.size();i++) {
            temp[i] = flightsOutConstraintsEqual.get(i);
        }
        return temp;
	}
}

