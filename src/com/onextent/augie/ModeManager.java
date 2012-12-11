package com.onextent.augie;

import java.util.List;

import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.util.codeable.CodeableName;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;


public interface ModeManager {

    public static final String MODE_KEY_DEFAULT = "MODE/SYSTEM/DEFAULT";
    
    public abstract void onCreate(Context context) throws AugieStoreException;

    public abstract void stop();

    public abstract void setCurrentMode(Mode mode) throws AugieException;

    public abstract Mode getCurrentMode();

    public abstract Mode getMode(CodeableName augieName);

    public abstract List<Mode> getModes() throws AugieStoreException;

    public abstract void deleteMode(CodeableName augieName);

    public abstract void addMode(Mode mode);
    
    public abstract Mode newMode();

    //ugh
    public abstract int getCurrentModeIdx();

    public AugCameraFactory getCameraFactory();

    public Activity getActivity();

    public AugieScape getAugieScape();

    public AugiementFactory getAugiementFactory();
    
    //this is extremely evil, get rid of these calls
    //this is extremely evil, get rid of these calls
    //this is extremely evil, get rid of these calls
    public ViewGroup getCamPrevLayout();
    public Button getButton();
    
}
