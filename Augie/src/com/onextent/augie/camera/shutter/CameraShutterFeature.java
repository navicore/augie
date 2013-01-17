/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;

import android.os.Build;
import android.util.Log;
import android.view.View.OnTouchListener;

import com.onextent.augie.Augiement;

/*
 * factory to allow pre-icecreamsandwich touchfocus devices
 */
public abstract class CameraShutterFeature implements Augiement, OnTouchListener {

    public static CameraShutterFeature getInstance( ) {
        
        CameraShutterFeature sInstance;

        int sdkVersion = Build.VERSION.SDK_INT;
        Log.d(TAG, "CameraShutterFeature sdkVersion=" + sdkVersion);
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            sInstance = new SimpleCameraShutterFeature();
        } else  {
            sInstance = new TouchFocusShutterFeature();
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
    abstract public boolean isShowFileSavedToast();
	abstract public void setShowFileSavedToast(boolean showFileSavedToast);
	abstract public String getPicturesDir();
	abstract public void setPicturesDir(String picturesDir);
	abstract public String getPicturesRoot();
	abstract public void setPicturesRoot(String picturesRoot);
    abstract public boolean isRegisterImageWithOS();
	abstract public void setRegisterImageWithOS(boolean r);
}
