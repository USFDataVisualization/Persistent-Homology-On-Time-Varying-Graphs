# Persistent-Homology-On-Time-Varying-Graphs

Software for the paper:

    Visual Detection of Structural Changes in Time-Varying Graphs Using Persistent Homology
    
    M Hajij, B Wang, C Scheidegger, P Rosen
    
    IEEE Pacific Visualization Symposium (PacificVis), 125-134

Limitations

- Current version only supports shortestpath distance and limited visualization capabilities.

Prerequisites 

- Ripser --- https://github.com/Ripser
  
- Hera --- https://bitbucket.org/grey_narn/hera/src/master/
  
Basic Usage:

- Start the software
  
    `> java -jar PHTimeVaryingGraphs.jar`
    
- When running, you will be asked for the path to the ripser executable and 2 hera executables.
  
- If you ever need to reconfig paths, just add "--config" as an argument:
  
    `> java -jar PHTimeVaryingGraphs.jar --config`
    
Data:
  
- We've included sample data. Simply point to the directory containing that data.
    
- If you want to use your own data, you must manually slice it and put it in the correct format. See 
    our paper for more information about slicing. Then, provide the path to the directory containing the 
    files, and processing will begin.
    
Running:
  
- If you'd like to stop processing type 'exit' at any time. You can resume the job by just restarting the application.

Trouble or questions, contact prosen AT usf DOT edu
