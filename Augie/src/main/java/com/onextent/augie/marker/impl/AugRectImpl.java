/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.marker.impl;

import android.graphics.Point;

import com.onextent.augie.marker.AugRect;

public class AugRectImpl implements AugRect {
    
    private Point p1;
    private Point p2;

    public AugRectImpl(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Point getCenter() {
        throw new java.lang.UnsupportedOperationException();
    }

    public void move(int x, int y) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public String toString() {
    
        return "rect: (" + p1 + ", " + p2 + ")";
    }
}
