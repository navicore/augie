/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import com.onextent.augie.AugDrawBase.HLine;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;

public class HorizonCheckFeature extends LevelerBase {

    public HorizonCheckFeature(AugieView v, HorizonFeature h, Context context, SharedPreferences p) {
        super(v, h, context, p);
    }
   
    @Override
    public void updateBmp() {
        if (!prefs.getBoolean("HORIZON_CHECKER_ENABLED", true)) return;

        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (AugLineImpl line : horizonFeture.getLines()) {
            float temp_w = line.getBorderWidth();
            p.setStrokeWidth( temp_w );
            AugLine cline;
            if (line instanceof HLine) {
                cline = correctHorizontal(line);
            } else {
                cline = correctVertical(line);
            }
            augview.getCanvas().drawLine(cline.getP1().x, cline.getP1().y, 
                    cline.getP2().x, cline.getP2().y, augview.getPaint());
        }
        p.setColor(old_color);
        p.setStrokeWidth(orig_w);
    }
}
