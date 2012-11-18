/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;

import java.util.Set;

import android.hardware.Camera;
import android.util.Log;

import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;

public class AugCamera implements Augiement {

	private Camera camera;
	private int camera_id;
	
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

	public void updateCanvas() {
		// TODO Auto-generated method stub

	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	// the camera preview should handle releasing the camera
	
	public void stop() {
		
	    releaseCamera();
	}

	public void resume() {

	}

	//todo: make front camera, rear camera, etc... someday tethering...
	//todo: make front camera, rear camera, etc... someday tethering...
	//todo: make front camera, rear camera, etc... someday tethering...
	//todo: make front camera, rear camera, etc... someday tethering...
	//todo: make front camera, rear camera, etc... someday tethering...
    public static final String AUGIE_NAME = "AUGIE/FEATURES/CAMERA";
    @Override
    public String getAugieName() {
        return AUGIE_NAME;
    }

    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
		camera_id = -1;  //default to first front facing camera
    }

    @Override
    public Set<String> getDependencyNames() {
        return null;
    }
}

