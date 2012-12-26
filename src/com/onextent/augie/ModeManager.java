package com.onextent.augie;

import java.util.List;

import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.store.CodeStore;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;


public interface ModeManager {

    static final String MODE_KEY_DEFAULT = "MODE/SYSTEM/DEFAULT";
    
    abstract void onCreate(Context context) throws AugieStoreException, CodeableException;

    abstract void stop();

    abstract void setCurrentMode(Mode mode) throws AugieException;

    abstract Mode getCurrentMode();

    abstract Mode getMode(CodeableName augieName) throws CodeableException;

    abstract List<Code> getAllModeCode() throws AugieStoreException;

    abstract void deleteMode(CodeableName augieName) throws CodeableException;

    abstract void addMode(Mode mode) throws CodeableException;
    
    abstract Mode newMode();
    
    void saveMode(Mode mode) throws CodeableException;

    //ugh
    abstract int getCurrentModeIdx();

    AugCameraFactory getCameraFactory();

    Activity getActivity();

    AugieScape getAugieScape();

    AugiementFactory getAugiementFactory();
    
    CodeStore getStore();
    
    //this is extremely evil, get rid of these calls
    //this is extremely evil, get rid of these calls
    //this is extremely evil, get rid of these calls
    ViewGroup getCamPrevLayout();
    Button getButton();
}
