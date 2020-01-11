import os
import argparse
import datetime
import sys


parser = argparse.ArgumentParser(description='Time-varying Graph Subdivder.')
parser.add_argument( '-s', '--splits', metavar='[N]', type=int, nargs=1, required=True, help='number of subgraphs to create')
parser.add_argument( '-o', '--overlap', metavar='[%]', type=float, nargs=1, required=True, help='overlap between adjacent file (e.g., 0.1 = 10%% overlap)' )
parser.add_argument( '-i', '--input_file', metavar='[FILE]', nargs=1, required=True, help='input file (time-varying network)' )

args = parser.parse_args()

overlap = args.overlap[0]
splits = args.splits[0]
input_file = args.input_file[0]

format = "%01d"
if splits >= 9999 :
    format = "%05d"
elif splits >= 999 :
    format = "%04d"
elif splits >= 99 :
    format = "%03d"
elif splits >= 9 :
    format = "%02d"




def filter_file( wholefile, usestart, useend ) :
    ret = []
    for line in wholefile:
        record = line.split( )
        timestamp = int(record[2])
        if timestamp > usestart and timestamp < useend:
            ret.append( line )
    return ret


def get_hitlist( filtered_file ):
    hitlist = {}
    
    for line in filtered_file:
        record = line.split( )

        idx0 = int(record[0])
        idx1 = int(record[1])
    
        name = str( min(idx0,idx1) ) + " " + str( max(idx0,idx1) )
    
        if name in hitlist:
            hitlist[name] += 1
        else:
            hitlist[name] = 1
            
    return hitlist

def write_edgelist( hitlist, outfile ):
    print( outfile )
    f = open( outfile, "w" )
    for name in hitlist:
        f.write( name + " " + str( hitlist[name] ) + "\n" )
    f.close()

def write_split( filtered_file, outfile ):
    print( outfile )
    f = open( outfile, "w" )
    for line in filtered_file :
        record = line.split( )
        f.write( (record[0]) + " " + (record[1]) + " " + (record[2]) + "\n" )
    f.close()






base_path, base_extension = os.path.splitext(input_file)
output_dir = base_path + "_" + str(splits) + "_" + str(overlap)
base_filename = os.path.split(input_file)[1][0:-len(base_extension)]


if os.path.exists( output_dir ) :
    print( "Skipping (" + output_dir + "), output directory already exists" )
    sys.exit()
    
    
try:
    os.mkdir( output_dir )
except OSError as error:
    print(error)
    print( "Skipping (" + output_dir + "), output directory already exists" )
    sys.exit()


file1 = open(input_file,"r+")
wholefile = file1.readlines()
file1.close()

mintime = 0xffffffffffffffff
maxtime = 0

for line in wholefile:
    record = line.split( )
    mintime = min( mintime, int(record[2]) );
    maxtime = max( maxtime, int(record[2]) );


start = mintime
step  = (maxtime - mintime) / splits
overoff = step * overlap / 2

for i in range(1, splits+1):
    end = mintime + step * i
    usestart = start-overoff
    useend   = end+overoff
    
    print( "Processing file " + str(i) + ": " + str(int(usestart)) + " to " + str(int(useend)) )
    
    filtered = filter_file( wholefile, usestart, useend )
    write_split( filtered, output_dir + "/" + base_filename + "_split" + (format % i) + ".txt" )

    hitlist  = get_hitlist( filtered )
    write_edgelist( hitlist, output_dir + "/" + base_filename + "_split" + (format % i) + ".edgelist" )
    
    start = end

