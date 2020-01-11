#!/bin/sh

splits=( "20" "40" "60" "80" "100" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )


for s in "${splits[@]}"
do
    for o in "${overlaps[@]}"
    do
        python subdivide_graph.py -s $s -o $o -i ../data/CollegeMsg.txt
    done
done




for d in ../data/*/; do
    python calc_persistence_diagram.py -d $d
    python calc_topological_distance.py -d $d
done
