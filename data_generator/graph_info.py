import os
import argparse
import datetime

parser = argparse.ArgumentParser(description='Time-varying Graph Subdivder.')
parser.add_argument( '-i', '--input_file', metavar='[FILE]', nargs=1, required=True, help='input file (time-varying network)' )

args = parser.parse_args()

input_file = args.input_file[0]


file1 = open(input_file,"r+")
wholefile = file1.readlines()
file1.close()

mintime = 0xffffffffffffffff
maxtime = 0

for line in wholefile:
    record = line.split( )
    mintime = min( mintime, int(record[2]) );
    maxtime = max( maxtime, int(record[2]) );

minLen  = 60
hourLen = minLen*60
dayLen  = hourLen*24

print( )
print( "Min: " + str(mintime) + " Max: " + str(maxtime) )
print()
print( "Start: " + str(datetime.datetime.fromtimestamp(mintime)) )
print( "       " + str(datetime.datetime.fromtimestamp(mintime - (mintime%minLen))) )
print( "       " + str(datetime.datetime.fromtimestamp(mintime - (mintime%hourLen))) )
print( "       " + str(datetime.datetime.fromtimestamp(mintime - (mintime%dayLen))) )
print( "End:   " + str(datetime.datetime.fromtimestamp(maxtime)) )
print()
span = maxtime-mintime;
spanSec  = span%minLen
spanMin  = (span-spanSec)%hourLen
spanHours = (span-spanMin-spanSec)%dayLen
spanDays = (span-spanHours-spanMin-spanSec)
print( "Span:   " + str(spanDays/dayLen) + " days, " + str(spanHours/hourLen) + " hours, " + str(spanMin/minLen) + " min, " + str(spanSec) + " sec " )
print()
print( "Minute: " + str(60) )
print( "Hour:   " + str(3600) )
print( "Day:    " + str(3600*24) )
print()

