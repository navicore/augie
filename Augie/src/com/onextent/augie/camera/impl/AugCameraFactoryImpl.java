/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.CameraName;

public class AugCameraFactoryImpl implements AugCameraFactory {

	private final Map<CameraName, AugCamera> cameras;
	private final Map<CameraName, Class<? extends AugCamera>> cameraClasses;
	
	public AugCameraFactoryImpl() {
	    
	    cameras = new HashMap<CameraName, AugCamera>();
	    cameraClasses = new HashMap<CameraName, Class<? extends AugCamera>>();
	}
	
    @Override
	public AugCamera getCamera(CameraName name) throws AugCameraException {
        
        if (name == null)  {
            throw new AugCameraException("no camera name");
        }
        
        AugCamera camera = null;
        
        if (cameras.containsKey(name)) {
            
            Log.d(TAG, "getting already instantiated camera: " + name);
            camera = cameras.get(name);
            
        } else {
            
            Log.d(TAG, "constructing new camera instance: " + name);
            camera = createCamera(name);
            if (camera != null) cameras.put(name, camera);
        }
        
        if (camera == null) throw new AugCameraException("no camera");

        return camera;
	}
	
    private AugCamera createCamera(CameraName name) {
        
        AugCamera camera = null;
        
        if (cameraClasses.containsKey(name)) {
            
            Class<? extends AugCamera> c = cameraClasses.get(name);
            try {
                camera = c.newInstance();
            } catch (InstantiationException e) {
                Log.e(TAG, "can not create camera", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "can not access camera", e);
            }
        }
            
        return camera;
    }

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

	public void updateCanvas() {
	}

    @Override
	public void clear() {
	}

    @Override
	public void stop() {

	    for (AugCamera c : cameras.values()) {
	        try {
                c.close();
            } catch (AugCameraException e) {
                Log.e(TAG, "can not close camera", e);
            }
	    }
	    cameras.clear();
	}

    @Override
	public void resume() {

	}

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
    }

    @Override
    public void registerCamera(int id, CameraName augname, String name) {
        AugCamera c = new CameraImpl(id, augname, name);
        cameras.put(augname, c);
    }
   
    /**
     * warnging, this isn't baked at all, cameras registered by class don't
     * show up in the getCameras set yet
     */
    @Override
    public void registerCamera(Class<? extends AugCamera> camclass, CameraName name) {
        cameraClasses.put(name, camclass);
    }

    @Override
    public Set<CameraName> getCameraNames() {

        return cameraClasses.keySet();
    }

    @Override
    public Code getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(Code code) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Collection<AugCamera> getCameras() {
        
        return cameras.values();
    }

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }
}