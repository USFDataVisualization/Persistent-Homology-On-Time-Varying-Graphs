from flask import Flask
from flask import request
from flask import render_template
from flask import Flask, request, send_from_directory

import os
import networkx as nx

import dimred as DR

app = Flask( __name__, template_folder="template" )

def error( err ):
    print( err )

@app.route('/')
def hello_world():
    #return DR.run_mds("../data/CollegeMsg_20_0.05", "bott_biharm.txt" )
    #return 'Hello, World!'
    return render_template('main.html', error=error)


@app.route('/static/<path:path>')
def send_static(path):
    return send_from_directory('static', path)


@app.route('/graph', methods=['GET', 'POST'])
def getGraph():
    #print( request.args )

    dir = "../data/"
    file = ""

    if request.args.get('dataset') == 'college' :
        dir = dir + "CollegeMsg_"
        file = file + "CollegeMsg_split"
    
    if request.args.get('dataset') == 'eu-email' :
        dir = dir + "email-Eu-core-temporal_"
        file = file + "email-Eu-core-temporal_split"

    if request.args.get('dataset') == 'highschool2011' :
        dir = dir + "highschool_2011_"
        file = file + "highschool_2011_split"

    if request.args.get('dataset') == 'highschool2012' :
        dir = dir + "highschool_2012_"
        file = file + "highschool_2012_split"

        
    splits = int( request.args.get('splits') )
    overlap = float( request.args.get('overlap') )
    instance = int( request.args.get('instance') )
    
    
    format = "%01d"
    if splits >= 9999 :
        format = "%05d"
    elif splits >= 999 :
        format = "%04d"
    elif splits >= 99 :
        format = "%03d"
    elif splits >= 9 :
        format = "%02d"
        
    dir = dir + str( splits ) + "_" + str( overlap )
    file = file + (format % instance) + ".edgelist"
    
    #print( dir + "/" + file )
    
    if os.path.exists( dir + "/" + file ) :
        G = nx.read_weighted_edgelist( dir + "/" + file )
        return nx.node_link_data(G)
    
    return "{}";



@app.route('/dimred', methods=['GET', 'POST'])
def dimred():
    #print( request.args )

    dir = "../data/"
    
    if request.args.get('dataset') == 'college' :
        dir = dir + "CollegeMsg_"
        
    if request.args.get('dataset') == 'eu-email' :
        dir = dir + "email-Eu-core-temporal_"
        
    if request.args.get('dataset') == 'highschool2011' :
        dir = dir + "highschool_2011_"
        
    if request.args.get('dataset') == 'highschool2012' :
        dir = dir + "highschool_2012_"

    dir = dir + str( int( request.args.get('splits') ) ) + "_"
    dir = dir + str( float( request.args.get('overlap') ) )

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
