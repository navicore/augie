package com.onextent.augie;

import com.onextent.augie.camera.AugCamera;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * manages the layout of views comprised of:
 *   - realityscape (camera preview)
 *   - augiescape (stacks of augiements)
 *   - ui button
 *   
 *   - usage (by Mode):
 *       construct
 *       add augiements
 *       activate
 *       ...
 *       ...
 *       ...
 *       deactivate
 *     
 */
public class SuperScape {


    private final ViewGroup layout;
    private final AugieScape augieScape;
    private final Button button;
    private final Activity activity;

    public SuperScape(Activity activity, AugieScape augieScape, Button button, ViewGroup layout) {
        this.activity = activity;
        this.augieScape = augieScape;
        this.button = button;
        this.layout = layout;
    }

    public void activate(AugCamera camera) {
       
        configCamPreview(camera);
    }

    public void deactivate() {

    }
    
    private void configCamPreview(AugCamera augcamera) {
        
        layout.removeAllViewsInLayout(); //todo: check for leaks, no idea about views cleaning up
    
        View camPreview = new RealityScape(activity, augcamera);
    
        layout.addView(camPreview); //bottom layer sees
    
        layout.addView((View) augieScape); //transparent top layer
   
        if (button != null) layout.addView(button);
    }
}
