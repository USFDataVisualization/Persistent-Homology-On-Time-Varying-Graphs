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

package usf.dvl.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import usf.dvl.common.SystemX;

public class IgnoreList extends ArrayList<String> {
	
	private static final long serialVersionUID = -4737384734720788133L;

	File fpath;
	
	public IgnoreList( String path ) {
		fpath = new File( path );
		try {
			for( String s : SystemX.readFileContents(path) ) {
				add(s);
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		add( fpath.toString() );
	}
	
	public void save( ) {
		PrintWriter pw;
		try {
			pw = new PrintWriter( fpath );
			for( String s : this ) {
				pw.println(s);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
