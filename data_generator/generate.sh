#!/bin/sh

export HERA_BOTTLENECK="/Users/prosen/tda/hera/bottleneck_dist"
export HERA_WASSERSTEIN="/Users/prosen/tda/hera/wasserstein_dist"


#splits=( "20" "40" "60" "80" "100" )
offset=( "86400" "604800" "2419200" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )


#for s in "${splits[@]}"
for f in "${offset[@]}"
do
    for o in "${overlaps[@]}"
    do
        #python subdivide_graph.py -s $s -o $o -i ../data/CollegeMsg.txt
        python subdivide_graph.py --overlap $o --offset $f -i ../data/email-Eu-core-temporal.txt
    done
done

offset=( "86400" "604800" "2419200" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )

for f in "${offset[@]}"
do
    for o in "${overlaps[@]}"
    do
        python subdivide_graph.py --overlap $o --offset $f -i ../data/email-Eu-core-temporal.txt
    done
done


offset=( "3600" "7200" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )

for f in "${offset[@]}"
do
    for o in "${overlaps[@]}"
    do
        python subdivide_graph.py --overlap $o --offset $f -i ../data/highschool_2011.txt
        python subdivide_graph.py --overlap $o --offset $f -i ../data/highschool_2012.txt
    done
done




for d in ../data/*/; do
    python calc_persistence_diagram.py -d $d
    python calc_topological_distance.py -d $d
done
