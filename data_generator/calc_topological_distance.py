import random
import multiprocessing
import networkx as nx
import numpy.linalg
import ripser as ripser
import numpy as np
import matplotlib.pyplot as plt
import math
import sys
import os
import argparse

hera_bottleneck  = "/Users/prosen/tda/hera/bottleneck_dist"
hera_wasserstein = "/Users/prosen/tda/hera/wasserstein_dist"


parser = argparse.ArgumentParser(description='Calculate Graph Persistence Diagrams Based on Various Distance Measures.')
parser.add_argument( '-d', '--directory', metavar='[dir]', nargs=1, required=True, help='directory containing persistent diagrams (dgm format)' )

args = parser.parse_args()

rel_error = 0.01


files = []

dirpath = args.directory[0]


for X in os.listdir( args.directory[0] ):
    input_file = args.directory[0] + "/" + X
    files.append( input_file )
        
files_biharm = []
files_comm   = []
files_spath  = []
files_diff   = []

for input_file in files :
    base_filename, base_extension = os.path.splitext(input_file)
    comps = base_filename.split("_")

    if( base_extension.lower() == ".dgm" and comps[-1].lower() == "biharm" ):
        files_biharm.append( input_file )
    if( base_extension.lower() == ".dgm" and comps[-1].lower() == "spath" ):
        files_spath.append( input_file )
    if( base_extension.lower() == ".dgm" and comps[-1].lower() == "comm" ):
        files_comm.append( input_file )
    if( base_extension.lower() == ".dgm" and comps[-2].lower() == "diff" ):
        files_diff.append( input_file )

files_biharm.sort();
files_spath.sort();
files_comm.sort();
files_diff.sort();


def filter_inf_persistence_diagram( infile, outfile ):
    fout = open( outfile, "w" )
    fin  = open( infile,  "r" )
    
    cc = 0
    for line in fin.readlines():
        record = line.split( )
        if math.isinf( float(record[1]) ):
            cc += 1
        else:
            fout.write( record[0] + " " + record[1] + "\n" )
            
    fin.close()
    fout.close()

    return cc


def filter_inf_pd_files( file_list, cc_outfile=None ) :
    tmp_file_list = []
    cc = []
    for f in file_list :
        pi = os.path.abspath( f )
        ptmp = pi[0:-3] + "tmp"
        cc.append( filter_inf_persistence_diagram( pi, ptmp ) )
        tmp_file_list.append( ptmp )
        
    if cc_outfile != None :
        fout = open( cc_outfile, "w" )
        for c in cc :
            fout.write( str(c) + "\n" )
        fout.close()
    
    return tmp_file_list
        


def wasserstein_distance_matrix( outfile, tmp_file_list ) :

    ret = np.zeros( (len(tmp_file_list), len(tmp_file_list)) )
        
    for i in range( 0, len(tmp_file_list) ):
        pi = tmp_file_list[i]
        #print( tmp_file_list[i] )
        for j in range( i+1, len(tmp_file_list) ):
            pj = tmp_file_list[j]
            stream = os.popen( hera_wasserstein + " " + pi + " " + pj + " " + str(rel_error) )
            output = stream.read()
            ret[i][j] = ret[j][i] = float(output)
            #print( pi + " " + pj + " " + str( float(output) ) )

    np.savetxt( outfile, ret, delimiter=", " )


def bottleneck_distance_matrix( outfile, tmp_file_list ) :

    ret = np.zeros( (len(tmp_file_list), len(tmp_file_list)) )
        
    for i in range( 0, len(tmp_file_list) ):
        pi = tmp_file_list[i]
        #print( tmp_file_list[i] )
        for j in range( i+1, len(tmp_file_list) ):
            pj = tmp_file_list[j]
            stream = os.popen( hera_bottleneck + " " + pi + " " + pj + " " + str(rel_error) )
            output = stream.read()
            ret[i][j] = ret[j][i] = float(output)
            #print( pi + " " + pj + " " + str( float(output) ) )

    np.savetxt( outfile, ret, delimiter=", " )






if __name__ == "__main__":


    tmp_files_biharm = filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_biharm.txt" )
    tmp_files_comm   = filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_comm.txt" )
    tmp_files_spath  = filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_spath.txt" )
    tmp_files_diff   = filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_diff.txt" )

    jobs = []

    if not os.path.exists( dirpath + "/wass_biharm.txt" ) :
        jobs.append( multiprocessing.Process( target=wasserstein_distance_matrix, args=( dirpath + "/wass_biharm.txt", tmp_files_biharm ) ) )
    
    if not os.path.exists( dirpath + "/wass_spath.txt" ) :
        jobs.append( multiprocessing.Process( target=wasserstein_distance_matrix, args=( dirpath + "/wass_spath.txt",  tmp_files_spath )  ) )
        
    if not os.path.exists( dirpath + "/wass_comm.txt" ) :
        jobs.append( multiprocessing.Process( target=wasserstein_distance_matrix, args=( dirpath + "/wass_comm.txt",   tmp_files_comm )   ) )
        
    if not os.path.exists( dirpath + "/wass_diff.txt" ) :
        jobs.append( multiprocessing.Process( target=wasserstein_distance_matrix, args=( dirpath + "/wass_diff.txt",   tmp_files_diff )   ) )
    
    
    if not os.path.exists( dirpath + "/bott_biharm.txt" ) :
        jobs.append( multiprocessing.Process( target=bottleneck_distance_matrix, args=( dirpath + "/bott_biharm.txt", tmp_files_biharm ) ) )
        
    if not os.path.exists( dirpath + "/bott_spath.txt" ) :
        jobs.append( multiprocessing.Process( target=bottleneck_distance_matrix, args=( dirpath + "/bott_spath.txt",  tmp_files_spath )  ) )
        
    if not os.path.exists( dirpath + "/bott_comm.txt" ) :
        jobs.append( multiprocessing.Process( target=bottleneck_distance_matrix, args=( dirpath + "/bott_comm.txt",   tmp_files_comm )   ) )
        
    if not os.path.exists( dirpath + "/bott_diff.txt" ) :
        jobs.append( multiprocessing.Process( target=bottleneck_distance_matrix, args=( dirpath + "/bott_diff.txt",   tmp_files_diff )   ) )


    # Start the processes
    for j in jobs:
        j.start()

    # Ensure all of the processes have finished
    for j in jobs:
        j.join()

