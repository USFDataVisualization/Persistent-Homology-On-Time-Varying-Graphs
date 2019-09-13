package usf.dvl.graph.timevarying.visualization;

import processing.core.PApplet;
import processing.core.PConstants;
import usf.dvl.draw.DColorScheme;
import usf.dvl.draw.DObject;
import usf.dvl.draw.frames.LineChartFrame;
import usf.dvl.draw.objects.DragableBox;
import usf.dvl.graph.timevarying.GraphSet;

public class ViewLineCharts extends DObject {

	LineChartFrame line0, line1, line2, line3;


	DragableBox redBox;
	DragableBox bluBox;
	DragableBox selected = null;


	GraphSet grphs = null;

	String title;

	public ViewLineCharts( PApplet p, String _title ){
		super(p);
		
		title = _title;
		
		line0 = new LineChartFrame(p);
		line1 = new LineChartFrame(p);
		line2 = new LineChartFrame(p);
		line3 = new LineChartFrame(p);

		bluBox = new DragableBox(papplet,0);
		redBox = new DragableBox(papplet,1);

	}

	public void setPosition( int _u0, int _v0, int _w, int _h ){
		if( u0==_u0 && v0==_v0 && w==_w && h==_h ) return;
		super.setPosition(_u0, _v0, _w, _h);
		
		if( grphs != null ) {
			bluBox.setPosition( (int)PApplet.map( MainWindow.bluG, 0, grphs.size()-1, u0+5, u0+w-10 ), v0+25, 5, h-25 );
			redBox.setPosition( (int)PApplet.map( MainWindow.redG, 0, grphs.size()-1, u0+5, u0+w-10 ), v0+25, 5, h-25 );
		}
		
		bluBox.setConstraintsX(u0+5, u0+w-10);
		redBox.setConstraintsX(u0+5, u0+w-10);
		
		int ch = (h-25)/4;
		line0.setPosition( u0+5, v0+50,      w-10, ch-35 );
		line1.setPosition( u0+5, v0+50+ch,   w-10, ch-35 );
		line2.setPosition( u0+5, v0+50+2*ch, w-10, ch-35 );
		line3.setPosition( u0+5, v0+50+3*ch, w-10, ch-35 );
		
	}
	
	public void setData( GraphSet _grphs ){
		if( grphs == _grphs ) return;
		grphs = _grphs;

		bluBox.setPosition( (int)PApplet.map( MainWindow.bluG, 0, grphs.size()-1, u0+5, u0+w-10 ), v0+25, 5, h-25 );		
		bluBox.lockY();
		bluBox.setConstraintsX(u0+5, u0+w-10);
		bluBox.setColorScheme( new SelectionBoxColorScheme() );

		redBox.setPosition( (int)PApplet.map( MainWindow.redG, 0, grphs.size()-1, u0+5, u0+w-10 ), v0+25, 5, h-25 );
		redBox.lockY();
		redBox.setConstraintsX(u0+5, u0+w-10);
		redBox.setColorScheme(  new SelectionBoxColorScheme() );
    
	}

	
	public void draw(){

		papplet.strokeWeight(1.0f);
		papplet.stroke(0);
		papplet.fill(0);

		redBox.draw();
		bluBox.draw();


		papplet.fill(255);
		papplet.noStroke();
		papplet.rect(u0, line0.getV0()-25, w, 20);
		papplet.rect(u0, line1.getV0()-25, w, 20);
		papplet.rect(u0, line2.getV0()-25, w, 20);
		papplet.rect(u0, line3.getV0()-25, w, 20);	    




		papplet.noFill();
		papplet.stroke(100,0,100);
		papplet.strokeWeight(3);
		papplet.rect( u0,line0.getV0()-25,w,line0.getHeight()+35);	    
		papplet.rect( u0,line1.getV0()-25,w,line1.getHeight()+35);	    
		papplet.rect( u0,line2.getV0()-25,w,line2.getHeight()+35);	    
		papplet.rect( u0,line3.getV0()-25,w,line3.getHeight()+35);	    

		line0.draw();
		line1.draw();
		line2.draw();
		line3.draw();


		papplet.strokeWeight(1.0f);
		papplet.stroke(0);
		papplet.fill(0);
		papplet.textSize(20);
		papplet.textAlign( PConstants.LEFT, PConstants.TOP );
		papplet.text( title,  u0+10, v0 );
		
	}


	public boolean keyPressed(){
		switch( papplet.key ){

		case 'm': {
			MainWindow.bluG = (MainWindow.bluG+1)%grphs.size(); 
			bluBox.setPosition( (int)PApplet.map( MainWindow.bluG, 0, grphs.size()-1, 1, w-1 ), bluBox.getV0(), bluBox.getWidth(), bluBox.getHeight() );
			return true;
		} 
		case 'n': {
			MainWindow.redG = (MainWindow.redG+1)%grphs.size(); 
			redBox.setPosition( (int)PApplet.map( MainWindow.redG, 0, grphs.size()-1, 1, w-1 ), redBox.getV0(), redBox.getWidth(), redBox.getHeight() );
			return true;
		}
		default: System.out.println("typed " + ((int)papplet.key) + " " + papplet.keyCode);
		}
		return false;
	}


	public boolean mousePressed(){
		if( redBox.mousePressed() ){ selected = redBox; return true; }
		if( bluBox.mousePressed() ){ selected = bluBox; return true; }
		return false;
	}

	public boolean mouseDragged(){
		if( selected != null ){
			MainWindow.redG = (int)PApplet.map( redBox.getU0(), u0+5, u0+w-10, 0, grphs.size() );
			MainWindow.bluG = (int)PApplet.map( bluBox.getU0(), u0+5, u0+w-10, 0, grphs.size() );
			MainWindow.redG = PApplet.constrain( MainWindow.redG, 0, grphs.size()-1 );
			MainWindow.bluG = PApplet.constrain( MainWindow.bluG, 0, grphs.size()-1 );
			return true;
		}
		return false;
	}

	public boolean mouseReleased(){
		selected = null;
		bluBox.mouseReleased();
		redBox.mouseReleased();
		return false;
	}





	private class SelectionBoxColorScheme implements DColorScheme {

		@Override public boolean fillEnabled() { return true; }
		@Override public int getFill(int idx) {
			if( idx == 0 ) { return papplet.color(170,170,255); } 
			if( idx == 1 ) { return papplet.color(255,170,170); }
			return 0;
		}

		@Override public boolean strokeEnabled() { return false; }
		@Override public float getStrokeWeight(int idx) { return 0; }
		@Override public int getStroke(int idx) { return 0; }

		@Override public boolean shadowEnabled() { return false; }
		@Override public int getShadow() { return 0; }

	}
}
