/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraFactory;

public class AugCameraFactoryImpl implements AugCameraFactory {

	private final Map<String, AugCamera> cameras;
	private final Map<String, Class<? extends AugCamera>> cameraClasses;
	
    public static final AugieName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/CAMERA/FACTORY");
    public static final String AUGIE_DEFAULT_CAMERA = "AUGIE/FEATURES/CAMERA/DEFAULT_CAMERA";
	
	private String currentCameraName;
	
	public AugCameraFactoryImpl() {
	    
	    cameras = new HashMap<String, AugCamera>();
	    cameraClasses = new HashMap<String, Class<? extends AugCamera>>();
	    currentCameraName = null;
       
	    //prime with to default camera impls
	    registerCamera(BackCamera.class, BackCamera.CAMERA_NAME);
        registerCamera(FrontCamera.class, FrontCamera.CAMERA_NAME);
	}
	
    @Override
	public AugCamera getCamera(String name) {
        
        AugCamera camera = null;
       
        if (name == null)  {
            
            if (currentCameraName == null) {
                //todo: if currentCameraName is null, run UI to select camera?
                name = AUGIE_DEFAULT_CAMERA;
            } else {
                name = AUGIE_DEFAULT_CAMERA;
            }
        }
        
        if (cameras.containsKey(name)) {
            
            camera = cameras.get(name);
            
        } else {
            
            camera = createCamera(name);
            if (camera != null) cameras.put(name, camera);
        }
        
        currentCameraName = name;

        return camera;
	}
	
    private AugCamera createCamera(String name) {
        
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
    public AugieName getAugieName() {
        return AUGIE_NAME;
    }

	public void updateCanvas() {
	}

    @Override
	public void clear() {
	}

    @Override
	public void stop() {

        /*
	    for (AugCamera c : cameras.values()) {
	        try {
                c.close();
            } catch (AugCameraException e) {
                Log.e(TAG, "can not close camera", e);
            }
	    }
         */
	}

    @Override
	public void resume() {

	}

    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
    }

    @Override
    public Set<AugieName> getDependencyNames() {
        return null;
    }
    
    @Override
    public void registerCamera(Class<? extends AugCamera> camclass, String name) {
        if (cameraClasses.isEmpty()) {
            cameraClasses.put(AUGIE_DEFAULT_CAMERA, camclass);
        }
        cameraClasses.put(name, camclass);
    }

    @Override
    public JSONObject getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(JSONObject state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void edit(Context context, EditCallback cb) {
        // TODO Auto-generated method stub
        if (cb != null) cb.done();
    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getCameraNames() {

        return cameraClasses.keySet();
    }
}
