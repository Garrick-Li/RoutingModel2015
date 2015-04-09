package cmu.routing.model;

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import cmu.routing.model.RoutingData;

public interface RoutingEquations
{

    /**
     * 
     * Creates the objective function for the model
     * 
     * @param cplex
     *            The CPLEX object for this model
     * @param data
     *            Object holding the necessary model data
     * @throws IloException
     *             Thrown when any error during creation of objective function
     */
    public void setObjectiveFunction(IloCplex cplex, RoutingData data)
            throws IloException;

    /**
     * 
     * Adds the required constraint equations to the model.
     * 
     * @param cplex
     *            The CPLEX object for this model
     * @param data
     *            Object holding the necessary data
     * @throws IloException
     *             Thrown when any error during adding constraint equations
     */
    //public void setConstraints(IloCplex cplex, RoutingData data)
      //      throws IloException;
    
    public IloNumVar[] getX();

    public IloRange[] getCoverConstraints1();
    
    public IloRange[] getCoverConstraints2();

    public IloRange[] getFlightsInConstraints1();
    
    public IloRange[] getFlightsInConstraints2();
    
    public IloRange[] getFlightsOutConstraints1();
    
    public IloRange[] getFlightsOutConstraints2();

    public IloRange[] getCountConstraints();

    void setConstraints(IloCplex cplex, RoutingData data,
            int[][] stringsInSolution) throws IloException;

    public IloRange[] getBoundX();

	  void setConstraints(IloCplex cplex, RoutingData data) throws IloException;

  	public IloRange[] getCoverConstraintsEqual();

  	public IloRange[] getFlightsInConstraintsEqual();

  	public IloRange[] getFlightsOutConstraintsEqual();

  	public IloRange[] getConstraints233();

  	public IloRange[] getConstraints235();

  	public IloRange[] getConstraints237();


}
