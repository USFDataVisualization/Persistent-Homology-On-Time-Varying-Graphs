#!/bin/bash

export HERA_BOTTLENECK="/tda/hera/bottleneck_dist"
export HERA_WASSERSTEIN="/tda/hera/wasserstein_dist"


splits=( "1" "2" "4" "8" )
overlaps=( "0.05" "0.1" "0.25" "0.5" "0.75" )


for o in "${overlaps[@]}"
do
    for s in "${splits[@]}"
    do
        for f in ../data/sg_infectious_contact_list/listcontacts_2009_*.txt
        do
            echo "Processing $f file..."
            python3 subdivide_graph.py --overlap $o --split $s -i $f
        done
        
        output="../data/sg_infectious_"$s"_"$o
        mkdir $output
        rm $output"/"*.edgelist
        rm $output"/"*.txt
        
        mv "../data/sg_infectious_contact_list/listcontacts_2009_"*"_"$s"_"$o"/"* $output"/"
        
        
        ls $output"/"*".edgelist" | sort | cat -n | while read n f; do
            foo=$(printf "%04d" $n)
            newfile=$output"/sg_infectious_split"$foo".edgelist"
            mv $f $newfile
        done
        
        ls $output"/"*".txt" | sort | cat -n | while read n f; do
            foo=$(printf "%04d" $n)
            newfile=$output"/sg_infectious_split"$foo".txt"
            mv $f $newfile
        done

    done
    
    rm -r "../data/sg_infectious_contact_list/listcontacts_2009_"*"_"$o"/"

done



for d in ../data/sg_infectious_*/; do
    python3 calc_persistence_diagram.py -d $d
done

for d in ../data/sg_infectious_*/; do
    python3 calc_topological_distance.py -d $d
done




