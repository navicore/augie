/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.AugDrawBase.HLine;
import com.onextent.augie.AugDrawBase.VLine;
import com.onextent.augie.AugDrawBase.Line;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class FrameLevelerFeature extends LevelerBase {
    
    private final List<Line> frameLines;
    private boolean isInitialized = false;

    public FrameLevelerFeature(AugmentedView v, HorizonFeature h, Context context, SharedPreferences p) {
        super(v, h, context, p);
        frameLines = new ArrayList<Line>();
    }
    private boolean init() {
        //warning, buggy mess, redo, needs to be able to have view resized during life
        if (isInitialized) return true;
        Bitmap bmp = augview.getBitmap();
        if (bmp == null) return false;
        float vwidth = Float.parseFloat(prefs.getString("VERTICAL_LINE_WIDTH", "9"));
        float hwidth = Float.parseFloat(prefs.getString("HORIZONTAL_LINE_WIDTH", "18"));
        Line top = new HLine(0, augview.getWidth(), 0, hwidth);
        Line bottom = new HLine(0, augview.getWidth(), augview.getHeight(), hwidth);
        Line left = new VLine(0, augview.getHeight(), 0, vwidth);
        Line right = new VLine(0, augview.getHeight(), augview.getWidth(), vwidth);
        frameLines.clear();
        frameLines.add(top);
        frameLines.add(bottom);
        frameLines.add(left);
        frameLines.add(right);
        isInitialized = true;
        return true;
    }
    
    public void updateBmp() {
        
        if (!prefs.getBoolean("FRAME_LEVELER_ENABLED", false)) return;
        if (!init()) return;

        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (Line line : frameLines) {
            float temp_w = line.width;
            p.setStrokeWidth( temp_w );
            Line cline;
            if (line instanceof HLine) {
                cline = correctHorizontal(line);
            } else {
                cline = correctVertical(line);
            }
            augview.getCanvas().drawLine(cline.p1.x, cline.p1.y, 
                    cline.p2.x, cline.p2.y, augview.getPaint());
        }
        p.setColor(old_color);
        p.setStrokeWidth(orig_w);
    }
}
