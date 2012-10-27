/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HorizonFeature extends AugDrawBase {

	private Point startP;
	private List<Line> lines;
    
    public HorizonFeature(AugmentedView augview) {
	    super(augview);
	    this.lines = new ArrayList<Line>();
    }

    private LINE_TYPE getLineType(Point p1, Point p2) {
    
    	LINE_TYPE t = getLineTypeByOrder(p1, p2);
    	if (t == LINE_TYPE.BAD_LINE) return getLineTypeByOrder(p2,  p1);
    	return t;
    }
    private LINE_TYPE getLineTypeByOrder(Point begin, Point end) {
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
	    	return LINE_TYPE.HORIZONTAL_LINE;
            	
        } else if ( verti && begin.y < 50 && end.y > (augview.getHeight() - 50) ) {
	    	return LINE_TYPE.VERTICAL_LINE;
        }
    	return LINE_TYPE.BAD_LINE;
    }
    
	public boolean onTouch(View v, MotionEvent event) {
		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
            startP = new Point((int) event.getX(), (int) event.getY());
			break;

		case MotionEvent.ACTION_UP:

            Point endP = new Point((int) event.getX(), (int) event.getY());
            LINE_TYPE lt = getLineType(startP, endP);
           	Point p1, p2;
           	Line line;
            switch (lt) {
            case HORIZONTAL_LINE:
            	p1 = new Point(0, startP.y);
            	p2 = new Point(augview.getWidth(), startP.y);
            	line = new HLine(p1, p2);
            	lines.add(line);
            	redraw();
            	augview.reset();
            	break;
            case VERTICAL_LINE:
            	p1 = new Point(startP.x, 0);
            	p2 = new Point(startP.x, augview.getHeight());
            	line = new VLine(p1, p2);
            	lines.add(line);
            	redraw();
            	augview.reset();
            	break;
            case BAD_LINE:
            	break;
            }
		}
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
		return false;
	}

	public void redraw() {
    	for (Line l : lines) {
    		//todo: calculate p2 based on rotation
    		augview.getCanvas().drawLine(l.p1.x, l.p1.y, l.p2.x, l.p2.y, augview.getPaint());
    	}
	}

	public void clear() {
		lines.clear();
	}
}
