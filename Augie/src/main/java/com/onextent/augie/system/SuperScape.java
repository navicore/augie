/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.system;

import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieScape;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class SuperScape {

    private final ViewGroup cntlLayout;
    private final AugieScape augieScape;
    private final View button;
    private final AugieActivity activity;
    private RealityScape camPreview;

    public SuperScape(AugieActivity a) {
        this.activity = a;
        this.augieScape = a.getAugieScape();
        this.button = a.getMenuButton();
        this.cntlLayout = a.getCamPrevLayout();
    }

    public void activate(AugCamera camera) throws AugCameraException {
      
        if (camPreview == null)
            configCamPreview(camera);
        else {
            boolean switchedOk = camPreview.setAugcamera(camera);
            if (!switchedOk) 
                configCamPreview(camera);
        }
    }

    public void deactivate() {

    }
    
    private void configCamPreview(AugCamera augcamera) throws AugCameraException {
        
        cntlLayout.removeAllViewsInLayout(); //todo: check for leaks, no idea about views cleaning up
    
        camPreview = new RealityScape((Activity) activity, augieScape);
        camPreview.setAugcamera(augcamera);
    
        cntlLayout.addView(camPreview); //bottom layer sees
    
        cntlLayout.addView((View) augieScape); //transparent top layer
   
        if (button != null) cntlLayout.addView(button);
    }
}
