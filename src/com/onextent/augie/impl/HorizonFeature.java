/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeArray;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;
import com.onextent.augie.marker.impl.AugLineImpl;


import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HorizonFeature extends AugDrawBase {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/HORIZON_DRAW");
    private static final String UI_NAME = "Movable Horizon";
    
    private AugLine movingLine;
    private Point startP;
    private final List<AugLine> lines;
    private AugDrawFeature augdraw;
    
    private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugDrawFeature.AUGIE_NAME);
    }
    
    public HorizonFeature() {
        lines = new ArrayList<AugLine>();
    }
    
    @Override
    public Set<CodeableName> getDependencyNames() {
        return deps;
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
        for (Augiement a : helpers) {
            if (a instanceof AugDrawFeature) {
                augdraw = (AugDrawFeature) a;
            }
        }
        if (augdraw == null) throw new AugiementException("draw feature is null");
        this.movingLine = null;
    }
    
    public AugLine getLine(MotionEvent e) {
        if (closeToEdge(e)) return null;
        for (AugLine l : lines) {
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
        AugScrible.GESTURE_TYPE lt;
        if (movingLine != null) {
            if (isVerticalLine(movingLine)) {
                lt = AugScrible.GESTURE_TYPE.VERTICAL_LINE;
                int newx = (int) event.getX();
                startP = new Point(newx, movingLine.getP1().y);
            } else {
                lt = AugScrible.GESTURE_TYPE.HORIZONTAL_LINE;
                int newy = (int) event.getY();
                startP = new Point(movingLine.getP1().x, newy);
            }
        } else {
            AugScrible s = augdraw.getCurrentScrible();
            lt = s == null ? GESTURE_TYPE.NONE : augdraw.getCurrentScrible().getGestureType();
        }
        
        AugLineImpl line;
        //float vwidth = Float.parseFloat(prefs.getString("VERTICAL_LINE_WIDTH", "9"));
        float vwidth = 9;
        //float hwidth = Float.parseFloat(prefs.getString("HORIZONTAL_LINE_WIDTH", "18"));
        float hwidth = 18;
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

    public List<AugLine> getLines() {
        return lines;
    }
    
    @Override
    public void updateCanvas() {
        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        //Log.d(TAG, "ejs update HF: " + lines.size() + " hc: " + hashCode());
        for (AugLine l : lines) {
            float temp_w = l.getWidth();
            p.setStrokeWidth( temp_w );
            augview.getCanvas().drawLine(l.getP1().x, l.getP1().y, l.getP2().x, l.getP2().y, augview.getPaint());
        }
        p.setStrokeWidth(orig_w);
    }

    @Override
    public void clear() {
        if (lines != null) lines.clear();
    }
    
    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() {

        Code code = JSONCoder.newCode();
        try {
            if (lines != null && !lines.isEmpty()) {
                CodeArray<Code> linesCode = JSONCoder.newArrayOfCode();
                code.put("lines", linesCode);
                for (AugLine l : lines) {
                    linesCode.add(l.getCode());
                }
            }
        } catch (CodeableException e) {
            Log.e(TAG, e.toString(), e);
        }
        
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        lines.clear();
        if (code.has("lines")) {
            @SuppressWarnings("unchecked")
            CodeArray<Code> linesCode = (CodeArray<Code>) code.getCodeArray("lines");
            for (Code lcode : linesCode) {
                AugLine l = new AugLineImpl();
                l.setCode(lcode);
                lines.add(l);
            }
        }
    }

    @Override
    public String getUIName() {

        return UI_NAME;
    }
    
    public static final AugiementFactory.Meta getMeta() {
        return new AugiementFactory.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return HorizonFeature.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }
        };
    }

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }
}
