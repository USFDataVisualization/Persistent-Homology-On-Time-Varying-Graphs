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

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import usf.dvl.draw.DObject;
import usf.dvl.graph.layout.forcedirected.ForceDirectedLayoutFrame;
import usf.dvl.graph.timevarying.GraphSet;
import usf.dvl.topology.draw.DBarcode;



public class MainWindow extends PApplet {

	public static GraphSet data = null;

	ViewDistanceMatrix view1;
	ViewMDS1D view2;
	ViewMDS2D view3;
	DObject curView;


	public static int redG = 0;
	public static int bluG = 10;


	DBarcode bc0, bc1;
	ArrayList<ForceDirectedLayoutFrame> graphLayouts = new ArrayList<ForceDirectedLayoutFrame>();


	boolean saveImage = false;
	boolean saveVideo = false;

	public void settings() {
		size(1600, 900, "processing.opengl.PGraphics3D");
	}
	
	PFont arial10, arial48;

	public void setup() {
		ortho();
		
		arial10 = createFont("Arial", 10);
		arial48 = createFont("Arial", 48);
		textFont( arial48 );

		view1 = new ViewDistanceMatrix(this);	
		view1.setPosition(0,520,width,height-520);
		view1.setData( data );

		view2 = new ViewMDS1D(this);	
		view2.setPosition(0,520,width,height-520);
		view2.setData( data );

		view3 = new ViewMDS2D(this);	
		view3.setPosition(0,520,width,height-520);
		view3.setData( data );	  

		curView = view1;


		bc0 = new DBarcode(this);
		bc0.hideInfinite(true);

		bc1 = new DBarcode(this);
		bc1.hideInfinite(true);

		graphLayouts.clear();
		for( int i = 0; i < data.size(); i++) {
			graphLayouts.add( new ForceDirectedLayoutFrame( this, data.getGraph(i), true ) );
		}

	}



	public void draw() {
		background( 255 );

		textFont( arial48 );

		noFill();
		strokeWeight(3.0f);
		stroke(255,0,0); rect( width-height/2-120,          1, height/2, height/2-2 );
		stroke(0,0,255); rect( width-height/2-120, height/2+1, height/2, height/2-2 );
		strokeWeight(1.0f);


		graphLayouts.get(redG).setPosition(width-height/2-120,          1, height/2, height/2-2);
		graphLayouts.get(redG).draw();

		graphLayouts.get(bluG).setPosition(width-height/2-120, height/2+1, height/2, height/2-2);
		graphLayouts.get(bluG).draw();

		bc0.setPosition( width-120, 0, 120, height/2 );
		bc0.setData(  data.getPD(redG) );
		bc0.draw();

		bc1.setPosition( width-120, height/2, 120, height/2 );
		bc1.setData( data.getPD(bluG) );
		bc1.draw();

		strokeWeight(1.0f);
		stroke(0);
		fill(0);
		textSize(12);
		textAlign( PConstants.LEFT, PConstants.TOP );
		text( "Graph t="+redG, width-height/2-110, 10 );
		text( "Graph t="+bluG, width-height/2-110, height/2+10 );
		textAlign( PConstants.LEFT, PConstants.BOTTOM );


		curView.setPosition(5,5,width-height/2-130,height-10);
		curView.draw();

		strokeWeight(1.0f);
		stroke(0);
		fill(0);
		
		textFont( arial10 );		
		textSize(10);
		textAlign( PConstants.RIGHT, PConstants.TOP );
		text( "Change Views:",  895, 2 );		
		textAlign( PConstants.LEFT, PConstants.TOP );
		text( "'1' - Distance Matrix View",  900, 2 );		
		text( "'2' - MDS View",  900, 15 );		

		if( saveImage ){
			saveFrame("frame######.png");
			saveImage = false;
		}
		if( saveVideo ){
			saveFrame("frame######.png");
		}

	}


	public void keyPressed() {
		switch( key ){
		case '1': curView = view1; break;
		case '2': curView = view2; break;
		case '3': curView = view3; break;
		case 's': saveImage = true; break;
		default:
			curView.keyPressed();
		}
	}

	public void mousePressed(){
		curView.mousePressed();
		if( graphLayouts.get(redG).mousePressed() ) return;
		if( graphLayouts.get(bluG).mousePressed() ) return;

	}

	public void mouseDragged(){
		if( curView == view1 ) view1.mouseDragged();

	}

	public void mouseReleased(){
		curView.mouseReleased();
		graphLayouts.get(redG).mouseReleased();
		graphLayouts.get(bluG).mouseReleased();
	}

}
