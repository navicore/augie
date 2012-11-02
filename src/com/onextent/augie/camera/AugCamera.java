/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;

import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.onextent.augie.AugmentedViewFeature;
import com.onextent.augie.testcamera.TestCameraActivity;

public class AugCamera implements AugmentedViewFeature {

	private Camera camera;
	private int camera_id;
	
	public AugCamera() {
		camera_id = -1;  //default to first front facing camera
	}
	
	public Camera getCamera() {
    	if (camera != null && camera_id == -1) return camera;
    	
        try {
        	if (camera != null) camera.release();
        	camera = null;
            camera = Camera.open();
            camera_id = -1;
        
        } catch (Exception e) {
            // todo: fail louder
        	Log.e(TAG, "no camera on get instance", e);
        }
        return camera;
	}
	public Camera getCamera(int id) {
		if (id == -1) return getCamera();
    	if (camera != null && id == camera_id) return camera;
    	
        try {
        	if (camera != null) camera.release();
        	camera = null;
            camera = Camera.open(id);
            camera_id = id;
        
        } catch (Exception e) {
            // todo: fail louder
        	Log.e(TAG, "no camera on get instance", e);
        }
        return camera;
	}
	
	public void releaseCamera() {
        	if (camera != null) {
        		camera.release();
        		camera = null;
        	}
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateBmp() {
		// TODO Auto-generated method stub

	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	// the camera preview should handle releasing the camera
	
	public void stop() {
		// TODO Auto-generated method stub

	}

	public void resume() {
		// TODO Auto-generated method stub

	}

}
