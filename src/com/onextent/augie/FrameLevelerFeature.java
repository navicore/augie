/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.onextent.augie.AugDrawBase.HLine;
import com.onextent.augie.AugDrawBase.VLine;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public class FrameLevelerFeature extends LevelerBase {
    
    private List<AugLine> frameLines;
    private boolean isInitialized = false;

    public static final String AUGIE_NAME = "AUGIE/FEATURES/FRAME_LEVELER";
    @Override
    public String getAugieName() {
        return AUGIE_NAME;
    }
    
    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
        frameLines = new ArrayList<AugLine>();
    }
    
    public boolean initFrame() {
        //warning, buggy mess, redo, needs to be able to have view resized during life
        if (isInitialized) return true;
        Bitmap bmp = augview.getBitmap();
        if (bmp == null) return false;
        float vwidth = Float.parseFloat(prefs.getString("VERTICAL_LINE_WIDTH", "9"));
        float hwidth = Float.parseFloat(prefs.getString("HORIZONTAL_LINE_WIDTH", "18"));
        AugLineImpl top = new HLine(0, augview.getWidth(), 0, hwidth);
        AugLineImpl bottom = new HLine(0, augview.getWidth(), augview.getHeight(), hwidth);
        AugLineImpl left = new VLine(0, augview.getHeight(), 0, vwidth);
        AugLineImpl right = new VLine(0, augview.getHeight(), augview.getWidth(), vwidth);
        frameLines.clear();
        frameLines.add(top);
        frameLines.add(bottom);
        frameLines.add(left);
        frameLines.add(right);
        isInitialized = true;
        return true;
    }
    
    @Override
    public void updateCanvas() {
        
        if (!prefs.getBoolean("FRAME_LEVELER_ENABLED", false)) return;
        if (!initFrame()) return;

        Paint p = augview.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (AugLine line : frameLines) {
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
