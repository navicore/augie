/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;

import android.os.Build;
import android.util.Log;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;

/*
 * factory to allow pre-icecreamsandwich touchfocus devices
 */
public abstract class CameraShutterFeature implements Augiement {

    public static CameraShutterFeature getInstance( AugCamera c, AugDrawFeature d, AugieView v ) {
        
        CameraShutterFeature sInstance;

        int sdkVersion = Build.VERSION.SDK_INT;
        Log.d(TAG, "CameraShutterFeature sdkVersion=" + sdkVersion);
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            sInstance = new SimpleCameraShutterFeature(v.getContext(), c, d);
        } else  {
            sInstance = new TouchFocusShutterFeature(v.getContext(), c, d, v);
        }
        return sInstance;
    }
}
