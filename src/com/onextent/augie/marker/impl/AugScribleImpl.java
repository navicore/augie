package com.onextent.augie.marker.impl;

import java.util.ArrayList;
import java.util.Collection;

import android.graphics.Point;
import android.util.Log;

import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;

public class AugScribleImpl extends ArrayList<AugLine> implements AugScrible {

    protected final String TAG = Augiement.TAG;
    private static final int MAX_TAP_SCRIBLE_LEN = 10;
    private static final int MAX_TAP_SCRIBLE_END_DISTANCE = 50;
    
    private static final long serialVersionUID = 1L;
    
    private final AugieView augview;
    
    private GESTURE_TYPE gtype;
    private boolean gtype_is_set;
    private boolean ends_are_close;
    private boolean ends_are_close_is_calculated;
    private int minX, maxX, minY, maxY, sumOfEdges;
    private Point prevPnt;

    public AugScribleImpl(AugieView v) {
        augview = v;
        gtype_is_set = false;
        ends_are_close = false;
        ends_are_close_is_calculated = false;
        gtype = GESTURE_TYPE.NONE;
        minX = 0; maxX = 0; minY = 0; maxY = 0; sumOfEdges = 0;
        prevPnt = null;
    }
    
    //
    // begin api
    //
    
    public int getMaxX() {
       return maxX;
    }
    
    public int getMinX() {
       return minX;
    }
    
    public int getMaxY() {
       return maxY;
    }
    
    public int getMinY() {
       return minY;
    }

    public GESTURE_TYPE getGestureType() {
        if (!gtype_is_set) {
            gtype = getLineType();
            if (gtype == GESTURE_TYPE.NONE) {
                gtype = getTapGType();
                if (gtype == GESTURE_TYPE.NONE) {
                    gtype = getRectGType();
                    if (gtype == GESTURE_TYPE.CLOCKWISE_AREA) {
                        Log.d(TAG, "is a clockwise");
                    } else if (gtype == GESTURE_TYPE.COUNTER_CLOCKWISE_AREA) {
                        Log.d(TAG, "is a counter clockwise");
                    }
                }
            }
            gtype_is_set = true;
        }
        return gtype;
    }
    
    //
    // begin impl
    //
    private void resetGesture() {
        gtype_is_set = false;
        ends_are_close = false;
        ends_are_close_is_calculated = false;
        minX = 0; maxX = 0; minY = 0; maxY = 0; sumOfEdges = 0;
        prevPnt = null;
    }
    private boolean endsAreClose() {
        if (ends_are_close_is_calculated) return ends_are_close;
        Point p1 = get(0).getP1();
        Point p2 = get(size() - 1).getP2();
        double dist = Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));

        ends_are_close = dist < MAX_TAP_SCRIBLE_END_DISTANCE;
        ends_are_close_is_calculated = true;
        return ends_are_close;
    }
    
    GESTURE_TYPE getRectType() {
        
        if (maxX - minX < MAX_TAP_SCRIBLE_END_DISTANCE || maxY - minY < MAX_TAP_SCRIBLE_END_DISTANCE) return GESTURE_TYPE.NONE;
        return sumOfEdges > 0 ? GESTURE_TYPE.CLOCKWISE_AREA : GESTURE_TYPE.COUNTER_CLOCKWISE_AREA;
    }

    //might be faster to do this on each add (as long as there is no remove)
    private void checkPoint(Point p) {
        if (p.x > maxX) maxX = p.x;
        if (minX == 0 || p.x < minX) minX = p.x;
        if (p.y > maxY) maxY = p.y;
        if (minY == 0 || p.y < minY) minY = p.y;
    }
    
    private void sumEdges() {
        //can not detect clockwise or counter clockwise with 
        //more than 180 degrees of sample
        int sampleSz = (int) (size() * .4); 
        for (int i = 0; i < sampleSz; i++) {
            addPoint(get(i).getP1());
            addPoint(get(i).getP2());
        }
    }
    private void addPoint(Point p) { //to determine 
        if (prevPnt == null) {
            prevPnt = p;
        } else {
            int edge = (p.x - prevPnt.x) * (p.y + prevPnt.y);
            sumOfEdges += edge;
        }
    }
    private void calculateCornersAndEdges() {
        for (AugLine l : this) {
            checkPoint(l.getP1());
            checkPoint(l.getP2());
        }
        sumEdges();

    }
    private GESTURE_TYPE getRectGType() {
        if (size() <= 0) return GESTURE_TYPE.NONE;
        if (size() > MAX_TAP_SCRIBLE_LEN && endsAreClose()) {
            calculateCornersAndEdges();
            return getRectType();

        }
        return GESTURE_TYPE.NONE;
    }
    private GESTURE_TYPE getTapGType() {
        if (size() <= 0) return GESTURE_TYPE.NONE;

        if (size() < MAX_TAP_SCRIBLE_LEN && endsAreClose()) {

            return GESTURE_TYPE.TAP;
        }
        return GESTURE_TYPE.NONE;
    }
    
    @Override
    public boolean add(AugLine object) {
        resetGesture();
        return super.add(object);
    }

    @Override
    public void add(int index, AugLine object) {
        resetGesture();
        super.add(index, object);
    }

    @Override
    public boolean addAll(Collection<? extends AugLine> collection) {
        resetGesture();
        return super.addAll(collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends AugLine> collection) {
        resetGesture();
        return super.addAll(index, collection);
    }

    @Override
    public void clear() {
        resetGesture();
        super.clear();
    }

    @Override
    public AugLine remove(int index) {
        resetGesture();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object object) {
        resetGesture();
        return super.remove(object);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        resetGesture();
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public AugLine set(int index, AugLine object) {
        resetGesture();
        return super.set(index, object);
    }

    private AugScrible.GESTURE_TYPE getLineType() {
       
        if (size() <= 0) return GESTURE_TYPE.NONE;
        Point p1 = get(0).getP1();
        Point p2 = get(size() - 1).getP2();
    
        AugScrible.GESTURE_TYPE t;
        
        t = getLineTypeByOrder(p1, p2);
        if (t == AugScrible.GESTURE_TYPE.NONE) t = getLineTypeByOrder(p2, p1);
        return t;
    }
    private AugScrible.GESTURE_TYPE getLineTypeByOrder(Point begin, Point end) {
        boolean verti = false;
        int ydif = begin.y - end.y;
        //if (ydif < 200 && ydif > -200) {
        if (ydif < 200) {
            verti = true;
        }
        boolean horiz = false;
        int xdif = begin.x - end.x;
        //if (xdif < 200 && xdif > -200) {
        if (xdif < 200) {
            horiz = true;
        }
           
        if ( horiz && begin.x < 50 && end.x > (augview.getWidth() -50) ) {
            return AugScrible.GESTURE_TYPE.HORIZONTAL_LINE;
                
        } else if ( verti && begin.y < 50 && end.y > (augview.getHeight() - 50) ) {
            return AugScrible.GESTURE_TYPE.VERTICAL_LINE;
        }
        return AugScrible.GESTURE_TYPE.NONE;
    }
  
}
