/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.marker;

import java.util.List;

public interface AugScrible extends AugMarker, List<AugLine> {
   
    /*
     * ya, mixed metaphor, but you gesture by scribbling.
     */
    public enum GESTURE_TYPE {HORIZONTAL_LINE, 
                              VERTICAL_LINE, NONE, 
                              COUNTER_CLOCKWISE_AREA, 
                              CLOCKWISE_AREA, 
                              TAP}
    
    public int getMaxX();
    
    public int getMinX();
    
    public int getMaxY();
    
    public int getMinY();
    
    public GESTURE_TYPE getGestureType();

    void setGestureType(GESTURE_TYPE t);
}
