import multiprocessing
import os
import argparse
import mod_graph_tda as tda




parser = argparse.ArgumentParser(description='Calculate Graph Persistence Diagrams Based on Various Distance Measures.')
parser.add_argument( '-d', '--directory', metavar='[dir]', nargs=1, required=True, help='directory containing persistent diagrams (dgm format)' )

args = parser.parse_args()



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





if __name__ == "__main__":


    tmp_files_biharm = tda.filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_biharm.txt" )
    tmp_files_comm   = tda.filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_comm.txt" )
    tmp_files_spath  = tda.filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_spath.txt" )
    tmp_files_diff   = tda.filter_inf_pd_files( files_biharm, cc_outfile=dirpath + "/cc_diff.txt" )

    jobs = []

    if not os.path.exists( dirpath + "/wass_biharm.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.wasserstein_distance_matrix, args=( dirpath + "/wass_biharm.txt", tmp_files_biharm ) ) )
    
    if not os.path.exists( dirpath + "/wass_spath.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.wasserstein_distance_matrix, args=( dirpath + "/wass_spath.txt",  tmp_files_spath )  ) )
        
    if not os.path.exists( dirpath + "/wass_comm.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.wasserstein_distance_matrix, args=( dirpath + "/wass_comm.txt",   tmp_files_comm )   ) )
        
    if not os.path.exists( dirpath + "/wass_diff.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.wasserstein_distance_matrix, args=( dirpath + "/wass_diff.txt",   tmp_files_diff )   ) )
    
    
    if not os.path.exists( dirpath + "/bott_biharm.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.bottleneck_distance_matrix, args=( dirpath + "/bott_biharm.txt", tmp_files_biharm ) ) )
        
    if not os.path.exists( dirpath + "/bott_spath.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.bottleneck_distance_matrix, args=( dirpath + "/bott_spath.txt",  tmp_files_spath )  ) )
        
    if not os.path.exists( dirpath + "/bott_comm.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.bottleneck_distance_matrix, args=( dirpath + "/bott_comm.txt",   tmp_files_comm )   ) )
        
    if not os.path.exists( dirpath + "/bott_diff.txt" ) :
        jobs.append( multiprocessing.Process( target=tda.bottleneck_distance_matrix, args=( dirpath + "/bott_diff.txt",   tmp_files_diff )   ) )


    # Start the processes
    for j in jobs:
        j.start()

    # Ensure all of the processes have finished
    for j in jobs:
        j.join()

