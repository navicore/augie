package com.onextent.augie.impl;

import java.util.HashSet;
import java.util.Set;

import com.onextent.augie.AugieException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.SuperScape;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.CameraName;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeArray;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.JSONCoder;

import android.util.Log;

public class ModeImpl implements Codeable, Mode {
    
    static final String KEY_NAME = Mode.KEY_MODE_UI_NAME;
    static final String KEY_AUGIENAME = Codeable.CODEABLE_NAME_KEY;
    
    static final String KEY_CAMERA = "camera";
    static final String KEY_CODE = "code";
    
    static final String KEY_AUGIEMENTS = "augiements";

    private String name;
    private CodeableName augieName;
    
    private AugCamera camera;
    
    private final ModeManager modeManager;
    
    private final Set<Augiement> augiements;


    public ModeImpl(ModeManager mm) {
        modeManager = mm;
        augiements = new HashSet<Augiement>();
        augieName = null;
        camera = null;
    }

    public ModeImpl(ModeManager mm, String cn, AugCamera c) {
        if (c == null) throw new java.lang.NullPointerException("no camera");
        modeManager = mm;
        augiements = new HashSet<Augiement>();
        if (cn != null) augieName = new CodeableName(cn){};
        camera = c;
    }

    @Override
    public Code getCode() {
        Code code = JSONCoder.newCode();
        try {
            code.put(KEY_NAME, name);
            code.put(KEY_AUGIENAME, augieName.toString());
            
            Code cameraJson = JSONCoder.newCode();
            code.put(KEY_CAMERA, cameraJson);
            cameraJson.put(KEY_AUGIENAME, camera.getCameraName().toString());
            cameraJson.put(KEY_CODE, camera.getCode());
            
            if (!augiements.isEmpty()) {
                CodeArray<Code> features = JSONCoder.newArrayOfCode();
                code.put(KEY_AUGIEMENTS, features);
                
                for (Augiement f : augiements) {
                    Code fjson = JSONCoder.newCode();
                    features.add(fjson);
                    fjson.put(KEY_AUGIENAME, f.getCodeableName().toString());
                    Code fcode = f.getCode();
                    if (fcode != null)
                        fjson.put(KEY_CODE, fcode);
                }
            }
        } catch (CodeableException e) {
            Log.e(TAG, e.toString(), e);
        }
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        try {
            name = code.getString(KEY_NAME);
            augieName = new ModeName(code.getString(KEY_AUGIENAME));
            if (!code.has(KEY_CAMERA)) throw new CodeableException("no camera");
            Code cameraJson = code.get(KEY_CAMERA);
            CameraName cameraName = new CameraName(cameraJson.getString(KEY_AUGIENAME));
            Code cameraCode = cameraJson.get(KEY_CODE);
            camera = modeManager.getCameraFactory().getCamera(cameraName);
            if (camera != null && cameraCode != null) {
                camera.setCode(cameraCode);
            }
            if (code.has(KEY_AUGIEMENTS)) {
                @SuppressWarnings("unchecked")
                CodeArray<Code> features = (CodeArray<Code>) code.getCodeArray(KEY_AUGIEMENTS);
                for (int i = 0; i < features.length(); i++) {
                    Code acode = features.get(i);
                    CodeableName fName = new AugiementName(acode.getString(KEY_AUGIENAME));
                    Augiement f = modeManager.getAugiementFactory().newInstance(fName);
                    if (f == null) throw new java.lang.NullPointerException("no feature from augiment factory");
                    if (acode.has(KEY_CODE)) {
                        Code fCode = acode.get(KEY_CODE);
                        if (fCode != null) f.setCode(fCode);
                    }
                    augiements.add(f);
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
        if (this.camera != null) {
            camera.close();
        }
        this.camera = camera;
    }
    
    @Override
    public void removeAugiement(Augiement a) {
        if (a == null) {
            augiements.clear();
        } else {
            augiements.remove(a);
        }
    }      
    @Override
    public void addAugiement(Augiement a) {
        augiements.add(a);
    }   
    @Override
    public Set<Augiement> getAugiements() {
        return augiements;
    }

    @Override
    public void activate() throws AugieException {
        
        Log.d(TAG, "activating mode " + getCodeableName());
        
        ModeManager mm = modeManager;
        AugieScape v = mm.getAugieScape();
        v.stop();
        v.removeFeature(null);
        for (Augiement f : augiements) {
            Log.d(TAG, "    activate node add feature " + f.getCodeableName());
            v.addFeature(f);
        }
        AugCamera c = getCamera();
        v.addFeature(c);
        v.resume();
        
        SuperScape superScape = new SuperScape(mm.getActivity(), v, mm.getButton(), mm.getCamPrevLayout());
        superScape.activate(c);
    }

    @Override
    public void deactivate() throws AugieException {
        Log.d(TAG, "deactivate node " + getCodeableName());
        try {
            camera.close();
            modeManager.saveMode(this);
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
