/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;


import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HorizonFeature extends AugDrawBase {

    private Line movingLine;
    private Point startP;
    private List<Line> lines;
    private final AugDrawFeature augdraw;
    
    public HorizonFeature(AugmentedView augview, AugDrawFeature augdraw, SharedPreferences p) {
        super(augview, p);
        this.lines = new ArrayList<Line>();
        this.augdraw = augdraw;
        this.movingLine = null;
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
  
    public Line getLine(MotionEvent e) {
        if (closeToEdge(e)) return null;
        for (Line l : lines) {
            float diffx = l.p1.x - e.getX();
            float diffy = l.p1.y - e.getY();
            if ( Math.abs(diffx) < CLOSE_PIXELS ) {
                return l;
            } else if ( Math.abs(diffy) < CLOSE_PIXELS) {
                return l;
            }
        }
        return null;
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
        	
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
          
            movingLine = getLine(event);
            startP = new Point((int) event.getX(), (int) event.getY());
            break;

        case MotionEvent.ACTION_MOVE:
            
            if (movingLine != null)  {
                paintLine(event, true);
            }
            break;
        	
        case MotionEvent.ACTION_UP:

            paintLine(event, false);
            movingLine = null;
        default:
        }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }

    private void paintLine(MotionEvent event, boolean moving) {
        Point endP;
        LINE_TYPE lt;
        if (movingLine != null) {
            if (movingLine instanceof HLine) {
                lt = LINE_TYPE.HORIZONTAL_LINE;
                int newy = (int) event.getY();
                startP = new Point(movingLine.p1.x, newy);
            } else if (movingLine instanceof VLine) {
                lt = LINE_TYPE.VERTICAL_LINE;
                int newx = (int) event.getX();
                startP = new Point(newx, movingLine.p1.y);
            } else {
                Log.e(TAG, "moving line is bad line !!!!!");
                lt = LINE_TYPE.BAD_LINE;
            }
        } else {
            endP = new Point((int) event.getX(), (int) event.getY());
            lt = getLineType(startP, endP);
        }
        
        Line line;
        float vwidth = Float.parseFloat(prefs.getString("VERTICAL_LINE_WIDTH", "9"));
        float hwidth = Float.parseFloat(prefs.getString("HORIZONTAL_LINE_WIDTH", "18"));
        switch (lt) {
        case HORIZONTAL_LINE:
            line = new HLine(0, augview.getWidth(), startP.y, hwidth);
            newMovingLine(line);
            if (moving)
                augdraw.undoCurrentScrible();
            else {
                augdraw.undoLastScrible();
            }
            if (ycloseToEdge(event)) {
                deleteMovingLine();
            }
            break;
        case VERTICAL_LINE:
            line = new VLine(0, augview.getHeight(), startP.x, vwidth);
            newMovingLine(line);
            if (moving)
                augdraw.undoCurrentScrible();
            else {
                augdraw.undoLastScrible();
            }
            if (xcloseToEdge(event)) {
                deleteMovingLine();
            }
            break;
        case BAD_LINE:
            break;
        }
    }
    private void deleteMovingLine() {
        lines.remove(movingLine);
        movingLine = null;
    }
    private void newMovingLine(Line l) {
        lines.remove(movingLine);
        movingLine = l;
        lines.add(l);
    }

    public List<Line> getLines() {
        return lines;
    }
    
    @Override
    public void updateBmp() {
        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        for (Line l : lines) {
            float temp_w = l.width;
            p.setStrokeWidth( temp_w );
            augview.getCanvas().drawLine(l.p1.x, l.p1.y, l.p2.x, l.p2.y, augview.getPaint());
        }
        p.setStrokeWidth(orig_w);
    }

    @Override
    public void clear() {
        lines.clear();
    }
}
