import networkx as nx
import matplotlib.pyplot as plt
import os
import sys


args = len(sys.argv) - 1

if args < 1:
    print()
    print( "Draw Edgelist Graph and Connected Component Subgraphs" )
    print()
    print( "   python " + sys.argv[0] + " <input_file_0> ... <input_file_n>")
    print("        input_file_X -- input files (edgelist format)")
    print()
    exit();




def save_graph( input_file ):

    base_filename, base_extension = os.path.splitext(input_file)

    G=nx.read_weighted_edgelist(input_file)

    nx.draw( G, pos=nx.spring_layout(G) )
    plt.axis('off')
    plt.savefig(base_filename + "_graph.png")
    plt.clf()


    idx = 0

    for c in nx.connected_components(G):
        sg = G.subgraph(c)
        
        nx.draw( sg, pos=nx.spring_layout(sg) )
        plt.savefig(base_filename + "_subgraph" + str(idx) + ".png")
        plt.clf()

        idx = idx+1





for j in range( 1, args+1 ):
    print( sys.argv[j] )
    save_graph( sys.argv[j] )


