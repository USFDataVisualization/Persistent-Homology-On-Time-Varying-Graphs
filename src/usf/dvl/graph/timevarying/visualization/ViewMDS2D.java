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

import usf.dvl.draw.DObject;
import processing.core.PApplet;
import processing.data.JSONObject;
import usf.dvl.draw.DObject;
import usf.dvl.draw.objects.DragableBox;
import usf.dvl.graph.timevarying.GraphSet;

public class ViewMDS2D extends DObject {

	  //MDS2D mds2D0, mds2D1, mds2D2, mds2D3;
	        


	  public ViewMDS2D(PApplet p ){
	    super(p);
	    /*
	    mds2D0 = new MDS2D(p);
	    mds2D1 = new MDS2D(p);
	    mds2D2 = new MDS2D(p);
	    mds2D3 = new MDS2D(p);
	    */
	  }
	  
	  public void draw() {}
	  
	  /*
	  public JSONObject serializeViewJSON(){
	    JSONObject json = new JSONObject();
	    json.setJSONObject( "detail", detView.serializeViewJSON() );
	    return json;
	  }
	  
	  public void parseViewJSON( JSONObject json ) {
	    detView.parseViewJSON( json.getJSONObject( "detail" ) );
	  }
	  
*/

	  public void setData( GraphSet grphs ){
	  }
	  /*
	    int [] dim0 = {-1,-1,-1,-1};
	    int [] dim1 = {-1,-1,-1,-1};

	    //Arrays.fill(dim0,-1);
	    //Arrays.fill(dim1,-1);

	    if( pds.bottleneck_pd0_mds_valid.size()>1 ){  dim0[0] = pds.bottleneck_pd0_mds_valid.get(0); dim1[0] = pds.bottleneck_pd0_mds_valid.get(1);  mds2D0.setData( pds.bottleneck_pd0_mds,  dim0[0], dim1[0] ); }
	    if( pds.bottleneck_pd1_mds_valid.size()>1 ){  dim0[1] = pds.bottleneck_pd1_mds_valid.get(0); dim1[1] = pds.bottleneck_pd1_mds_valid.get(1);  mds2D1.setData( pds.bottleneck_pd1_mds,  dim0[1], dim1[1] ); }
	    if( pds.wasserstein_pd0_mds_valid.size()>1 ){ dim0[2] = pds.wasserstein_pd0_mds_valid.get(0);dim1[2] = pds.wasserstein_pd0_mds_valid.get(1); mds2D2.setData( pds.wasserstein_pd0_mds, dim0[2], dim1[2] ); }
	    if( pds.wasserstein_pd1_mds_valid.size()>1 ){ dim0[3] = pds.wasserstein_pd1_mds_valid.get(0);dim1[3] = pds.wasserstein_pd1_mds_valid.get(1); mds2D3.setData( pds.wasserstein_pd1_mds, dim0[3], dim1[3] ); }
	    
	    setPanels( mds2D0, mds2D1, mds2D2, mds2D3 );
	    setLabels( "bottleneck_pd0_mds: "  + dim0[0] + " x " + dim1[0],
	               "bottleneck_pd1_mds: "  + dim0[1] + " x " + dim1[1], 
	               "wasserstein_pd0_mds: " + dim0[2] + " x " + dim1[2], 
	               "wasserstein_pd1_mds: " + dim0[3] + " x " + dim1[3] );
	               
	    detView.setData( grphs, pds, mds2D0, mds2D1, mds2D2, mds2D3 );
	    
	  }
	  
	  
	  boolean mousePressed(){
	    if( detView.mousePressed() ) return true;
	    return super.mousePressed();
	  }

	  boolean mouseReleased(){
	    if( detView.mouseReleased() ) return true;
	    return super.mouseReleased();
	  }

	  public void draw(){
	     super.draw();
	     detView.draw();
	     pushMatrix();
	     translate(0,0,100);
	     if( MSDcolorScheme == 0 ){ seqColMap.draw( papplet, width-200, 10, 150, 40, new String[]{"midnight","6am","noon","6pm","midnight"} ); }
	     if( MSDcolorScheme == 1 ){ cycleColormap.draw( papplet, width-200, 30, new String []{ "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" } ); }
	     popMatrix();
	  }  
	}




	class MDS2D  extends DPointsLines {
	  
	  BaseMatrix mat;
	  int col0 = -1, col1 = -1;
	  
	  float [] mins = new float[2];
	  float [] maxs = new float[2];

	  MDS2D( PApplet p ){
	    super( p );
	     enableSelection( 5 );
	  }
	  
	  void setData( BaseMatrix _pos, int _col0, int _col1 ){
	    mat = _pos; col0 = _col0; col1 = _col1; 
	    setData( new MDSPositions2D() ); 
	    setColorScheme( new MDSColor2D() );
	  }
	  
	  public void draw(){
	    if( pos == null ) return;
	      
	    mins[0] =  Float.MAX_VALUE;
	    mins[1] =  Float.MAX_VALUE;
	    maxs[0] = -Float.MAX_VALUE;
	    maxs[1] = -Float.MAX_VALUE;
	      
	    for( int idx = 0; idx < mat.getWidth(); idx++ ){
	      float x = (float)mat.get( idx, col0 );
	      float y = (float)mat.get( idx, col1 );
	      mins[0] = min(mins[0],x); maxs[0] = max(maxs[0],x);
	      mins[1] = min(mins[1],y); maxs[1] = max(maxs[1],y);
	    }
	      
	    super.draw();
	  }
	  
	  
	  class MDSPositions2D implements DPositionSet2D {
	    MDSPositions2D( ){ } 
	    int count(){ return timeline.getStopIndex()-timeline.getStartIndex(); } // pos.getWidth(); }
	    float getX(int idx){ return ((float)mat.get( timeline.getStartIndex()+idx, col0 ) - mins[0]) / (maxs[0]-mins[0]) * w; }
	    float getY(int idx){ return ((float)mat.get( timeline.getStartIndex()+idx, col1 ) - mins[1]) / (maxs[1]-mins[1]) * h; }
	    float getSize(int idx){ return 5; }
	  }
	  
	  
	  class MDSLine2D implements DLineSet {
	    int [] getLine( int idx ){ return new int[]{idx,idx+1}; }
	    int count(){ return pos.count()-1; }
	    float getWeight(){ return 3; }
	  }
	  
	    
	  class MDSColor2D implements ColorScheme {
	    int getFill( int idx ){
	      switch( MSDcolorScheme ){
	        case 0: 
	          int hr = timeline.getTimeAtIndex( idx+timeline.getStartIndex() ).get(Calendar.HOUR_OF_DAY);
	          return seqColMap.getColor( (float)hr/24.0f );
	        case 1:
	          int day = timeline.getTimeAtIndex( idx+timeline.getStartIndex() ).get(Calendar.DAY_OF_WEEK);
	          return cycleColormap.getColor(day);
	      }
	      return color(100,100,200);
	    }
	    int getStroke( int idx ){ return color(0); }
	  }
	 */
}
