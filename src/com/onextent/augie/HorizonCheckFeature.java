/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.Set;

import com.onextent.augie.AugDrawBase.HLine;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.graphics.Color;
import android.graphics.Paint;

public class HorizonCheckFeature extends LevelerBase {

    public HorizonCheckFeature(HorizonFeature h) {
        super(h);
    }
   
    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
    }
    
    @Override
    public void updateCanvas() {
        if (!prefs.getBoolean("HORIZON_CHECKER_ENABLED", true)) return;

        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (AugLineImpl line : horizonFeature.getLines()) {
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
    
    public static final String AUGIE_NAME = "AUGIE/FEATURES/HORIZON_LEVEL";
    @Override
    public String getAugieName() {
        return AUGIE_NAME;
    }
}
