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

package usf.dvl.graph.timevarying.visualization;

import processing.core.PApplet;
import processing.core.PConstants;
import usf.dvl.common.DistanceMatrix;
import usf.dvl.draw.DPositionSet1D;
import usf.dvl.graph.timevarying.GraphSet;

public class ViewDistanceMatrix extends ViewLineCharts {



	public ViewDistanceMatrix( PApplet p ){
		super(p, "Distance Matrix View (drag red and blue to change current timestep)");
	}


	public void setData( GraphSet _grphs ){
		if( grphs == _grphs ) return;
		super.setData(_grphs);

		line0.setData( new RedP1D( grphs.dm[0]) );
		line1.setData( new RedP1D( grphs.dm[1]) );
		line2.setData( new RedP1D( grphs.dm[2]) );
		line3.setData( new RedP1D( grphs.dm[3]) );	    
	}

	
	public void draw() {
		super.draw();
		
		papplet.strokeWeight(1.0f);
		papplet.stroke(0);
		papplet.fill(0);
		papplet.textSize(12);
		papplet.textAlign( PConstants.LEFT, PConstants.BOTTOM );
		papplet.text( "Bottleneck Distance 0-dim", u0+10, line0.getV0()-5 );
		papplet.text( "Bottleneck Distance 1-dim", u0+10, line1.getV0()-5 );
		papplet.text( "Wasserstein Distance 0-dim", u0+10, line2.getV0()-5 );
		papplet.text( "Wasserstein Distance 1-dim", u0+10, line3.getV0()-5 );		
	}



	private class RedP1D implements DPositionSet1D {
		DistanceMatrix dm;
		public RedP1D( DistanceMatrix _dm ){ dm = _dm; }
		@Override public int countPoints() { return dm.getColumnCount(); }
		@Override public float getPoint(int idx) { return (float) dm.get(MainWindow.redG,idx); }
		@Override public float getPointSize(int idx) { return 2; }
	}

}
