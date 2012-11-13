package com.onextent.augie.marker;

import android.graphics.Point;

public interface AugLine {

    public float getBorderWidth();
    public void setWidth(float width);
    public Point getP1();
    public void setP1(Point p1);
    public Point getP2();
    public void setP2(Point p2);
    public Point getCenter();
    public void setCenter(Point center);
}
