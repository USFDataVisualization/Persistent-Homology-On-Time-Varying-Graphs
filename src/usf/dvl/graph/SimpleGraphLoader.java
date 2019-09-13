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

package usf.dvl.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class SimpleGraphLoader extends Graph {

	HashMap< Integer, GraphVertex > verts = new HashMap<Integer,GraphVertex>();
	
	GraphVertex findVertex( int id ) {
		if( !verts.containsKey(id) ) { 
			verts.put( id, this.addVertex( ) );
		}
		return verts.get(id);
	}
	
	public SimpleGraphLoader( String filename ) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader( filename ));

		String line;
		 while( ( line = reader.readLine() ) != null ) {
			 String [] parts = line.split("\\s+");
			 if( parts.length == 1 ) {
				 findVertex( Integer.parseInt(parts[0]) );
			 }
			 else if( parts.length == 3 ) {
				 GraphEdge e = new GraphEdge();
				 e.v0 = findVertex( Integer.parseInt(parts[0]) );
				 e.v1 = findVertex( Integer.parseInt(parts[1]) );
				 e.w  = Double.parseDouble( parts[2] );
				 this.addEdge(e);
			 }
			 else {
				 System.out.println("Warning: I don't know what to do with this line (" + filename + ")");
				 System.out.println("  --> " + line );
				 reader.close();
				 throw new IOException();
			 }
	     }
		 reader.close();

	}
}
