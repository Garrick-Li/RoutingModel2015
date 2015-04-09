package cmu.routing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import cmu.routing.constants.RoutingFileConstants;
import cmu.routing.constants.ModelConstants.Model;
import cmu.routing.model.RoutingEquations;
import cmu.routing.model.ccp.CCPRoutingData;
import cmu.routing.model.ccp.CCPRoutingEquations;
import cmu.routing.model.delta_ev.Delta_EVData;
import cmu.routing.model.delta_ev.Delta_EVEquations;
import cmu.routing.model.delta_obj_ev.Delta_OBJ_EVData;
import cmu.routing.model.delta_obj_ev.Delta_OBJ_EVEquations;
import cmu.routing.model.eccp.ECCPRoutingData;
import cmu.routing.model.eccp.ECCPRoutingEquations;
import cmu.routing.model.ev.EVRoutingData;
import cmu.routing.model.ev.EVRoutingEquations;
//import cmu.routing.model.delta_ev.Delta_EVData;
//import cmu.routing.model.delta_ev.Delta_EVEquations;
//import cmu.routing.model.ev.EVData;
//import cmu.routing.model.ev.EVEquations;
import cmu.routing.model.nominal.NominalRoutingData;
import cmu.routing.model.nominal.NominalRoutingEquations;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * 
 * This is the main class for optimizing the propagation delay of aircrafts
 * 
 */
public class RouteOptimizer
{
    public int [][] stringsConsidered = new int [1][878207];
    public int [][] stringsInLP = new int [1][878207];
    public int stop = 0;
    public int variablesConsidered = 0;
    public double [] optimalValue = new double [200];
    
    public static void main(String[] args) throws Exception
    {
        RouteOptimizer opt = new RouteOptimizer();

        if (args.length != 2)
        {
            opt.printUsageString();
            System.exit(0);
        }
        try
        {
                opt.optimize(args[0], args[1]);
        } catch (IloException ioe)
        {
            System.out.println("Exception stack :-");
            ioe.printStackTrace();
        }
    }

    /**
     * 
     * Optimizes the routing plan based on the model and passed data. It
     * supports a limited set of models. The supported model name and the
     * mechanism number are required to provide the optimized delay
     * 
     * @param modelName
     *            Name of the supported model
     * @param mechanism
     *            Mechanism Number
     * @throws IloException
     *             Thrown when any error while optimizing the model
     * @throws IOException
     *             Thrown when any error while reading input file
     */
    public void optimize(String modelName, String mechanism) throws IloException, IOException
    {
        try{
            IloCplex cplex = new IloCplex();
            Model model = null;
            RoutingEquations eqns = null;
            boolean solveFlag = true;
            String baseInputFile1 = RoutingFileConstants.BASE_INPUT_FILE1;
            String baseInputFile2 = RoutingFileConstants.BASE_INPUT_FILE2;
            String baseInputFile3 = RoutingFileConstants.BASE_INPUT_FILE3;
            String incDataFile = "source/incremental_file_160_Gp1.txt";
            String hatDataFile = "source/hatfile_200_testGp1_15min.txt";
            
            try
            {
                model = Model.valueOf(modelName.toUpperCase());
            } catch (Exception e)
            {
                throw new IloException("The model \"" + modelName
                        + "\" is not supported");
            }
            
            
            
    
            switch (model)
            {
            case NOMINAL:
                NominalRoutingData nomData = new NominalRoutingData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
                eqns = new NominalRoutingEquations(nomData);
                eqns.setObjectiveFunction(cplex, nomData);
                eqns.setConstraints(cplex, nomData);
                if (solveFlag)
                {
                    //cplex.solve();
                    if (cplex.solve())
                    {
                        System.out.println("The optimal solution value is : "
                                + cplex.getObjValue());
                        
                        double[] xval = cplex.getValues(eqns.getX());
                        BufferedWriter out = new BufferedWriter(new FileWriter("soln_nominal_Gp5.txt",true));
                        System.out.println("IloNumExpr constraint6 = null;");
                        int num_str = 0;
                        for (int i = 0; i < 878207; i++) {
                            if (1 - xval[i] < 0.0001){
                                num_str++;
                                System.out.println("constraint6 = cplex.sum(constraint6, cplex.prod(1, x[" + String.valueOf(i) + "]));");
                            }
                        out.write(String.valueOf(xval[i]));
                        out.write(",");
                        }
                        System.out.println("cplex.addLe(constraint6, " + String.valueOf(num_str-1) + ");");
                        out.close();
                    } else
                    {
                        System.out.println("Error in solving the LP model");
                    }
                }
                break;
            case CCP:
            	CCPRoutingData ccpData = new CCPRoutingData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
                eqns = new CCPRoutingEquations(ccpData);
                eqns.setObjectiveFunction(cplex, ccpData);
                eqns.setConstraints(cplex, ccpData);
                if (solveFlag)
                {
                    //cplex.solve();
                    if (cplex.solve())
                    {
                        System.out.println("The optimal solution value is : "
                                + cplex.getObjValue());
                        
                        double[] xval = cplex.getValues(eqns.getX());
                        BufferedWriter out = new BufferedWriter(new FileWriter("soln_ccp_Gp5_15min_0.52.txt",true));
                        System.out.println("IloNumExpr constraint6 = null;");
                        int num_str = 0;
                        for (int i = 0; i < 878207; i++) {
                            if (1 - xval[i] < 0.0001){
                                num_str++;
                                System.out.println("constraint6 = cplex.sum(constraint6, cplex.prod(1, x[" + String.valueOf(i) + "]));");
                            }
                        out.write(String.valueOf(xval[i]));
                        out.write(",");
                        }
                        System.out.println("cplex.addLe(constraint6, " + String.valueOf(num_str-1) + ");");
                        out.close();
                    } else
                    {
                        System.out.println("Error in solving the LP model");
                    }
                }
                break;
            case ECCP:
            	ECCPRoutingData eccpData = new ECCPRoutingData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
                eqns = new ECCPRoutingEquations(eccpData);
                eqns.setObjectiveFunction(cplex, eccpData);
                eqns.setConstraints(cplex, eccpData);
                if (solveFlag)
                {
                    //cplex.solve();
                    if (cplex.solve())
                    {
                        System.out.println("The optimal solution value is : "
                                + cplex.getObjValue());
                        
                        double[] xval = cplex.getValues(eqns.getX());
                        BufferedWriter out = new BufferedWriter(new FileWriter("soln_eccp_Gp1_15min.txt",true));
                        System.out.println("IloNumExpr constraint6 = null;");
                        int num_str = 0;
                        for (int i = 0; i < 878207; i++) {
                            if (1 - xval[i] < 0.0001){
                                num_str++;
                                System.out.println("constraint6 = cplex.sum(constraint6, cplex.prod(1, x[" + String.valueOf(i) + "]));");
                            }
                        out.write(String.valueOf(xval[i]));
                        out.write(",");
                        }
                        System.out.println("cplex.addLe(constraint6, " + String.valueOf(num_str-1) + ");");
                        out.close();
                    } else
                    {
                        System.out.println("Error in solving the LP model");
                    }
                }
                break;
                        		
            case DELTA_EV:
                Delta_EVData Delta_EVData = new Delta_EVData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
                eqns = new Delta_EVEquations(Delta_EVData);
                eqns.setObjectiveFunction(cplex, Delta_EVData);
                eqns.setConstraints(cplex, Delta_EVData);
                if (solveFlag)
                {
                    //cplex.solve();
                    if (cplex.solve())
                    {
                        System.out.println("The optimal solution value is : "
                                + cplex.getObjValue());
                        
                        double[] xval = cplex.getValues(eqns.getX());
                        BufferedWriter out = new BufferedWriter(new FileWriter("soln_delta_ev_trial_15min.txt",true));
                        System.out.println("IloNumExpr constraint6 = null;");
                        int num_str = 0;
                        for (int i = 0; i < 878207; i++) {
                            if (1 - xval[i] < 0.0001){
                                num_str++;
                                System.out.println("constraint6 = cplex.sum(constraint6, cplex.prod(1, x[" + String.valueOf(i) + "]));");
                            }
                        out.write(String.valueOf(xval[i]));
                        out.write(",");
                        }
                        System.out.println("cplex.addLe(constraint6, " + String.valueOf(num_str-1) + ");");
                        out.close();
                    } else
                    {
                        System.out.println("Error in solving the LP model");
                    }
                }
                break;
                
            case EV:
                EVRoutingData EVData = new EVRoutingData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
                eqns = new EVRoutingEquations(EVData);
                eqns.setObjectiveFunction(cplex, EVData);
                eqns.setConstraints(cplex, EVData);
                if (solveFlag)
                {
                    //cplex.solve();
                    if (cplex.solve())
                    {
                        System.out.println("The optimal solution value is : "
                                + cplex.getObjValue());
                        
                        double[] xval = cplex.getValues(eqns.getX());
                        BufferedWriter out = new BufferedWriter(new FileWriter("soln_ev_Gp5_3_15min.txt",true));
                        System.out.println("IloNumExpr constraint6 = null;");
                        int num_str = 0;
                        for (int i = 0; i < 878207; i++) {
                            if (1 - xval[i] < 0.0001){
                                num_str++;
                                System.out.println("constraint6 = cplex.sum(constraint6, cplex.prod(1, x[" + String.valueOf(i) + "]));");
                            }
                        out.write(String.valueOf(xval[i]));
                        out.write(",");
                        }
                        System.out.println("cplex.addLe(constraint6, " + String.valueOf(num_str-1) + ");");
                        out.close();
                    } else
                    {
                        System.out.println("Error in solving the LP model");
                    }
                }
                break;
                
            case DELTA_OBJ_EV:
            	Delta_OBJ_EVData Delta_OBJ_EVData = new Delta_OBJ_EVData(baseInputFile1, baseInputFile2, baseInputFile3, incDataFile, hatDataFile, mechanism);
                eqns = new Delta_OBJ_EVEquations(Delta_OBJ_EVData);
                eqns.setConstraints(cplex, Delta_OBJ_EVData, stringsConsidered);
                if (solveFlag)
                {
                    //cplex.solve();
                    if (cplex.solve())
                    {
                        System.out.println("The optimal solution value is : "
                                + cplex.getObjValue());
                        
                        double[] xval = cplex.getValues(eqns.getX());
                        BufferedWriter out = new BufferedWriter(new FileWriter("soln_delta_obj_ev_Gp5_15min.txt",true));
                        System.out.println("IloNumExpr constraint6 = null;");
                        int num_str = 0;
                        for (int i = 0; i < 878207; i++) {
                            if (1 - xval[i] < 0.0001){
                                num_str++;
                                System.out.println("constraint6 = cplex.sum(constraint6, cplex.prod(1, x[" + String.valueOf(i) + "]));");
                            }
                        out.write(String.valueOf(xval[i]));
                        out.write(",");
                        }
                        System.out.println("cplex.addLe(constraint6, " + String.valueOf(num_str-1) + ");");
                        out.close();
                    } else
                    {
                        System.out.println("Error in solving the LP model");
                    }
                }
                break;
                        		
                        default:
                            printUsageString();
                            solveFlag = false;
                            break;
                        }
                    }
                
                    catch (IloException ex)
                    {
                        System.out.println("Concert exception caught" + ex);
                    }
                }
                

                private void printUsageString()
                {
                    System.out
                            .println("Usage: RouteOptimizer <modelName> <mechanism>"
                                    + " \n\twhere\n\t\tmodelName: Name of the model. [nominal/ccp/bs/delta/delta_alt"
                                    + "/eccp/bs_mpd/ccp_mpd/delta_mpd/eccp_mpd/ccp_ma]"
                                    + "\n\t\tmechanism: Mechanism Number"
                                    + "\n\n\tEx. RouteOptimizer nominal 01");
                }
                
                public static void quicksort(double[] reducedCost, int[] index) {
                    quicksort(reducedCost, index, 0, index.length - 1);
                }

                // quicksort a[left] to a[right]
                public static void quicksort(double[] a, int[] index, int left, int right) {
                    if (right <= left) return;
                    int i = partition(a, index, left, right);
                    quicksort(a, index, left, i-1);
                    quicksort(a, index, i+1, right);
                }

                // partition a[left] to a[right], assumes left < right
                private static int partition(double[] a, int[] index, 
                int left, int right) {
                    int i = left - 1;
                    int j = right;
                    while (true) {
                        while (less(a[++i], a[right]))      // find item on left to swap
                            ;                               // a[right] acts as sentinel
                        while (less(a[right], a[--j]))      // find item on right to swap
                            if (j == left) break;           // don't go out-of-bounds
                        if (i >= j) break;                  // check if pointers cross
                        exch(a, index, i, j);               // swap two elements into place
                    }
                    exch(a, index, i, right);               // swap with partition element
                    return i;
                }

                // is x < y ?
                private static boolean less(double x, double y) {
                    return (x < y);
                }

                // exchange a[i] and a[j]
                private static void exch(double[] a, int[] index, int i, int j) {
                    double swap = a[i];
                    a[i] = a[j];
                    a[j] = swap;
                    int b = index[i];
                    index[i] = index[j];
                    index[j] = b;
                }

            }
