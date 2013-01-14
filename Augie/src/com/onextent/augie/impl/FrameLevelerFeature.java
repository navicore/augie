/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.impl.AugDrawBase.HLine;
import com.onextent.augie.impl.AugDrawBase.VLine;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;

public class FrameLevelerFeature extends LevelerBase {
    
    private List<AugLine> frameLines;
    private boolean isInitialized = false;

    public static final String DESCRIPTION = "Draws a square frame level around the picture preview.";
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/FRAME_LEVELER");
    public static final String UI_NAME = "Frame Level";
    
    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }
    
    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
        frameLines = new ArrayList<AugLine>();
    }
    
    public boolean initFrame() {
        //warning, buggy mess, redo, needs to be able to have view resized during life
        if (isInitialized) return true;
        //Bitmap bmp = augview.getBitmap();
        //if (bmp == null) return false;
        float vwidth = Float.parseFloat(prefs.getString("VERTICAL_LINE_WIDTH", "9"));
        float hwidth = Float.parseFloat(prefs.getString("HORIZONTAL_LINE_WIDTH", "18"));
        AugLineImpl top = new HLine(0, augieScape.getWidth(), 0, hwidth);
        AugLineImpl bottom = new HLine(0, augieScape.getWidth(), augieScape.getHeight(), hwidth);
        AugLineImpl left = new VLine(0, augieScape.getHeight(), 0, vwidth);
        AugLineImpl right = new VLine(0, augieScape.getHeight(), augieScape.getWidth(), vwidth);
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

        Paint p = augieScape.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (AugLine line : frameLines) {
            float temp_w = line.getWidth();
            p.setStrokeWidth( temp_w );
            AugLine cline;
            if (line instanceof HLine) {
                cline = correctHorizontal(line);
            } else {
                cline = correctVertical(line);
            }
            augieScape.getCanvas().drawLine(cline.getP1().x, cline.getP1().y, 
                    cline.getP2().x, cline.getP2().y, augieScape.getPaint());
        }
        p.setColor(old_color);
        p.setStrokeWidth(orig_w);
    }

    @Override
    public Code getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(Code code) {
        // TODO Auto-generated method stub
    }
    
    public static final Meta META = 

        new Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return FrameLevelerFeature.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }

            @Override
            public String getDescription() {

                return DESCRIPTION;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                
                return null;
            }
        };

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta getMeta() {
    
        return META;
    }
}