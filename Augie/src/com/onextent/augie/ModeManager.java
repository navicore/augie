/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import java.util.List;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.store.CodeStore;
import com.onextent.augie.camera.AugCameraFactory;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public interface ModeManager {

    static final String MODE_KEY_DEFAULT = "MODE/SYSTEM/DEFAULT";
    
    void onCreate(Context context) throws AugieStoreException, CodeableException;

    void stop();
    void resume() throws AugieStoreException, CodeableException;

    void setCurrentMode(Mode mode) throws AugieException;

    Mode getCurrentMode();

    Mode getMode(CodeableName augieName) throws CodeableException;

    List<Code> getAllModeCode() throws AugieStoreException;
    
    Code getModeCode(CodeableName augieName) throws AugieStoreException;

    void deleteMode(CodeableName augieName) throws CodeableException;

    void addMode(Mode mode) throws CodeableException;
    
    Mode newMode();
    
    void saveMode(Mode mode) throws CodeableException;

    //ugh
    int getCurrentModeIdx();

    AugCameraFactory getCameraFactory();

    Activity getActivity();

    AugieScape getAugieScape();

    AugiementFactory getAugiementFactory();
    
    CodeStore getStore();
    
    //this is extremely evil, get rid of these calls
    //this is extremely evil, get rid of these calls
    //this is extremely evil, get rid of these calls
    ViewGroup getCamPrevLayout();
    View getButton();

    List<String> getModeNameStrings() throws AugieStoreException,
            CodeableException;

    int getCurrentModePos(List<String> names);
}
