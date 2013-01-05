package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

import com.onextent.augie.AugieException;
import com.onextent.augie.AugieStore;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.PinchZoom;
import com.onextent.augie.camera.TouchShutter;
import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.JSONCoder;
import com.onextent.util.store.CodeStore;

public class ModeManagerImpl implements ModeManager {

    private static final String TAG = Augiement.TAG;
    CodeStore store;
    private static final String CURRENT_MODE_KEY_KEY = "CURRENT/MODE_KEY";
    private static final String MODE_KEY_FLASH = "MODE/SYSTEM/FLASH";

    private Mode currentMode;
    private List<Code> allModeCode;
    
    private final AugCameraFactory cameraFactory;
    private final AugiementFactory augiementFactory;
    private final Activity activity;
    private final AugieScape augview;
    
    //todo: evil, get rid
    //todo: evil, get rid
    //todo: evil, get rid
    
    private final ViewGroup camPrevLayout;
    private final Button button;
    
    public ModeManagerImpl(Activity a, AugieScape av, AugCameraFactory cf, 
            AugiementFactory af, ViewGroup l, Button b) {
       
        activity = a;
        augview = av;
        cameraFactory = cf;
        augiementFactory = af;
        camPrevLayout = l;
        button = b;
    }
    
    @Override
    public ViewGroup getCamPrevLayout() {
        return camPrevLayout;
    }
    @Override
    public Button getButton() {
        return button;
    }   

    @Override
    public void onCreate(Context context) throws AugieStoreException, CodeableException {

        if (store != null) 
            throw new java.lang.IllegalStateException("already init");
        store = AugieStore.getCodeStore();
        init();
    }

    @Override
    public void stop() {
        if (store == null) {
            throw new java.lang.IllegalStateException("already stopped");
            //return;
        }
        Log.d(TAG, "ModeManager.stop");
        if (currentMode != null) {
            try {
                currentMode.deactivate();
            } catch (AugieException e) {
                Log.d(TAG, e.toString(), e);
            } 
        }
        allModeCode = null;
    }

    @Override
    public void setCurrentMode(Mode mode) throws AugieException {
        if (currentMode != null) {
           currentMode.deactivate(); 
        }
        currentMode = mode;
        if (mode == null) {
            throw new AugieException("mode is null") {
                private static final long serialVersionUID = 3508820766680012985L;};
        }
        Log.d(TAG, "setCurrentMode " + mode.getCodeableName());
        store.replaceContent(CURRENT_MODE_KEY_KEY, mode.getCodeableName().toString());
        currentMode.activate();
    }

    @Override
    public Mode getCurrentMode() {
        return currentMode;
    }
    
    @Override
    public Mode newMode() {
        return new ModeImpl(this);
    }

    @Override
    public Mode getMode(CodeableName augieName) throws CodeableException {
        
        Mode m = new ModeImpl(this);
        Code code = store.getContentCode(augieName);
        if (code == null) throw new CodeableException("mode not found") {
            private static final long serialVersionUID = 7886435403395937189L;};
            
        Log.d(TAG, "initializing mode from store");
        m.setCode(code);
        return m;
    }

    private void init() throws AugieStoreException, CodeableException {

        String currentMode_key = store.getContentString(CURRENT_MODE_KEY_KEY);
        if (currentMode_key == null) {
            Log.d(TAG, "no current mode key, initializing.");
            primeDbWithModes();
            currentMode_key = MODE_KEY_DEFAULT;
            store.replaceContent(CURRENT_MODE_KEY_KEY, currentMode_key);
        } 
        Log.d(TAG, "current mode key: " + currentMode_key);
        currentMode = getMode(new ModeName(currentMode_key));
    }
    
    @Override
    public Code getModeCode(CodeableName augieName) throws AugieStoreException {
        
        Code code;
        try {
            code = store.getContentCode(augieName);
        } catch (CodeableException e) {
            throw new AugieStoreException(e);
        }
        return code;
    }
    
    @Override
    public List<Code> getAllModeCode() throws AugieStoreException {
       
        if (store == null) return null;
        
        if (allModeCode != null) return allModeCode;
       
        List<Code> list = new ArrayList<Code>();
        Cursor c = store.getContentCursor("MODE/%");
        if (c.moveToFirst()) {
            do {
                String codeStr = c.getString(0);
                try {
                    Code code = JSONCoder.newCode(codeStr);
                    list.add(code);
                } catch (CodeableException e) {
                    Log.e(TAG, e.toString(), e);
                    c.close();
                    throw new AugieStoreException(e);
                }
                
            } while (c.moveToNext());
        }
        c.close();
        allModeCode = list;
        return list;
    }

    private void primeDbWithModes() throws AugieStoreException, CodeableException {
        primeDefaultMode();
        primeFlashMode();
    }

    private void primeDefaultMode() throws CodeableException {
        AugCamera camera;
        try {
            camera = cameraFactory.getCamera(AugCameraFactory.AUGIE_BACK_CAMERA);
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
        if (camera == null) throw new CodeableException("no camera");
        ModeImpl mode = new ModeImpl(this, MODE_KEY_DEFAULT, camera);
        mode.setName("Default");
        
        AugDrawFeature drawer = new AugDrawFeature();
        mode.addAugiement(drawer);

        mode.addAugiement(new HorizonFeature());
        
        mode.addAugiement(new HorizonCheckFeature());

        //mode.addAugiement(new FrameLevelerFeature());

        mode.addAugiement(new TouchShutter());

        mode.addAugiement(new PinchZoom());
        
        ShakeResetFeature shakeReseter = new ShakeResetFeature();
        mode.addAugiement(shakeReseter);
        
        addMode(mode);
    }

    private void primeFlashMode() throws CodeableException {
        AugCamera camera;
        try {
            camera = cameraFactory.getCamera(AugCameraFactory.AUGIE_BACK_CAMERA);
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
        if (camera == null) throw new CodeableException("no camera");
        ModeImpl mode = new ModeImpl(this, MODE_KEY_FLASH, camera);
        mode.setName("Flash");
        
        AugDrawFeature drawer = new AugDrawFeature();
        mode.addAugiement(drawer);

        mode.addAugiement(new HorizonFeature());
        
        mode.addAugiement(new HorizonCheckFeature());

        //mode.addAugiement(new FrameLevelerFeature());
        
        mode.addAugiement(new PinchZoom());

        mode.addAugiement(new TouchShutter());

        ShakeResetFeature shakeReseter = new ShakeResetFeature();
        mode.addAugiement(shakeReseter);
        
        addMode(mode);       
    }

    @Override
    public void deleteMode(CodeableName augieName) throws CodeableException {
        if (getCurrentMode().equals(augieName)) {
            try {
                setCurrentMode(getMode(new ModeName(MODE_KEY_DEFAULT)));
            } catch (AugieException e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        store.remove(augieName.toString());
        allModeCode = null;
    }
    
    @Override
    public void addMode(Mode mode) throws CodeableException {
        allModeCode = null;
        saveMode(mode);
    }
  
    @Override
    public void saveMode(Mode mode) throws CodeableException {
        if (store == null || mode == null) return; //too late
        Code code = mode.getCode();
        store.replaceContent(mode.getCodeableName(), code);       
        Log.d(TAG, "saved mode " + mode.getCodeableName());
    }

    //ugh
    @Override
    public int getCurrentModeIdx() {
        int idx = 0;
        try {
            int sz = getAllModeCode().size();
            for (int i = 0; i < sz; i++) {
                Code code = getAllModeCode().get(i);
                CodeableName icn = code.getCodeableName(Codeable.CODEABLE_NAME_KEY);
                Mode c = getCurrentMode();
                CodeableName ccn = c.getCodeableName();
                if (icn != null && 
                    ccn != null &&
                    icn.equals(ccn)) return i;
            }
        } catch (AugieStoreException e) {
            Log.e(TAG, e.toString(), e);
        } catch (CodeableException e) {
            Log.e(TAG, e.toString(), e);
        }
        return idx;
    }

    public AugCameraFactory getCameraFactory() {
        return cameraFactory;
    }
    public Activity getActivity() {
        return activity;
    }
    public AugieScape getAugieScape() {
        return augview;
    }
    public AugiementFactory getAugiementFactory() {
        return augiementFactory;
    }

    @Override
    public CodeStore getStore() {
        return store;
    }
}
