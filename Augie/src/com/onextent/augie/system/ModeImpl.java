/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.system;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeArray;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;

public class ModeImpl implements Codeable, Mode {
    
    static final String KEY_NAME = Codeable.UI_NAME_KEY;
    static final String KEY_AUGIENAME = Codeable.CODEABLE_NAME_KEY;
    
    static final String KEY_CAMERA = "camera";
    static final String KEY_CODE = "code";
    
    static final String KEY_AUGIEMENTS = "augiements";

    private String name;
    private CodeableName augieName;
    
    private AugCamera camera;
    
    private final ModeManager modeManager;
    
    private final Map<CodeableName, Augiement> augiements;


    public ModeImpl(ModeManager mm) {
        this(mm, null, null);
    }

    public ModeImpl(ModeManager mm, String cn, AugCamera c) {
        modeManager = mm;
        augiements = new HashMap<CodeableName, Augiement>();
        if (cn != null) augieName = new CodeableName(cn){};
        camera = c;
    }

    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
            code.put(KEY_NAME, name);
            code.put(KEY_AUGIENAME, augieName.toString());

            Code cameraCode = camera.getCode();
            code.put(KEY_CAMERA, cameraCode);
            
            if (!augiements.isEmpty()) {
                CodeArray<Code> features = JSONCoder.newArrayOfCode();
                code.put(KEY_AUGIEMENTS, features);
                
                for (Augiement f : augiements.values()) {
                    Code fjson = JSONCoder.newCode();
                    features.add(fjson);
                    fjson.put(KEY_AUGIENAME, f.getCodeableName().toString());
                    Code fcode = f.getCode();
                    if (fcode != null)
                        fjson.put(KEY_CODE, fcode);
                }
            }
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        try {
            if (camera != null) throw new CodeableException("camera already set");
            name = code.getString(KEY_NAME);
            augieName = new ModeName(code.getString(KEY_AUGIENAME));
            
            if (!code.has(KEY_CAMERA)) throw new CodeableException("no camera");
            Code cameraCode = code.get(KEY_CAMERA);
            if (cameraCode == null) {
                throw new CodeableException("no camera code");
            }
            
            CodeableName cameraName = cameraCode.getCodeableName();
            
            camera = modeManager.getCameraFactory().getCamera(cameraName);
            if (camera == null) 
                throw new CodeableException("no camera: " + cameraName);
            
            camera.setCode(cameraCode);
            
            if (code.has(KEY_AUGIEMENTS)) {
                @SuppressWarnings("unchecked")
                CodeArray<Code> features = (CodeArray<Code>) code.getCodeArray(KEY_AUGIEMENTS);
                for (int i = 0; i < features.length(); i++) {
                    Code acode = features.get(i);
                    CodeableName fName = new AugiementName(acode.getString(KEY_AUGIENAME));
                    Augiement f = null;
                    try {
                    	f = modeManager.getAugiementFactory().newInstance(fName);
                    } catch(Exception e) {
                    	Log.w(TAG, "can not find " + fName + " augiement");
                    	continue;
                    }
                    if (acode.has(KEY_CODE)) {
                        Code fCode = acode.get(KEY_CODE);
                        if (fCode != null) f.setCode(fCode);
                    }
                    augiements.put(f.getCodeableName(), f);
                }
            }
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public CodeableName getCodeableName() {
        return augieName;
    }

    @Override
    public AugCamera getCamera() {
        if (camera == null) throw new java.lang.NullPointerException("no camera");
        return camera;
    }

    @Override
    public void setCamera(AugCamera camera) throws AugCameraException {
        try {
            if (this.camera != null) {
                camera.close();
            }
            this.camera = camera;
        } catch (Exception e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public void removeAugiement(Augiement a) {
        if (a == null) {
            augiements.clear();
        } else {
            augiements.remove(a.getCodeableName());
        }
    }      
    @Override
    public void addAugiement(Augiement a) {
        augiements.put(a.getCodeableName(), a);
    }   
    @Override
    public Map<CodeableName, Augiement> getAugiements() {
        return augiements;
    }

    @Override
    public void activate() throws AugieException {
        
        AugLog.d( "activating mode " + getCodeableName());
        
        ModeManager mm = modeManager;
        AugieScape v = mm.getAugieScape();
        v.stop();
        v.removeFeature(null);
        for (Augiement f : augiements.values()) {
            AugLog.d( "    activate node add feature " + f.getCodeableName());
            v.addFeature(f);
        }
        AugCamera c = getCamera();
        
        if (c == null) throw new AugiementException("null camera");
        v.addFeature(c);
        
        SuperScape superScape = new SuperScape(mm);
        superScape.activate(c);
        //v.resume(); //some augiements like facerecog must 'resume' after preview has started
    }

    @Override
    public void deactivate() throws AugieException {
        AugLog.d( "deactivate node " + getCodeableName());
        try {
            modeManager.saveMode(this);
            camera.close();
        } catch (CodeableException e) {
            throw new AugieException(e) {
                private static final long serialVersionUID = 660929239042327743L;};
        }
        AugieScape v = modeManager.getAugieScape();
        v.removeFeature(null);
    }

    @Override
    public void setCodeableName(CodeableName modeName) {
       
        augieName = modeName;
    }
}
