package com.onextent.augie.impl;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.onextent.augie.AugieException;
import com.onextent.augie.AugieName;
import com.onextent.augie.Augiement;
import com.onextent.augie.RealityScape;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraName;
import com.onextent.augie.data.Codable;

import android.util.Log;

public class ModeImpl implements Codable, Mode {
    
    static final String KEY_NAME = "name";
    static final String KEY_AUGIENAME = "augieName";
    
    static final String KEY_CAMERA = "camera";
    static final String KEY_CODE = "code";
    
    static final String KEY_AUGIEMENTS = "augiements";

    private String name;
    private AugieName augieName;
    
    private AugCamera camera;
    
    private final ModeManager modeManager;
    


    public ModeImpl(ModeManager mm) {
        modeManager = mm;
    }

    @Override
    public JSONObject getCode() {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY_NAME, name);
            json.put(KEY_AUGIENAME, augieName.toString());
            if (camera != null) {
                JSONObject cameraJson = new JSONObject();
                json.put(KEY_CAMERA, cameraJson);
                cameraJson.put(KEY_AUGIENAME, camera.getCameraName().toString());
                cameraJson.put(KEY_CODE, camera.getCode());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
        }
        return json;
    }

    @Override
    public void setCode(JSONObject code) {
        try {
            name = code.getString(KEY_NAME);
            augieName = new ModeName(code.getString(KEY_AUGIENAME));
            if (code.has(KEY_CAMERA)) {
                JSONObject cameraJson = code.getJSONObject(KEY_CAMERA);
                CameraName cameraName = new CameraName(cameraJson.getString(KEY_AUGIENAME));
                JSONObject cameraCode = cameraJson.getJSONObject(KEY_CODE);
                camera = modeManager.getCameraFactory().getCamera(cameraName);
                if (camera != null && cameraCode != null) {
                    camera.setCode(cameraCode);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#getAugieName()
     */
    @Override
    public AugieName getAugieName() {
        return augieName;
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#setAugieName(com.onextent.augie.AugieName)
     */
    @Override
    public void setAugieName(AugieName augieName) {
        this.augieName = augieName;
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#getCamera()
     */
    @Override
    public AugCamera getCamera() {
        return camera;
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#setCamera(com.onextent.augie.camera.AugCamera)
     */
    @Override
    public void setCamera(AugCamera camera) {
        this.camera = camera;
    }
    
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#removeAugiement(com.onextent.augie.Augiement)
     */
    @Override
    public void removeAugiement(Augiement a) {
        throw new java.lang.UnsupportedOperationException();
    }      
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#addAugiement(com.onextent.augie.Augiement)
     */
    @Override
    public void addAugiement(Augiement a) {
        throw new java.lang.UnsupportedOperationException();
    }   
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.Mode#getAugiements()
     */
    @Override
    public Set<Augiement> getAugiements() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void activate() throws AugieException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deactivate() throws AugieException {
        // TODO Auto-generated method stub
        
    }
    
}
