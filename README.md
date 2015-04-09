# RoutingModel2015
The project for my master thesis - Robust Aircraft Routing problem

To run this model, you need to do a few set-ups.

1. Use IBM ILOG CPLEX, write the CPLEX Java library path in VM arguments.

2. Get all the source files. The files are too large to upload here. Give larger Jav heap size.

3. Prepare to start running. Write "Model Mechanism" in Program arguments. Now Model can be "nominal", "CCP", "ECCP", "EV', "Delta_EV", "Delta_obj_EV". Mechanism can only be "00".

4. Have a look at the input file path anf output file path. Start running. Problems of this size take 10-30 min.

Enjoy!
