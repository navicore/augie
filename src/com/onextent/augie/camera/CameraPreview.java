/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;

import java.io.IOException;

import com.onextent.augie.AugmentedViewFeature;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;

enum TOUCH_STATE {NOSTATE, SHOOTING, ZOOMING};

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder holder;
    private final AugCamera augcamera;
    
    protected final String TAG = AugmentedViewFeature.TAG;
   
    public CameraPreview(Activity context, AugCamera c) {
        super(context);
        augcamera = c;
        
        holder = getHolder();
        holder.addCallback(this);
        //holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
        	Camera c = augcamera.getCamera();
            c.setPreviewDisplay(holder);
            c.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        	Camera c = augcamera.getCamera();
        	if (c != null) {
        		c.stopPreview();
        		augcamera.releaseCamera();
        	}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        Camera c = augcamera.getCamera();
        // stop preview before making changes
        if (c != null)
        try {
            c.stopPreview();
        // start preview with new settings
        try {
        	// set preview size and make any resize, rotate or
        	// reformatting changes here
        	Camera.Parameters p = c.getParameters();
        	//p.setPreviewSize(w, h);  // like the changed w h are nonsense, 
        	//need to check supported preview sizes List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        	c.setParameters(p);

            c.setPreviewDisplay(holder);
            c.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
    }
}
