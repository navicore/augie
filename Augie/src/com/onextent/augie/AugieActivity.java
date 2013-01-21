/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import android.app.Activity;

import com.onextent.android.codeable.EventManager;
import com.onextent.android.ui.CallbackActivity;


public interface AugieActivity extends CallbackActivity, EventManager {

    ModeManager getModeManager();
    
    int getOrientation();
    
    Activity getActivity();
}
