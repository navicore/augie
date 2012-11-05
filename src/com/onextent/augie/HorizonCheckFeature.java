/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import com.onextent.augie.AugDrawBase.HLine;
import com.onextent.augie.AugDrawBase.Line;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class HorizonCheckFeature extends LevelerBase {

    public HorizonCheckFeature(AugmentedView v, HorizonFeature h, Context context, SharedPreferences p) {
        super(v, h, context, p);
    }
   
    public void updateBmp() {
        if (!prefs.getBoolean("HORIZON_CHECKER_ENABLED", true)) return;

        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (Line line : horizonFeture.getLines()) {
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
