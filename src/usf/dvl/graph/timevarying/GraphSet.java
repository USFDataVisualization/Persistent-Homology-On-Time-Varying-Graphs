//    Persistent Homology on Time Varying Graphs
//    Copyright (C) 2019  Paul Rosen
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

package usf.dvl.graph.timevarying;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import usf.dvl.apps.Configuration;
import usf.dvl.apps.IgnoreList;
import usf.dvl.common.DistanceMatrix;
import usf.dvl.common.SystemX;
import usf.dvl.graph.Graph;
import usf.dvl.graph.SimpleGraphLoader;
import usf.dvl.topology.distance.HeraTopologicalDistance;
import usf.dvl.topology.ph.impl.RipserDGM;
import usf.dvl.topology.ph.impl.RipserPH;

public class GraphSet {

	HashMap<File,Graph> graphs = new HashMap<File,Graph>();
	Hashtable<File,RipserDGM> dgm = new Hashtable<File,RipserDGM>();
	HashMap<File,String> heraDgm = new HashMap<File,String>();
	ArrayList<File> files = new ArrayList<File>();
	
	public DistanceMatrix [] dm = new DistanceMatrix[4];
	String  [] dmFile = new String[4];
	
	
	File graphDir;
	File shortestpathDir;

	RipserPH ripser;
	HeraTopologicalDistance hera;
	
	public GraphSet( Configuration config,  String dirName ){
		RipserPH.ripser_path = config.getRisper();

		HeraTopologicalDistance.bottleneck_dist_path = config.getHeraBottleneck();
		HeraTopologicalDistance.wasserstein_dist_path = config.getHeraWasserstein();

		try {
			ripser = new RipserPH();
			hera = new HeraTopologicalDistance();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		
		graphDir = new File(dirName); 
		
		IgnoreList ignore = new IgnoreList( graphDir.toString() + "/" + "ignore.txt" );
			Stream<Path> x = null;
			try {
				x = Files.list( graphDir.toPath() );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Iterator<Path> _x = x.iterator();
			while( _x.hasNext() ) {
				File f = (_x.next()).toFile();
				if( ignore.contains( f.toString() ) ) {
					System.out.println( "Info: Ignoring " + f.toString() );
					continue;
				}
				try {
					graphs.put( f, new SimpleGraphLoader(f.toString()) );
					files.add(f);
				} catch (IOException e) {
					ignore.add(f.toString());
					System.out.println( "Info: Skipping " + f.toString() );
				}
			}
			x.close();
			ignore.save();
		
		files.sort( new Comparator<File>() {
			@Override public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo( o2.getAbsolutePath() );
			}			
		});
		
		int i = 0;
		System.out.println();
		System.out.print(graphDir.toString());
		for( File f : files ) {
			if( i>0 ) System.out.print(" | ");
			if( i==0 || i%8 == 0 ) { System.out.println(); System.out.print("\t"); }
			System.out.print( f.toString().substring( f.getParent().length()+1 ) );
			i++;
		}
		System.out.println();
		System.out.println();
		
		shortestpathDir = new File( graphDir.toString() + "/shortestpath" );
		shortestpathDir.mkdir();
		
		
		dmFile[0] = shortestpathDir.toString() + "/" + "bottleneck0.txt";
		dmFile[1] = shortestpathDir.toString() + "/" + "bottleneck1.txt";
		dmFile[2] = shortestpathDir.toString() + "/" + "wasserstein0.txt";
		dmFile[3] = shortestpathDir.toString() + "/" + "wasserstein1.txt";
		
		for( int j = 0; j < 4; j++) {
			try {
				dm[j] = new DistanceMatrix( dmFile[j] );
			} catch (IOException e) {
				dm[j] = new DistanceMatrix( files.size() );
				dm[j].clear( Double.NaN );
			}
		}
		
	}
	
	
	public Graph getGraph( int idx ) {
		return graphs.get( files.get(idx) );
	}
	
	public RipserDGM getPD( int idx ) {
		return dgm.get( files.get(idx) );
	}
	
	public int size() { return files.size(); }
	
	public ExecutorService runPH( ) {
		
		ExecutorService es = Executors.newFixedThreadPool(6);
		for( File f : files ) {
			es.execute( new PHRunnable(f) );
		}
		es.shutdown();
		
		return es;
	}
	
	public ExecutorService runPDDistances() {
		ExecutorService es = Executors.newCachedThreadPool();
		GraphSet.DistanceRunnable [] dt = new GraphSet.DistanceRunnable[4];
		dt[0] = calculateBottleneckDistance(0);
		dt[1] = calculateBottleneckDistance(1);
		dt[2] = calculateWassersteinDistance(0);
		dt[3] = calculateWassersteinDistance(1);
		for(int i=0;i<4;i++) {
		    es.execute( dt[i] );
		}
		es.shutdown();	
		return es;
	}
	
	

	boolean quit = false;

	
	public void quit() {
		quit = true;
	}
	
	class PHRunnable implements Runnable {
		File f;
		public PHRunnable( File _f ) {
			f = _f;
		}
		
		@Override
		public void run() {
			if( quit ) return; 
			
			String baseFile = f.toString().substring( f.getParent().length()+1 );
			String dgmFile = shortestpathDir.toString() + "/" + baseFile + ".dgm";
			
			if( SystemX.fileExists(dgmFile) ) {
				System.out.println( "Skipping " + dgmFile + " (already cached)" );
			}
			else {
				System.out.println( "Calculating " + dgmFile + "" );
				ripser.cohomology( graphs.get( f ).shortestpath_distance(), dgmFile);
			}
			try {
				dgm.put( f, new RipserDGM(dgmFile) );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	class DistanceRunnable implements Runnable {
		int dim, dist;
		DistanceMatrix gdm = null;
		String gdmFile = null;
		boolean modified = false;
		
		DistanceRunnable( int dim, int dist ){
			this.dim = dim;
			this.dist = dist;
			gdm = dm[dist*2+dim];
			gdmFile = dmFile[dist*2+dim];
		}
		
		private void save( int curIdx ) {
			if( !modified ) return;
			System.out.println("Saving data " + (curIdx+1) + " of " + (files.size()*files.size()) + " (" + gdmFile + ")" );
			try {
				gdm.saveData(gdmFile);
				modified = false;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}			
		}
		
		@Override public void run() {

			
			int proc = 0;
			for(int i = 0; i < files.size(); i++) {
				File fi = files.get(i);
				gdm.set(i, i, 0);
				for( int j = i+1; j < files.size(); j++ ) {
					File fj = files.get(j);
					
					if( Double.isNaN( gdm.get(i, j)) ) {
						double d  = Double.NaN;
						if( dist == 0 ) d = hera.bottleneckDistance( dgm.get(fi).filterByDimension(dim), dgm.get(fj).filterByDimension(dim), 0.001f);
						if( dist == 1 ) d = hera.wassersteinDistance( dgm.get(fi).filterByDimension(dim), dgm.get(fj).filterByDimension(dim) );
						gdm.set( i, j, d );
						gdm.set( j, i, d );
						proc++;
						modified = true;
					}
					
					int curIdx = files.size()*i + j;
					if( quit || (proc%500) == 499 ) {
						save(curIdx);
					}
					if( quit ) return;
				}
			}	
			save( files.size() * files.size() - 1 );
		}
		
	}
	
	public DistanceRunnable calculateBottleneckDistance( int dim ) {
		return new DistanceRunnable(dim,0);
	}


	public DistanceRunnable calculateWassersteinDistance(int dim ) {
		return new DistanceRunnable(dim,1);
	}
}
