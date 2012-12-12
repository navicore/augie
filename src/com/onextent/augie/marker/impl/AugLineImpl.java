package com.onextent.augie.marker.impl;

import com.onextent.augie.marker.AugLine;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.JSONCoder;

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
    public AugLineImpl() {
    }
    public float getWidth() {
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
    
    //begin Codable
    static final private CodeableName cname = new CodeableName("AUGIE/PARTS/LINE") { };
    @Override
    public CodeableName getCodeableName() {
        return cname;
    }
    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        code.put(CODEABLE_NAME_KEY, cname);
        code.put("p1.x", getP1().x);
        code.put("p1.y", getP1().y);
        code.put("p2.x", getP2().x);
        code.put("p2.y", getP2().y);
        code.put("width", getWidth());
        return code;
    }
    @Override
    public void setCode(Code code) throws CodeableException {
        center = null;
        if (!code.has(CODEABLE_NAME_KEY) || !code.getCodeableName(CODEABLE_NAME_KEY).equals(cname)) throw new CodeableException("no p1.x");
        setP1(new Point(code.getInt("p1.x"), code.getInt("p1.y")));
        setP2(new Point(code.getInt("p2.x"), code.getInt("p2.y")));
        setWidth(code.getInt("width"));
    }
}
