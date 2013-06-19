/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.system;

import com.onextent.augie.AugSysLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;

import android.content.Context;
import android.content.res.Configuration;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

enum TOUCH_STATE {NOSTATE, SHOOTING, ZOOMING};

public class RealityScape extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder holder;
    private final AugieScape augieScape;
    
    private AugCamera augcamera;

    public RealityScape(Context context) {
        super(context);
        throw new java.lang.UnsupportedOperationException(); // ha ha
    }

    @SuppressWarnings("deprecation")
    public RealityScape(Context context, AugieScape augieScape) {
        super(context);
        this.augieScape = augieScape;

        holder = getHolder();
        holder.addCallback(this);
        //@#%#! documentation is wroooong.  you need this for 2.x
        //it is deprecated but NOT ignored on 2.3.3
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (augcamera == null) throw new java.lang.NullPointerException("camer not set");
        AugSysLog.d( "surfaceCreated");
        try {
            augcamera.open();

            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            {
                augcamera.setDisplayOrientation(90);
            }
            augcamera.setPreviewDisplay(holder);
            augieScape.resume();
            augcamera.startPreview();
        } catch (AugCameraException e) {
            AugSysLog.e( "Error surfaceCreated: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (augcamera == null) throw new java.lang.NullPointerException("camer not set");
        AugSysLog.d( "surfaceDestroyed");
        if (augcamera != null) {
            try {
                augcamera.stopPreview();
                augcamera.close();
            } catch (AugCameraException e) {
                AugSysLog.e( "Error surfaceDestroyed: " + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (augcamera == null) throw new java.lang.NullPointerException("camer not set");
        AugSysLog.d( "surfaceChanged");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
    }
    
    public boolean setAugcamera(AugCamera augcamera) throws AugCameraException {
        //todo: if preview is active, see if camera id is the same and swap without stopping
        //todo: if preview is active, see if camera id is the same and swap without stopping
        //todo: if preview is active, see if camera id is the same and swap without stopping
        //todo: if preview is active, see if camera id is the same and swap without stopping
        boolean r = augcamera.open(this.augcamera);
        this.augcamera = augcamera;
        return r;
    }
}
