#!/bin/bash

export HERA_BOTTLENECK="/tda/hera/bottleneck_dist"
export HERA_WASSERSTEIN="/tda/hera/wasserstein_dist"



for d in ../data/*/; do
    python3 calc_persistence_diagram.py -d $d
    #python3 calc_topological_distance.py -d $d
done

for d in ../data/*/; do
    #python3 calc_persistence_diagram.py -d $d
    python3 calc_topological_distance.py -d $d
done
