package com.onextent.augie.marker;

import com.onextent.augie.AugmentedView;
import com.onextent.augie.marker.impl.AugLineImpl;
import com.onextent.augie.marker.impl.AugRectImpl;
import com.onextent.augie.marker.impl.AugScribleImpl;

import android.graphics.Point;

public class MarkerFactory {

    public static AugLine createLine(Point p1, Point p2) {
        
        return new AugLineImpl(p1, p2);
    }

    public static AugScrible createScrible(AugmentedView v) {
        
        return new AugScribleImpl(v);
    }

    public static AugRect createRect(Point p1, Point p2) {
        
        return new AugRectImpl(p1, p2);
    }
    
    public static AugRect createRect(AugScrible s) {
        
        Point p1 = new Point(s.getMinX(), s.getMinY());
        Point p2 = new Point(s.getMaxX(), s.getMaxY());
        return createRect(p1, p2);
    }
}
