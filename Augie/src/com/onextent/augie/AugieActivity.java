/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import android.view.View;
import android.view.ViewGroup;

import com.onextent.android.codeable.EventManager;
import com.onextent.android.ui.CallbackActivity;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.system.SuperScape;


public interface AugieActivity extends CallbackActivity, EventManager {

    ViewGroup getCamPrevLayout();
    
    ModeManager getModeManager();
    
    int getOrientation();
    
    AugCameraFactory getCameraFactory();
    
    AugiementFactory getAugiementFactory();
    
    AugieScape getAugieScape();

    View getMenuButton();

    SuperScape getSuperScape();
}
