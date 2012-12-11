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
import com.onextent.augie.AugieName;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.TouchShutter;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.JSONCoder;

public class ModeManagerImpl implements ModeManager {

    private static final String TAG = Augiement.TAG;
    AugieStore store;
    private static final String CURRENT_MODE_KEY_KEY = "CURRENT/MODE_KEY";
    private static final String MODE_KEY_FLASH = "MODE/SYSTEM/FLASH";

    private Mode currentMode;
    private List<Mode> allModes;
    
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
    public void onCreate(Context context) throws AugieStoreException {

        if (store != null) throw new AugieStoreException("store already init");
        store = new AugieStore(context);
        store.open();
        init();
    }

    @Override
    public void stop() {
        Log.d(TAG, "ModeManager.stop");
        try {
            saveAllModes();
        } catch (AugieStoreException e) {
            Log.e(TAG, e.toString(), e);
        }
        if (store != null) {
            store.close();
            store = null;
        }
        allModes = null;
    }

    @Override
    public void setCurrentMode(Mode mode) throws AugieException {
        Log.d(TAG, "setCurrentMode " + mode.getAugieName());
        if (currentMode != null) {
           currentMode.deactivate(); 
        }
        currentMode = mode;
        store.replaceContent(CURRENT_MODE_KEY_KEY, mode.getAugieName().toString());
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
    public Mode getMode(AugieName augieName) {
        
        Mode m = new ModeImpl(this);
        String m_ser = store.getContentString(augieName.toString());
        if (m_ser != null) {
            Log.d(TAG, "initializing mode from store");
            try {
                //m.setCode(new JSONObject(m_ser));
                m.setCode(JSONCoder.newCode(m_ser));
                return m;
            } catch (CodeableException e) {
                Log.e(TAG, e.toString(), e);
            }
        } 
        Log.w(TAG, "no mode " + augieName);
        return null;
    }

    private void init() throws AugieStoreException {

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
    public List<Mode> getModes() throws AugieStoreException {
        
        if (store == null) return null;
        
        if (allModes != null) return allModes;
       
        List<Mode> list = new ArrayList<Mode>();
        Cursor c = store.getContent("MODE/%");
        if (c.moveToFirst()) {
            do {
                Mode mode = new ModeImpl(this);
                String codeStr = c.getString(0);
                try {
                    //JSONObject code = new JSONObject(codeStr);
                    Code code = JSONCoder.newCode(codeStr);
                    mode.setCode(code);
                    list.add(mode);
                } catch (CodeableException e) {
                    Log.e(TAG, e.toString(), e);
                    c.close();
                    throw new AugieStoreException(e);
                }
                
            } while (c.moveToNext());
        }
        c.close();
        allModes = list;
        return list;
    }

    private void primeDbWithModes() throws AugieStoreException {
        primeDefaultMode();
        primeFlashMode();
    }

    private void primeDefaultMode() {
        ModeImpl mode = new ModeImpl(this);
        mode.setName("Default");
        mode.setAugieName(new ModeName(MODE_KEY_DEFAULT));
        
        AugDrawFeature drawer = new AugDrawFeature();
        mode.addAugiement(drawer);

        mode.addAugiement(new HorizonFeature());
        
        mode.addAugiement(new HorizonCheckFeature());

        //mode.addAugiement(new FrameLevelerFeature());

        mode.addAugiement(new TouchShutter());

        ShakeResetFeature shakeReseter = new ShakeResetFeature();
        mode.addAugiement(shakeReseter);
        
        addMode(mode);
    }

    private void primeFlashMode() {
        ModeImpl mode = new ModeImpl(this);
        mode.setName("Flash");
        mode.setAugieName(new ModeName(MODE_KEY_FLASH));
        
        AugDrawFeature drawer = new AugDrawFeature();
        mode.addAugiement(drawer);

        mode.addAugiement(new HorizonFeature());
        
        mode.addAugiement(new HorizonCheckFeature());

        //mode.addAugiement(new FrameLevelerFeature());

        mode.addAugiement(new TouchShutter());

        ShakeResetFeature shakeReseter = new ShakeResetFeature();
        mode.addAugiement(shakeReseter);
        
        addMode(mode);       
    }

    @Override
    public void deleteMode(AugieName augieName) {
        if (getCurrentMode().equals(augieName)) {
            try {
                setCurrentMode(getMode(new ModeName(MODE_KEY_DEFAULT)));
            } catch (AugieException e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        store.remove(augieName.toString());
        allModes = null;
    }
    
    @Override
    public void addMode(Mode mode) {
        allModes = null;
        saveMode(mode);
    }
   
    private void saveAllModes() throws AugieStoreException {
        for (Mode m : getModes()) {
            saveMode(m);
        }
    }
    private void saveMode(Mode mode) {
        String mode_ser = mode.getCode().toString();
        store.replaceContent(mode.getAugieName().toString(), mode_ser);       
    }

    //ugh
    @Override
    public int getCurrentModeIdx() {
        int idx = 0;
        try {
            int sz = getModes().size();
            for (int i = 0; i < sz; i++) {
                Mode m = getModes().get(i);
                Mode c = getCurrentMode();
                if (m != null && 
                    c != null &&
                    m.getAugieName().equals(c.getAugieName())) return i;
            }
        } catch (AugieStoreException e) {
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
}
