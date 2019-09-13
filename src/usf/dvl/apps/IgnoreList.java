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
