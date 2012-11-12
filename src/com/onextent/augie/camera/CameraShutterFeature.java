/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugmentedViewFeature;

public abstract class CameraShutterFeature implements AugmentedViewFeature {

    private static CameraShutterFeature sInstance;

    public static CameraShutterFeature getInstance(Context ctx, 
                                                    AugCamera c, 
                                                    AugDrawFeature d, 
                                                    SharedPreferences p) {

        if (sInstance == null) {

            int sdkVersion = Build.VERSION.SDK_INT;
            Log.d(TAG, "CameraShutterFeature sdkVersion=" + sdkVersion);
            if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
                sInstance = new SimpleCameraShutterFeature(ctx, c, d, p);
            } else  {
                sInstance = new SimpleCameraShutterFeature(ctx, c, d, p);
            }
        }
        return sInstance;
    }
}
