/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;

import com.onextent.augie.Augiement;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;

enum TOUCH_STATE {NOSTATE, SHOOTING, ZOOMING};

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    protected final String TAG = Augiement.TAG;

    private final SurfaceHolder holder;
    private final AugCamera augcamera;

    public CameraPreview(Context context) {
        super(context);
        throw new java.lang.UnsupportedOperationException(); // ha ha
    }

    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, AugCamera ac) {
        super(context);
        augcamera = ac;

        holder = getHolder();
        holder.addCallback(this);
        //@#%#! documentation is wroooong.  you need this for 2.x
        //it is deprecated but NOT ignored on 2.3.3
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            augcamera.open();
            augcamera.setPreviewDisplay(holder);
        } catch (AugCameraException e) {
            Log.e(TAG, "Error surfaceCreated: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (augcamera != null) {
            try {
                augcamera.stopPreview();
                augcamera.close();
            } catch (AugCameraException e) {
                Log.e(TAG, "Error surfaceDestroyed: " + e.getMessage());
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            augcamera.startPreview();
        } catch (AugCameraException e) {
            Log.e(TAG, "Error surfaceChanged: " + e.getMessage());
        }
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }
    }
}
