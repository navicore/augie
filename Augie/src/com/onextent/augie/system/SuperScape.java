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

    private final ViewGroup layout;
    private final AugieScape augieScape;
    private final View button;
    private final Activity activity;

    public SuperScape(Activity activity, AugieScape augieScape, View button, ViewGroup layout) {
        this.activity = activity;
        this.augieScape = augieScape;
        this.button = button;
        this.layout = layout;
    }

    public SuperScape(ModeManager mm) {
        this.activity = mm.getActivity();
        this.augieScape = mm.getAugieScape();
        this.button = mm.getButton();
        this.layout = mm.getCamPrevLayout();
    }

    public void activate(AugCamera camera) {
       
        configCamPreview(camera);
    }

    public void deactivate() {

    }
    
    private void configCamPreview(AugCamera augcamera) {
        
        layout.removeAllViewsInLayout(); //todo: check for leaks, no idea about views cleaning up
    
        View camPreview = new RealityScape(activity, augcamera, augieScape);
    
        layout.addView(camPreview); //bottom layer sees
    
        layout.addView((View) augieScape); //transparent top layer
   
        if (button != null) layout.addView(button);
    }
}
