/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.shutter;

import android.os.Build;
import android.view.View.OnTouchListener;

import com.onextent.augie.AugLog;
import com.onextent.augie.Augiement;

/*
 * factory to allow pre-icecreamsandwich touchfocus devices
 */
public abstract class AbstractTouchShutter implements Augiement, OnTouchListener {

    public static AbstractTouchShutter getInstance( ) {
        
        AbstractTouchShutter sInstance;

        int sdkVersion = Build.VERSION.SDK_INT;
        AugLog.d( "CameraShutterFeature sdkVersion=" + sdkVersion);
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            sInstance = new SimpleCameraShutter();
        } else  {
            sInstance = new TouchFocusShutter();
        }
        return sInstance;
    }
    
    abstract int getMeterAreaColor();
    abstract void setMeterAreaColor(int meterAreaColor);
    abstract int getFocusAreaColor();
    abstract void setFocusAreaColor(int focusAreaColor);
    
    abstract public boolean isAlways_set_focus_area();
    abstract public void setAlways_set_focus_area(boolean always_set_focus_area);
    abstract public int getTouchFocusSz();
    abstract public void setTouchFocusSz(int touchFocusSz);
}
