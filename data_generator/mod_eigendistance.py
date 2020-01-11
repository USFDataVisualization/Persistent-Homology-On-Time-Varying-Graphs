import numpy.linalg
import numpy as np
import math




def eigendistance_distance( eigenvalues, eigenvectors, type, args={} ):
    ret = numpy.zeros( shape=L.shape )
    
    dist_func = pairwise_commute_distance;
    
    if( type.upper() == 'BIHARMONIC' ):
        dist_func = pairwise_biharmonic_distance;
    elif( type.upper() == 'COMMUTE' ):
        dist_func = pairwise_commute_distance;
    elif( type.upper() == 'DIFFUSION' ):
        dist_func = pairwise_diffusion_distance;
    else:
        print("Warning, invalid distance type (" + type + "), defaulting to commute")
        
    for i in range( 0, L.shape[0] ):
        for j in range( i+1, L.shape[0] ):
            d2 = dist_func( i, j, eigenvalues, eigenvectors, args )
            ret[i][j] = ret[j][i] = math.sqrt( d2 )
    return ret


def pairwise_biharmonic_distance( ind1, ind2, eigenvalues, eigenvectors, args ):
    d = 0.0
    for v in range( 1, len(eigenvalues) ):
        dv = eigenvectors[v][ind1]-eigenvectors[v][ind2]
        ev = eigenvalues[v]
        d  = d + (dv*dv) / (ev*ev)
    return d


def pairwise_commute_distance( ind1, ind2, eigenvalues, eigenvectors, args ):
    d = 0.0
    for v in range( 1, len(eigenvalues) ):
        dv = eigenvectors[v][ind1]-eigenvectors[v][ind2]
        ev = eigenvalues[v]
        d  = d + (dv*dv) / (ev)
    return d


def pairwise_diffusion_distance( ind1, ind2, eigenvalues, eigenvectors, args ):
    d = 0.0
    for v in range( 1, len(eigenvalues) ):
        dv = eigenvectors[v][ind1]-eigenvectors[v][ind2]
        ev = eigenvalues[v]
        d  = d + (dv*dv) * math.exp( -2 * args['time'] * ev )
    return d



def fast_eigendistance_distance( eigenvalues, eigenvectors, type='BIHARMONIC', args={} ):
    ret = numpy.zeros( shape=eigenvectors.shape )
    
    if( len(eigenvalues) == 1 ):
        return ret
    
    max_ev = eigenvalues.shape[0]
    
    if( 'approx_thrd' in args ):
        base = 1/eigenvalues[1]
        for i in range( 1, eigenvalues.shape[0] ):
            curr = 1/eigenvalues[i]
            if( curr/base >= args['approx_thrd'] ):
                max_ev = i+1
            #print( str(i) + "  " + str(curr) + " ==> " + str(curr/base)  )
        #print( str(max_ev) + " " + str(L.shape[0]) )
        #print()
    
    eval = eigenvalues[1:max_ev]
    evec = eigenvectors[1:max_ev,:]
    
    if( type.upper() == 'BIHARMONIC' ):
        eval = numpy.reciprocal( np.multiply( eval, eval ) )
    elif( type.upper() == 'COMMUTE' ):
        eval = numpy.reciprocal( eval )
    elif( type.upper() == 'DIFFUSION' ):
        tmp = np.multiply( eval, -2 * args['time'] )
        eval = np.exp( tmp )
    else:
        print("Warning, invalid distance type (" + type + "), defaulting to commute")
    
    
    for ind1 in range( 0, eigenvalues.shape[0] ):
        for ind2 in range( ind1+1, eigenvalues.shape[0] ):
            tmp = np.subtract( evec[:,ind1], evec[:,ind2] )
            tmp = np.multiply( tmp, tmp )
            d2 = numpy.dot( tmp, eval )
            ret[ind1][ind2] = ret[ind2][ind1] = math.sqrt( d2 )
            
    return ret

