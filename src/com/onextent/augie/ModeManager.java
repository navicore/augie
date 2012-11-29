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
    public static final String MODE_KEY_DEFAULT = "MODE/SYSTEM/DEFAULT";
    private static final String MODE_KEY_FLASH = "MODE/SYSTEM/FLASH";

    private Mode currentMode;
    private List<Mode> allModes;

    public void onCreate(Context context) throws AugieStoreException {

        if (store != null) throw new AugieStoreException("store already init");
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
        store.replaceContent(CURRENT_MODE_KEY_KEY, mode.getAugieName().toString());
    }

    public Mode getCurrentMode() {
        return currentMode;
    }
    
    public Mode getMode(AugieName augieName) {
        
        Mode m = new Mode();
        String m_ser = store.getContentString(augieName.toString());
        if (m_ser != null) {
            try {
                m.setCode(new JSONObject(m_ser));
                return m;
            } catch (JSONException e) {
                Log.e(TAG, e.toString(), e);
            }
        } 
        return null;
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
        currentMode = getMode(new ModeName(currentMode_key));
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
        Mode mode = new Mode();
        mode.setName("Default");
        mode.setAugieName(new ModeName(MODE_KEY_DEFAULT));
        addMode(mode);
    }

    private void primeFlashMode() {
        Mode mode = new Mode();
        mode.setName("Flash");
        mode.setAugieName(new ModeName(MODE_KEY_FLASH));
        addMode(mode);       
    }

    public void deleteMode(AugieName augieName) {
        if (getCurrentMode().equals(augieName)) {
            setCurrentMode(getMode(new ModeName(MODE_KEY_DEFAULT)));
        }
        store.remove(augieName.toString());
        allModes = null;
    }
    
    public void addMode(Mode mode) {
        allModes = null;
        String mode_ser = mode.getCode().toString();
        Log.d(TAG, mode_ser);
        store.replaceContent(mode.getAugieName().toString(), mode_ser);       
    }

    //ugh
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
}
