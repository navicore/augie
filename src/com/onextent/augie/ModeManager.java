package com.onextent.augie;

import java.util.List;

import com.onextent.augie.camera.AugCameraFactory;

import android.app.Activity;
import android.content.Context;


public interface ModeManager {

    public static final String MODE_KEY_DEFAULT = "MODE/SYSTEM/DEFAULT";
    
    public abstract void onCreate(Context context) throws AugieStoreException;

    public abstract void stop();

    public abstract void setCurrentMode(Mode mode);

    public abstract Mode getCurrentMode();

    public abstract Mode getMode(AugieName augieName);

    public abstract List<Mode> getModes() throws AugieStoreException;

    public abstract void deleteMode(AugieName augieName);

    public abstract void addMode(Mode mode);
    
    public abstract Mode newMode();

    //ugh
    public abstract int getCurrentModeIdx();

    public AugCameraFactory getCameraFactory();

    public Activity getActivity();

    public AugieScape getAugview();

    public AugiementFactory getAugiementFactory();
    
}
