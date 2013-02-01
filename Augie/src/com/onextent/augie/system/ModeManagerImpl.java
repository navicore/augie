/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.system;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.view.View;
import android.view.ViewGroup;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.android.store.CodeStore;
import com.onextent.augie.AugSysLog;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieStore;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.ments.Draw;
import com.onextent.augie.ments.GPS;
import com.onextent.augie.ments.Horizon;
import com.onextent.augie.ments.HorizonCheck;
import com.onextent.augie.ments.PinchZoom;
import com.onextent.augie.ments.shutter.Shutter;
import com.onextent.augie.ments.shutter.TouchShutter;

public class ModeManagerImpl implements ModeManager {

    CodeStore store;
    private static final String CURRENT_MODE_KEY_KEY = "CURRENT/MODE_KEY";
    private static final String MODE_KEY_FLASH = "MODE/SYSTEM/FLASH";
    private static final String MODE_KEY_STREET = "MODE/SYSTEM/STREET";
    private static final String MODE_KEY_SELF = "MODE/SYSTEM/SELF";

    private Mode currentMode;
    private List<Code> allModeCode;

    private final AugCameraFactory cameraFactory;
    private final AugiementFactory augiementFactory;
    private final Activity activity;
    private final AugieScape augieScape;

    //todo: evil, get rid
    //todo: evil, get rid
    //todo: evil, get rid

    private final ViewGroup camPrevLayout;
    private final View button;

    public ModeManagerImpl(Activity a, AugieScape av, AugCameraFactory cf, 
            AugiementFactory af, ViewGroup l, View b) {

        activity = a;
        augieScape = av;
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
    public View getButton() {
        return button;
    }   

    @Override
    public void onCreate(Context c) throws AugieStoreException, CodeableException {

        //if (store != null) 
        //throw new java.lang.IllegalStateException("already init");

        if (store == null) 
            store = AugieStore.getCodeStore();
        //init();
    }

    @Override
    public void resume() throws AugieStoreException, CodeableException {
        //onCreate(context);
        init();
    }

    @Override
    public void stop() {
        if (store == null) {
            throw new java.lang.IllegalStateException("already stopped");
        }
        AugSysLog.d( "ModeManager.stop");
        if (currentMode != null) {
            try {
                currentMode.deactivate();
                currentMode = null;
            } catch (AugieException e) {
                AugSysLog.e( e.toString(), e);
            } 
        }
        allModeCode = null;
        //store = null;
    }

    @Override
    public void setCurrentMode(Mode mode) throws AugieException {
        if (currentMode != null) {
            currentMode.deactivate(); 
        }
        if (mode == null) {
            throw new AugieException("mode is null") {
                private static final long serialVersionUID = 3508820766680012985L;};
        }
        currentMode = mode;
        AugSysLog.d( "setCurrentMode " + mode.getCodeableName());
        store.replaceContent(CURRENT_MODE_KEY_KEY, mode.getCodeableName().toString());
        currentMode.activate();
    }

    @Override
    public Mode getCurrentMode() {
        if (currentMode == null)
            try {
                init();
            } catch (AugieStoreException e) {
                AugSysLog.e(e);
            } catch (CodeableException e) {
                AugSysLog.e(e);
            }
        return currentMode;
    }

    @Override
    public Mode newMode() {
        return new ModeImpl(this);
    }

    @Override
    public Mode getMode(CodeableName augieName) throws CodeableException {

        AugSysLog.d("ejs getMode " + augieName);
        Mode m = new ModeImpl(this);
        Code code = store.getContentCode(augieName);
        if (code == null) throw new CodeableException("mode not found") {
            private static final long serialVersionUID = 7886435403395937189L;};

            AugSysLog.d( "initializing mode from store");
            m.setCode(code);
            return m;
    }

    private void init() throws AugieStoreException, CodeableException {

        String currentMode_key = store.getContentString(CURRENT_MODE_KEY_KEY);
        if (currentMode_key == null) {
            AugSysLog.d( "no current mode key, initializing.");
            primeDbWithModes();
            currentMode_key = MODE_KEY_DEFAULT;
            store.replaceContent(CURRENT_MODE_KEY_KEY, currentMode_key);
        } 
        AugSysLog.d( "current mode key: " + currentMode_key);
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
                    AugSysLog.e( e.toString(), e);
                    c.close();
                    throw new AugieStoreException(e);
                }

            } while (c.moveToNext());
        }
        c.close();
        allModeCode = list;
        return list;
    }

    //todo: move this into app

    private void primeDbWithModes() throws AugieStoreException, CodeableException {
        primeDefaultMode();
        primeFlashMode();
        primeStreetMode();
        primeSelfMode();
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

        Draw drawer = new Draw();
        mode.addAugiement(drawer);

        mode.addAugiement(new Horizon());

        mode.addAugiement(new HorizonCheck());

        //mode.addAugiement(new HistogramFeature());

        mode.addAugiement(new Shutter());
        mode.addAugiement(new TouchShutter());

        mode.addAugiement(new PinchZoom());
        mode.addAugiement(new GPS());

        //ShakeResetFeature shakeReseter = new ShakeResetFeature();
        //mode.addAugiement(shakeReseter);

        addMode(mode);
    }

    private void primeFlashMode() throws CodeableException {
        AugCamera camera;
        try {
            camera = cameraFactory.getCamera(AugCameraFactory.AUGIE_BACK_CAMERA);
            if (camera == null) throw new CodeableException("no camera");
            camera.open();
            AugCameraParameters p = camera.getParameters();
            if (p == null) throw new CodeableException("no camera parameters");
            if (!p.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_ON))
                return;
            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

            ModeImpl mode = new ModeImpl(this, MODE_KEY_FLASH, camera);
            mode.setName("Flash");

            Draw drawer = new Draw();
            mode.addAugiement(drawer);

            mode.addAugiement(new Horizon());

            mode.addAugiement(new HorizonCheck());

            //mode.addAugiement(new PinchZoom());

            mode.addAugiement(new Shutter());
            mode.addAugiement(new TouchShutter());
            mode.addAugiement(new GPS());

            //ShakeReset shakeReseter = new ShakeReset();
            //mode.addAugiement(shakeReseter);

            addMode(mode);       
            camera.close();
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
    }

    private void primeStreetMode() throws CodeableException {
        AugCamera camera;
        try {
            camera = cameraFactory.getCamera(AugCameraFactory.AUGIE_BACK_CAMERA);
            if (camera == null) throw new CodeableException("no camera");
            camera.open();
            AugCameraParameters p = camera.getParameters();
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            if (!p.getSupportedColorModes().contains(Camera.Parameters.EFFECT_MONO))
                return;
            p.setColorMode(Camera.Parameters.EFFECT_MONO);

            ModeImpl mode = new ModeImpl(this, MODE_KEY_STREET, camera);
            mode.setName("Street Mono Infinity");

            Draw drawer = new Draw();
            mode.addAugiement(drawer);

            mode.addAugiement(new Horizon());

            mode.addAugiement(new HorizonCheck());

            //mode.addAugiement(new PinchZoom());

            mode.addAugiement(new Shutter());
            mode.addAugiement(new TouchShutter());
            mode.addAugiement(new GPS());

            //ShakeReset shakeReseter = new ShakeReset();
            //mode.addAugiement(shakeReseter);

            addMode(mode);       
            camera.close();
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
    }

    private void primeSelfMode() throws CodeableException {
        AugCamera camera;
        try {
            camera = cameraFactory.getCamera(AugCameraFactory.AUGIE_FRONT_CAMERA);
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
        if (camera == null) return;

        ModeImpl mode = new ModeImpl(this, MODE_KEY_SELF, camera);
        mode.setName("Self");

        Draw drawer = new Draw();
        mode.addAugiement(drawer);

        mode.addAugiement(new Horizon());

        mode.addAugiement(new HorizonCheck());

        //mode.addAugiement(new PinchZoom());

        mode.addAugiement(new Shutter());
        mode.addAugiement(new TouchShutter());
        mode.addAugiement(new GPS());

        //ShakeReset shakeReseter = new ShakeReset();
        //mode.addAugiement(shakeReseter);

        addMode(mode);       
    }

    @Override
    public void deleteMode(CodeableName augieName) throws CodeableException {
        if (getCurrentMode().equals(augieName)) {
            try {
                setCurrentMode(getMode(new ModeName(MODE_KEY_DEFAULT)));
            } catch (AugieException e) {
                AugSysLog.e( e.toString(), e);
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
        AugSysLog.d( "saved mode " + mode.getCodeableName());
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
            AugSysLog.e( e.toString(), e);
        } catch (CodeableException e) {
            AugSysLog.e( e.toString(), e);
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
        return augieScape;
    }
    public AugiementFactory getAugiementFactory() {
        return augiementFactory;
    }

    @Override
    public CodeStore getStore() {
        return store;
    }

    @Override
    public List<String> getModeNameStrings() throws AugieStoreException, CodeableException {

        List<String> names = new ArrayList<String>();
        List<Code> modeCode = getAllModeCode();
        for (Code c : modeCode) {
            String n = c.getString(Codeable.UI_NAME_KEY);
            names.add(n);
        }
        return names;
    }
    
    @Override
    public int getCurrentModePos(List<String> names) {
        String currentModeUIName = getCurrentMode().getName();
        return names.indexOf(currentModeUIName); 
    }
}
