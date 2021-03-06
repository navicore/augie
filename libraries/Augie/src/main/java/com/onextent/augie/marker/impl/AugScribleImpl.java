/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.marker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.graphics.Point;

import com.onextent.augie.AugieScape;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;

public class AugScribleImpl extends ArrayList<AugLine> implements AugScrible {
    
    //todo: make markers codeable and replace toString with getCode()

    private static final int MAX_TAP_SCRIBLE_LEN = 10;
    private static final int MAX_TAP_SCRIBLE_END_DISTANCE = 50;

    private static final long serialVersionUID = 1L;

    private final AugieScape augieScape;

    private GESTURE_TYPE gtype;
    private boolean gtype_is_set;
    private boolean ends_are_close;
    private boolean ends_are_close_is_calculated;
    private int minX, maxX, minY, maxY, sumOfEdges;

    public AugScribleImpl(AugieScape v) {
        augieScape = v;
        gtype_is_set = false;
        ends_are_close = false;
        ends_are_close_is_calculated = false;
        gtype = GESTURE_TYPE.NONE;
        minX = 0; maxX = 0; minY = 0; maxY = 0; sumOfEdges = 0;
    }

    @Override
    public void setGestureType(GESTURE_TYPE t) {
        gtype = t;
        gtype_is_set = true;
    }

    //
    // begin api
    //

    @Override
    public int getMaxX() {
        return maxX;
    }

    @Override
    public int getMinX() {
        return minX;
    }

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public int getMinY() {
        return minY;
    }

    @Override
    public GESTURE_TYPE getGestureType() {
        if (!gtype_is_set) {
            gtype = getLineType();
            if (gtype == GESTURE_TYPE.NONE) {
                gtype = getTapGType();
                if (gtype == GESTURE_TYPE.NONE) {
                    gtype = getRectGType();
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

    private void calculateCornersAndEdges() {
        for (AugLine l : this) {
            checkPoint(l.getP1());
            checkPoint(l.getP2());
        }
        sumEdges();
    }
    
    // (x2-x1)(y2+y1)
    private void sumEdges() {

        List<Point> points = new ArrayList<Point>();
        for (AugLine l : this) {
            points.add(l.getP1());
            points.add(l.getP2());
        }
        int c = 0;
        int j = 0;
        int n = points.size();

        if (n < 3) {
           
            return;
        }

        for (int i=0; i < n; i++) {
          
            j = i + 1;
            if (j == n) j = 0;
            c += (points.get(j).x - points.get(i).x) * (points.get(j).y + points.get(i).y);
        }
        
        sumOfEdges = c;
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

        if (size() < MAX_TAP_SCRIBLE_LEN 
                && endsAreClose()
                && !isOnFrame()
                ) {

            return GESTURE_TYPE.TAP;
        }
        return GESTURE_TYPE.NONE;
    }

    private boolean isOnFrame() {
        if (size() != 1) return false;
        Point p1 = get(0).getP1();
        Point p2 = get(0).getP2();
        if (p1.x < MAX_TAP_SCRIBLE_END_DISTANCE && p2.x < MAX_TAP_SCRIBLE_END_DISTANCE) return true;
        if (p1.x > augieScape.getWidth() - MAX_TAP_SCRIBLE_END_DISTANCE 
                && p2.x > augieScape.getWidth() - MAX_TAP_SCRIBLE_END_DISTANCE) return true;
        if (p1.y < MAX_TAP_SCRIBLE_END_DISTANCE && p2.y < MAX_TAP_SCRIBLE_END_DISTANCE) return true;
        if (p1.y > augieScape.getHeight() - MAX_TAP_SCRIBLE_END_DISTANCE 
                && p2.y > augieScape.getHeight() - MAX_TAP_SCRIBLE_END_DISTANCE) return true;
        return false;
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

        if ( horiz && begin.x < 50 && end.x > (augieScape.getWidth() -50) ) {
            return AugScrible.GESTURE_TYPE.HORIZONTAL_LINE;

        } else if ( verti && begin.y < 50 && end.y > (augieScape.getHeight() - 50) ) {
            return AugScrible.GESTURE_TYPE.VERTICAL_LINE;
        }
        return AugScrible.GESTURE_TYPE.NONE;
    }

    @Override
    public String toString() {
        String ret = "scrible: (";
        switch (gtype) {
        case TAP:
            ret += "g: tap";
            break;
        case CLOCKWISE_AREA:
            ret += "g: clockwise";
            break;
        case COUNTER_CLOCKWISE_AREA:
            ret += "g: counter-clockwise";
            break;
        case HORIZONTAL_LINE:
            ret += "g: horizontal";
            break;
        case VERTICAL_LINE:
            ret += "g: vertical";
            break;
        case NONE:
            ret += "g: none";
            break;
        }
        for (AugLine l : this) {
            ret += ", " + l;
        }
        ret += ")";
        return ret;
    }
}
