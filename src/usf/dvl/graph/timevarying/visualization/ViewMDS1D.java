package usf.dvl.graph.timevarying.visualization;

import processing.core.PApplet;
import processing.core.PConstants;
import usf.dvl.common.DistanceMatrix;
import usf.dvl.draw.DPositionSet1D;
import usf.dvl.graph.timevarying.GraphSet;
import usf.dvl.ml.dimensionreduction.ClassicalMDS;


public class ViewMDS1D extends ViewLineCharts {


	public ViewMDS1D( PApplet p ){
	    super(p,"MDS View (press 'c' to change components)");
	  }

	
	
	public void setData( GraphSet _grphs ){
		if( grphs == _grphs ) return;
		super.setData(_grphs);
		
		line0.setData( new MDS1D( grphs.dm[0]) );
		line1.setData( new MDS1D( grphs.dm[1]) );
		line2.setData( new MDS1D( grphs.dm[2]) );
		line3.setData( new MDS1D( grphs.dm[3]) );	    
		
	  }
	
	public void draw() {
		super.draw();

		
		papplet.strokeWeight(1.0f);
		papplet.stroke(0);
		papplet.fill(0);
		papplet.textSize(12);
		papplet.textAlign( PConstants.LEFT, PConstants.BOTTOM );
		papplet.text( "Bottleneck 0-dim (component " + component + ")",  u0+10, line0.getV0()-5 );
		papplet.text( "Bottleneck 1-dim (component " + component + ")",  u0+10, line1.getV0()-5 );
		papplet.text( "Wasserstein 0-dim (component " + component + ")", u0+10, line2.getV0()-5 );
		papplet.text( "Wasserstein 1-dim (component " + component + ")", u0+10, line3.getV0()-5 );	
		
	}
	
	int component = 0;
	int maxComponent = 5;
	
	class MDS1D implements DPositionSet1D {

		DistanceMatrix dm;
		ClassicalMDS mds;
		
		MDS1D(DistanceMatrix _dm ){ 
			dm = _dm;
			mds = new ClassicalMDS( dm, maxComponent );
		}
		
		public void setComponent( int comp ) { component = comp; }
		
		@Override public int countPoints() { return mds.getColumnCount(); }
		@Override public float getPoint(int idx) { return (float)mds.get( component,  idx ); }
		@Override public float getPointSize(int idx) { return 2; }
		
	}
	
	    public boolean keyPressed(){ 
	    	if( papplet.key == 'c' ) {
	    		component = (component+1)%maxComponent;
	    		return true;
	    	}
	    	super.keyPressed();
	    	return false;
	    }

}
