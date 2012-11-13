package com.onextent.augie.marker;

import android.graphics.Point;

public interface AugRect extends AugMarker {

    public Point getP1();
    public void setP1(Point p1);
    
    public Point getP2();
    public void setP2(Point p2);
    
    public Point getCenter();
    
    public void move(int x, int y);

    //public void getCameraArea(); // api 14 ics
}
