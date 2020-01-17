from flask import Flask
from flask import request
from flask import render_template
from flask import send_from_directory
from flask import send_file

import os
import networkx as nx
import json

import dimred as DR

app = Flask( __name__ )
datasets = []

with open('static/datasets.json') as json_file:
    datasets = json.load(json_file)


def error( err ):
    print( err )

@app.route('/')
def render_index():
    return send_file('pages/main.html')


@app.route('/static/<path:path>')
def send_static(path):
    return send_from_directory('static', path)


@app.errorhandler(404)
def page_not_found(error):
    return 'This page does not exist', 404

@app.route('/graph', methods=['GET', 'POST'])
def getGraph():

    splits = int( request.args.get('splits') )
    overlap = float( request.args.get('overlap') )
    instance = int( request.args.get('instance') ) + 1
    


    dir  = ""
    file = ""

    for ds in datasets :
        if request.args.get('dataset') == ds['value'] :
        
            format = "%01d"
            if splits >= 9999 :
                format = "%05d"
            elif splits >= 999 :
                format = "%04d"
            elif splits >= 99 :
                format = "%03d"
            elif splits >= 9 :
                format = "%02d"

            if 'format_override' in ds :
                format = ds['format_override']
            
            dir  = "../data/" + ds['dir'] + str( splits ) + "_" + str( overlap )
            file = ds['file'] + (format % instance) + ".edgelist"


    if os.path.exists( dir + "/" + file ) :
        G = nx.read_weighted_edgelist( dir + "/" + file )
        return nx.node_link_data(G)
    
    return "{}";



@app.route('/dimred', methods=['GET', 'POST'])
def dimred():
    #print( request.args )

    dir = ""
    
    splits = int( request.args.get('splits') )
    overlap = float( request.args.get('overlap') )

    for ds in datasets :
        if request.args.get('dataset') == ds['value'] :
            dir  = "../data/" + ds['dir'] + str( splits ) + "_" + str( overlap )


    filename = ""
    if request.args.get('topo_dist') == 'bottleneck' :
        filename = filename + "bott_"
    if request.args.get('topo_dist') == 'wasserstein' :
        filename = filename + "wass_"

    if request.args.get('graph_dist') == 'shortest' :
        filename = filename + "spath.txt"
    if request.args.get('graph_dist') == 'biharmonic' :
        filename = filename + "biharm.txt"
    if request.args.get('graph_dist') == 'diffusion' :
        filename = filename + "diff.txt"
    if request.args.get('graph_dist') == 'commute' :
        filename = filename + "comm.txt"

    #print( dir + "/" + filename )
    
    if request.args.get('method') == 'mds' :
        return DR.run_mds(dir, filename, dim=int(request.args.get('dim')) )
    if request.args.get('method') == 'isomap' :
        return DR.run_isomap(dir, filename, dim=int(request.args.get('dim')) )
    if request.args.get('method') == 'tsne' :
        return DR.run_tsne(dir, filename, dim=int(request.args.get('dim')) )
    return 'No method selected'
    #if request.method == 'POST':




#parser = argparse.ArgumentParser(description='Calculate Graph Persistence Diagrams Based on Various Distance Measures.')
#parser.add_argument( '-d', '--directory', metavar='[dir]', nargs=1, required=True, help='directory containing data to reduce' )

#args = parser.parse_args()

#rel_error = 0.01

#files = [ "bott_biharm.txt" ]
#,
#          "bott_comm.txt",
#          "bott_diff.txt",
#          "bott_spath.txt",
#          "bott_biharm.txt",
#          "bott_comm.txt",
#          "bott_diff.txt",
#          "bott_spath.txt"   ]
