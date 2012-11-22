/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.Set;

import org.json.JSONObject;

import com.onextent.augie.AugieView;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.impl.AugDrawBase.HLine;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

public class HorizonCheckFeature extends LevelerBase {

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

    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }
}