import ripser as ripser
import os
import math
import numpy as np




hera_bottleneck  = "/Users/prosen/tda/hera/bottleneck_dist"
hera_wasserstein = "/Users/prosen/tda/hera/wasserstein_dist"

rel_error = 0.01



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




