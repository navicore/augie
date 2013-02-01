/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.system;

import com.onextent.augie.AugieScape;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class SuperScape {

    private final ViewGroup cntlLayout;
    private final AugieScape augieScape;
    private final View button;
    private final Activity activity;

    public SuperScape(ModeManager mm) {
        this.activity = mm.getActivity();
        this.augieScape = mm.getAugieScape();
        this.button = mm.getButton();
        this.cntlLayout = mm.getCamPrevLayout();
    }

    public void activate(AugCamera camera) {
       
        configCamPreview(camera);
    }

    public void deactivate() {

    }
    
    private void configCamPreview(AugCamera augcamera) {
        
        cntlLayout.removeAllViewsInLayout(); //todo: check for leaks, no idea about views cleaning up
    
        View camPreview = new RealityScape(activity, augcamera, augieScape);
    
        cntlLayout.addView(camPreview); //bottom layer sees
    
        cntlLayout.addView((View) augieScape); //transparent top layer
   
        if (button != null) cntlLayout.addView(button);
    }
}
