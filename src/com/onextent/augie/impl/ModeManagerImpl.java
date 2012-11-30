package com.onextent.augie.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.camera.AugCameraFactory;

public class ModeManagerImpl implements ModeManager {

    private static final String TAG = Augiement.TAG;
    AugieStore store;
    private static final String CURRENT_MODE_KEY_KEY = "CURRENT/MODE_KEY";
    public static final String MODE_KEY_DEFAULT = "MODE/SYSTEM/DEFAULT";
    private static final String MODE_KEY_FLASH = "MODE/SYSTEM/FLASH";

    private ModeImpl currentMode;
    private List<ModeImpl> allModes;
    
    private final AugCameraFactory cameraFactory;
    private final AugiementFactory augiementFactory;
    
    public ModeManagerImpl(AugCameraFactory cf, AugiementFactory af) {
        
        cameraFactory = cf;
        augiementFactory = af;
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#onCreate(android.content.Context)
     */
    @Override
    public void onCreate(Context context) throws AugieStoreException {

        if (store != null) throw new AugieStoreException("store already init");
        store = new AugieStore(context);
        store.open();
        initCurrentMode();
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#stop()
     */
    @Override
    public void stop() {
        if (store != null) {
            store.close();
            store = null;
        }
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#setCurrentMode(com.onextent.augie.Mode)
     */
    @Override
    public void setCurrentMode(ModeImpl mode) {
        currentMode = mode;
        store.replaceContent(CURRENT_MODE_KEY_KEY, mode.getAugieName().toString());
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#getCurrentMode()
     */
    @Override
    public ModeImpl getCurrentMode() {
        return currentMode;
    }
    
    @Override
    public ModeImpl newMode() {
        
        return new ModeImpl(cameraFactory, augiementFactory);
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#getMode(com.onextent.augie.AugieName)
     */
    @Override
    public ModeImpl getMode(AugieName augieName) {
        
        ModeImpl m = new ModeImpl(cameraFactory, augiementFactory);
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
    
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#getModes()
     */
    @Override
    public List<ModeImpl> getModes() throws AugieStoreException {
        
        if (allModes != null) return allModes;
       
        List<ModeImpl> list = new ArrayList<ModeImpl>();
        Cursor c = store.getContent("MODE/%");
        if (c.moveToFirst()) {
            do {
                ModeImpl mode = new ModeImpl(cameraFactory, augiementFactory);
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
        ModeImpl mode = new ModeImpl(cameraFactory, augiementFactory);
        mode.setName("Default");
        mode.setAugieName(new ModeName(MODE_KEY_DEFAULT));
        addMode(mode);
    }

    private void primeFlashMode() {
        ModeImpl mode = new ModeImpl(cameraFactory, augiementFactory);
        mode.setName("Flash");
        mode.setAugieName(new ModeName(MODE_KEY_FLASH));
        addMode(mode);       
    }

    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#deleteMode(com.onextent.augie.AugieName)
     */
    @Override
    public void deleteMode(AugieName augieName) {
        if (getCurrentMode().equals(augieName)) {
            setCurrentMode(getMode(new ModeName(MODE_KEY_DEFAULT)));
        }
        store.remove(augieName.toString());
        allModes = null;
    }
    
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#addMode(com.onextent.augie.Mode)
     */
    @Override
    public void addMode(ModeImpl mode) {
        allModes = null;
        String mode_ser = mode.getCode().toString();
        Log.d(TAG, mode_ser);
        store.replaceContent(mode.getAugieName().toString(), mode_ser);       
    }

    //ugh
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.ModeManager#getCurrentModeIdx()
     */
    @Override
    public int getCurrentModeIdx() {
        int idx = 0;
        try {
            int sz = getModes().size();
            for (int i = 0; i < sz; i++) {
                ModeImpl m = getModes().get(i);
                ModeImpl c = getCurrentMode();
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
