/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.Set;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;

import android.graphics.Color;
import android.graphics.Paint;

public class HorizonCheckFeature extends LevelerBase {
    
    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
    }
    
    @Override
    public void updateCanvas() {

        Paint p = augview.getPaint();
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
            augview.getCanvas().drawLine(cline.getP1().x, cline.getP1().y, 
                    cline.getP2().x, cline.getP2().y, augview.getPaint());
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

    @Override
    public String getUIName() {

        return UI_NAME;
    }
    
    public static final AugiementFactory.Meta getMeta() {
        return new AugiementFactory.Meta() {

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
        };
    }
}
