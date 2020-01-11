import networkx as nx
import numpy.linalg
import ripser as ripser
import numpy as np
import matplotlib.pyplot as plt
import math
import sys
import os
import argparse
import eigendistance as ed


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





def calculate_persistence_diagram( dist_matrix, pd_out ):
    R = ripser.ripser( dist_matrix, distance_matrix=True )

    for x in R['dgms'][0]:
        pd_out['pd0'].append( x )
        
    for x in R['dgms'][1]:
        pd_out['pd1'].append( x )


def save_persistence_diagram( pd, outfile ):
    f = open( outfile, "w" )

    for x in pd['pd0']:
        #f.write( "0 " + str(x[0]) + " " + str(x[1]) + "\n" )
        f.write( str(x[0]) + " " + str(x[1]) + "\n" )

    for x in pd['pd1']:
        #f.write( "1 " + str(x[0]) + " " + str(x[1]) + "\n" )
        f.write( str(x[0]) + " " + str(x[1]) + "\n" )

    f.close()




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
            sd = numpy.asarray( nx.floyd_warshall_numpy(sg) )
            calculate_persistence_diagram( sd, sd_pd )
        
        if computeBD or computeCD or computeDD:
            L = nx.laplacian_matrix(sg)
            E = np.linalg.eigh(L.A)
        
            if computeBD :
                bd = ed.fast_eigendistance_distance( E[0], E[1], "BIHARMONIC", {'approx_thrd':approx_thrd} )
                #bd = eigendistance_distance( E[0], E[1], "BIHARMONIC" )
                calculate_persistence_diagram( bd, bd_pd )
            
            if computeCD :
                cd = ed.fast_eigendistance_distance( E[0], E[1], "COMMUTE", {'approx_thrd':approx_thrd} )
                #cd = eigendistance_distance( E[0], E[1], "COMMUTE" )
                calculate_persistence_diagram( cd, cd_pd )
        
            if computeDD :
                dd = ed.fast_eigendistance_distance( E[0], E[1], "DIFFUSION", {'time':diffusion_time,'approx_thrd':approx_thrd} )
                #dd = eigendistance_distance( E[0], E[1], "DIFFUSION",  {'time':diffusion_time} )
                calculate_persistence_diagram( dd, dd_pd )
        

    if computeSP :
        save_persistence_diagram( sd_pd, base_filename + "_spath.dgm" )
        
    if computeBD :
        save_persistence_diagram( bd_pd, base_filename + "_biharm.dgm" )
        
    if computeCD :
        save_persistence_diagram( cd_pd, base_filename + "_comm.dgm" )
        
    if computeDD :
        save_persistence_diagram( dd_pd, base_filename + "_diff_0.1.dgm" )

