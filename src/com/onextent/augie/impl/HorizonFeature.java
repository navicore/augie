/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.onextent.augie.AugieView;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;
import com.onextent.augie.marker.impl.AugLineImpl;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HorizonFeature extends AugDrawBase {

    private AugLineImpl movingLine;
    private Point startP;
    private List<AugLineImpl> lines;
    private AugDrawFeature augdraw;
    
    private final static Set<String> deps;
    static {
        deps = new HashSet<String>();
        deps.add(AugDrawFeature.AUGIE_NAME);
    }
    
    @Override
    public Set<String> getDependencyNames() {
        return deps;
    }

    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
        for (Augiement a : helpers) {
            if (a instanceof AugDrawFeature) {
                augdraw = (AugDrawFeature) a;
            }
        }
        if (augdraw == null) throw new AugiementException("draw feature is null");
        this.lines = new ArrayList<AugLineImpl>();
        this.movingLine = null;
    }
    
    public AugLineImpl getLine(MotionEvent e) {
        if (closeToEdge(e)) return null;
        for (AugLineImpl l : lines) {
            float diffx = l.getP1().x - e.getX();
            float diffy = l.getP1().y - e.getY();
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
        //Point endP;
        AugScrible.GESTURE_TYPE lt;
        if (movingLine != null) {
            if (movingLine instanceof HLine) {
                lt = AugScrible.GESTURE_TYPE.HORIZONTAL_LINE;
                int newy = (int) event.getY();
                startP = new Point(movingLine.getP1().x, newy);
            } else if (movingLine instanceof VLine) {
                lt = AugScrible.GESTURE_TYPE.VERTICAL_LINE;
                int newx = (int) event.getX();
                startP = new Point(newx, movingLine.getP1().y);
            } else {
                Log.e(TAG, "moving line is bad line !!!!!");
                lt = AugScrible.GESTURE_TYPE.NONE;
            }
        } else {
            //endP = new Point((int) event.getX(), (int) event.getY());
            AugScrible s = augdraw.getCurrentScrible();
            lt = s == null ? GESTURE_TYPE.NONE : augdraw.getCurrentScrible().getGestureType();
        }
        
        AugLineImpl line;
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
        case NONE:
            break;
        default:
            break;
        }
    }
    private void deleteMovingLine() {
        lines.remove(movingLine);
        movingLine = null;
    }
    private void newMovingLine(AugLineImpl l) {
        lines.remove(movingLine);
        movingLine = l;
        lines.add(l);
    }

    public List<AugLineImpl> getLines() {
        return lines;
    }
    
    @Override
    public void updateCanvas() {
        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        for (AugLineImpl l : lines) {
            float temp_w = l.getBorderWidth();
            p.setStrokeWidth( temp_w );
            augview.getCanvas().drawLine(l.getP1().x, l.getP1().y, l.getP2().x, l.getP2().y, augview.getPaint());
        }
        p.setStrokeWidth(orig_w);
    }

    @Override
    public void clear() {
        lines.clear();
    }
    
    public static final String AUGIE_NAME = "AUGIE/FEATURES/HORIZON_DRAW";
    @Override
    public String getAugieName() {
        return AUGIE_NAME;
    }

    @Override
    public JSONObject getState() throws AugieableException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setState(JSONObject state) throws AugieableException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void edit(Context context, EditCallback cb) throws AugieableException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }
}
