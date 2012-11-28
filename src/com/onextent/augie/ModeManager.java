package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.onextent.augie.impl.AugieStore;

public class ModeManager {

    private static final String TAG = Augiement.TAG;
    AugieStore store;
    private static final String CURRENT_MODE_KEY_KEY = "CURRENT/MODE_KEY";
    private static final String MODE_KEY_DEFAULT = "MODE/DEFAULT";
    private static final String MODE_KEY_FLASH = "MODE/FLASH";

    private Mode currentMode;
    private List<Mode> allModes;

    public void onCreate(Context context) throws AugieStoreException {

        store = new AugieStore(context);
        store.open();
        initCurrentMode();
    }

    public void stop() {
        if (store != null) {
            store.close();
            store = null;
        }

    }

    public void setCurrentMode(Mode mode) {
        currentMode = mode;
        store.replaceContent(CURRENT_MODE_KEY_KEY, mode.getAugieName());
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    private void initCurrentMode() throws AugieStoreException {

        String currentMode_key = store.getContentString(CURRENT_MODE_KEY_KEY);
        if (currentMode_key == null) {
            Log.d(TAG, "no current mode key, initializing.");
            primeDbWithModes();
            currentMode_key = MODE_KEY_DEFAULT;
            store.replaceContent(CURRENT_MODE_KEY_KEY, currentMode_key);
        } 
        Log.d(TAG, "current mode key: " + currentMode_key);
        currentMode = new Mode();
        try {
            String currentMode_ser = store.getContentString(currentMode_key);
            Log.d(TAG, "current mode key: " + currentMode_key + " json: " + currentMode_ser);
            currentMode.setCode(new JSONObject(currentMode_ser));
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
            throw new AugieStoreException(e);
        }
    }
    
    public List<Mode> getModes() throws AugieStoreException {
        
        if (allModes != null) return allModes;
       
        List<Mode> list = new ArrayList<Mode>();
        Cursor c = store.getContent("MODE/%");
        if (c.moveToFirst()) {
            do {
                Mode mode = new Mode();
                String codeStr = c.getString(0);
                try {
                    JSONObject code = new JSONObject(codeStr);
                    mode.setCode(code);
                    list.add(mode);
                } catch (JSONException e) {
                    Log.e(TAG, e.toString(), e);
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
        Mode mode = new Mode();
        mode.setName("Default");
        mode.setAugieName(MODE_KEY_DEFAULT);
        addMode(mode);
    }

    private void primeFlashMode() {
        Mode mode = new Mode();
        mode.setName("Flash");
        mode.setAugieName(MODE_KEY_FLASH);
        addMode(mode);       
    }

    public void addMode(Mode mode) {
        allModes = null;
        String mode_ser = mode.getCode().toString();
        Log.d(TAG, mode_ser);
        store.replaceContent(mode.getAugieName(), mode_ser);       
    }

    //ugh
    public int getCurrentModeIdx() {
        int idx = 0;
        try {
            int sz = getModes().size();
            for (int i = 0; i < sz; i++) {
                Mode m = getModes().get(i);
                if (m.getAugieName().equals(getCurrentMode().getAugieName())) return i;
            }
        } catch (AugieStoreException e) {
            Log.e(TAG, e.toString(), e);
        }
        return idx;
    }
}
