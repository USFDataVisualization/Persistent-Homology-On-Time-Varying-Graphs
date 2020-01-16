#!/bin/bash

export HERA_BOTTLENECK="/tda/hera/bottleneck_dist"
export HERA_WASSERSTEIN="/tda/hera/wasserstein_dist"


#splits=( "20" "40" "60" "80" "100" )
offset=( "86400" "604800" "2419200" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )


#for s in "${splits[@]}"
for f in "${offset[@]}"
do
    for o in "${overlaps[@]}"
    do
        #python subdivide_graph.py -s $s -o $o -i ../data/CollegeMsg.txt
        python3 subdivide_graph.py --overlap $o --offset $f -i ../data/CollegeMsg.txt
    done
done

offset=( "86400" "604800" "2419200" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )

for f in "${offset[@]}"
do
    for o in "${overlaps[@]}"
    do
        python3 subdivide_graph.py --overlap $o --offset $f -i ../data/email-Eu-core-temporal.txt
    done
done


offset=( "3600" "7200" )
overlaps=( "0.05" "0.10" "0.25" "0.50" "0.75" )

for f in "${offset[@]}"
do
    for o in "${overlaps[@]}"
    do
        python3 subdivide_graph.py --overlap $o --offset $f -i ../data/highschool_2011.txt
        python3 subdivide_graph.py --overlap $o --offset $f -i ../data/highschool_2012.txt
    done
done



splits=( "4" "8" "16" )
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
        rm -f $output"/"*.edgelist
        rm -f $output"/"*.txt
        
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
    
    rm -rf "../data/sg_infectious_contact_list/listcontacts_2009_"*"_"$o"/"

done
