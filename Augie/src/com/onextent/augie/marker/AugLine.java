/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.marker;

import com.onextent.android.codeable.Codeable;

import android.graphics.Point;

public interface AugLine extends Codeable {

    public float getWidth();
    public void setWidth(float width);
    public Point getP1();
    public void setP1(Point p1);
    public Point getP2();
    public void setP2(Point p2);
    public Point getCenter();
    //public void setCenter(Point center);
}
