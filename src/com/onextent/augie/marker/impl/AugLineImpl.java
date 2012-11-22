package com.onextent.augie.marker.impl;

import com.onextent.augie.marker.AugLine;

import android.graphics.Point;

public class AugLineImpl implements AugLine {
    
    private Point p1;
	private Point p2;
	private Point center;
    private float width;
	public AugLineImpl(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.center = null;
        this.width = 9;
    }
    public float getBorderWidth() {
        return width;
    }
    public void setWidth(float width) {
        this.width = width;
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
        if (center == null) {
            center = new Point((p2.x + p1.x) / 2, (p2.y + p1.y) / 2); //?
        }
        return center;
    }
    public void setCenter(Point center) {
        this.center = center;
    }
}
