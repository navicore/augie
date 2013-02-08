/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.fonecam;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.CameraMeta;

public class FoneCamFactory implements AugCameraFactory {

	//private final Map<CodeableName, AugCamera> cameras;
	private final Map<CodeableName, CameraMeta> camerasMeta;
	private final Map<CodeableName, Class<? extends AugCamera>> cameraClasses;
	
	public FoneCamFactory() {
	    
	    //cameras = new HashMap<CodeableName, AugCamera>();
	    camerasMeta = new HashMap<CodeableName, CameraMeta>();
	    cameraClasses = new HashMap<CodeableName, Class<? extends AugCamera>>();
	}
	
    @Override
	public AugCamera getCamera(CodeableName name) throws AugCameraException {
        
        if (name == null)  {
            throw new AugCameraException("no camera name");
        }
        
        AugCamera camera = null;
        
        if (camerasMeta.containsKey(name)) {
            
            AugLog.d( "getting cam by id: " + name);
            CameraMeta h = camerasMeta.get(name);
            camera = new FoneCam(h.getId(), h.getCn(), h.getUiname());
            
        } else {
            
            AugLog.d( "constructing new camera instance: " + name);
            camera = createCamera(name);
            //if (camera != null) cameras.put(name, camera);
        }
        
        if (camera == null) throw new AugCameraException("no camera: " + name);

        return camera;
	}
	
    private AugCamera createCamera(CodeableName name) {
        
        AugCamera camera = null;
        
        if (cameraClasses.containsKey(name)) {
            
            Class<? extends AugCamera> c = cameraClasses.get(name);
            try {
                camera = c.newInstance();
            } catch (InstantiationException e) {
                AugLog.e( "can not create camera", e);
            } catch (IllegalAccessException e) {
                AugLog.e( "can not access camera", e);
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

        /*
	    for (AugCamera c : cameras.values()) {
	        try {
                c.close();
            } catch (AugCameraException e) {
                AugLog.e( "can not close camera", e);
            }
	    }
	    cameras.clear();
         */
	    camerasMeta.clear();
	}

    @Override
	public void resume() {

	}

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
    }

    @Override
    public void registerCamera(int id, CodeableName cn, String name) {
        //AugCamera c = new CameraImpl(id, augname, name);
        //cameras.put(augname, c);
        camerasMeta.put(cn, new CameraMeta(id, cn, name));
    }
   
    /**
     * warnging, this isn't baked at all, cameras registered by class don't
     * show up in the getCameras set yet
     */
    @Override
    public void registerCamera(Class<? extends AugCamera> camclass, CodeableName name) {
        cameraClasses.put(name, camclass);
    }

    @Override
    public Set<CodeableName> getCodeableNames() {

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
    public Collection<CameraMeta> getCameras() {
        
        //return cameras.values();
        return camerasMeta.values();
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
