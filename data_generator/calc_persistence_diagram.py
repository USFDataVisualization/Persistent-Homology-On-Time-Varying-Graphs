import networkx as nx
import numpy as np
import numpy.linalg
import os
import argparse
import mod_eigendistance as ed
import mod_graph_tda as tda

parser = argparse.ArgumentParser(description='Calculate Graph Persistence Diagrams Based on Various Distance Measures.')
parser.add_argument( '-d', '--directory', metavar='[dir]', nargs=1, required=True, help='directory containing graphs (edgelist format)' )

args = parser.parse_args()


approx_thrd = 0.001
diffusion_time = 0.1

files = []

for X in os.listdir( args.directory[0] ):
    input_file = args.directory[0] + "/" + X
    base_filename, base_extension = os.path.splitext(input_file)
    if( base_extension.lower() == ".edgelist" ):
        files.append( input_file )

files.sort();






for inFile in files:

    print( "Processing: " + inFile )

    base_filename, base_extension = os.path.splitext(inFile)

    sd_pd = {'pd0':[],'pd1':[]}
    bd_pd = {'pd0':[],'pd1':[]}
    cd_pd = {'pd0':[],'pd1':[]}
    dd_pd = {'pd0':[],'pd1':[]}
    
    G = nx.read_weighted_edgelist( inFile )
    
    computeSP = not os.path.exists( base_filename + "_spath.dgm" )
    computeBD = not os.path.exists( base_filename + "_biharm.dgm" )
    computeCD = not os.path.exists( base_filename + "_comm.dgm" )
    computeDD = not os.path.exists( base_filename + "_diff_0.1.dgm" )

    for c in nx.connected_components(G):

        sg = G.subgraph(c)
    
        if computeSP :
            sd = np.asarray( nx.floyd_warshall_numpy(sg) )
            tda.calculate_persistence_diagram( sd, sd_pd )
        
        if computeBD or computeCD or computeDD:
            L = nx.laplacian_matrix(sg)
            E = np.linalg.eigh(L.A)
        
            if computeBD :
                bd = ed.fast_eigendistance_distance( E[0], E[1], "BIHARMONIC", {'approx_thrd':approx_thrd} )
                #bd = eigendistance_distance( E[0], E[1], "BIHARMONIC" )
                tda.calculate_persistence_diagram( bd, bd_pd )
            
            if computeCD :
                cd = ed.fast_eigendistance_distance( E[0], E[1], "COMMUTE", {'approx_thrd':approx_thrd} )
                #cd = eigendistance_distance( E[0], E[1], "COMMUTE" )
                tda.calculate_persistence_diagram( cd, cd_pd )
        
            if computeDD :
                dd = ed.fast_eigendistance_distance( E[0], E[1], "DIFFUSION", {'time':diffusion_time,'approx_thrd':approx_thrd} )
                #dd = eigendistance_distance( E[0], E[1], "DIFFUSION",  {'time':diffusion_time} )
                tda.calculate_persistence_diagram( dd, dd_pd )
        

    if computeSP :
        tda.save_persistence_diagram( sd_pd, base_filename + "_spath.dgm" )
        
    if computeBD :
        tda.save_persistence_diagram( bd_pd, base_filename + "_biharm.dgm" )
        
    if computeCD :
        tda.save_persistence_diagram( cd_pd, base_filename + "_comm.dgm" )
        
    if computeDD :
        tda.save_persistence_diagram( dd_pd, base_filename + "_diff_0.1.dgm" )

