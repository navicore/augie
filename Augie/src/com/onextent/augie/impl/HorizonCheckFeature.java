/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.Set;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;

public class HorizonCheckFeature extends LevelerBase {
    
    private static String DESCRIPTION = "A level for the horizon augiment.";
    
    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
    }
    
    @Override
    public void updateCanvas() {

        Paint p = augieScape.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        p.setColor(Color.RED);
        for (AugLine line : horizonFeature.getLines()) {
            float temp_w = line.getWidth();
            p.setStrokeWidth( temp_w );
            AugLine cline;
            if (!AugDrawBase.isVerticalLine(line)) {
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
    
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/HORIZON_LEVEL");
    public static final String UI_NAME = "Level";
    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
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
    
                return HorizonCheckFeature.class;
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

                return deps;
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
