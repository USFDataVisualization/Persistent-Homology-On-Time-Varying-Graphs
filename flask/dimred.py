
from sklearn.manifold import MDS
from sklearn.manifold import Isomap
from sklearn.manifold import TSNE

import numpy as np
import simplejson as json


def load_dm( dir, file ):
    input_file = dir + "/" + file
    return np.loadtxt( input_file, delimiter="," )


def run_dm( embedding, data ):
    X_transformed = embedding.fit_transform( data )
    return json.dumps( X_transformed.tolist(), separators=(',', ':') )


def run_mds( dir, file, dim=2 ):
    data = load_dm( dir, file)
    embedding = MDS( n_components=dim, dissimilarity="precomputed" )
    return run_dm( embedding, data )


def run_isomap( dir, file, dim=2 ):
    data = load_dm( dir, file)
    embedding = Isomap( n_components=dim, n_neighbors=5, metric="precomputed" )
    return run_dm( embedding, data )


def run_tsne( dir, file, dim=2 ):
    data = load_dm( dir, file)
    embedding = TSNE( n_components=dim, metric="precomputed" )
    return run_dm( embedding, data )


