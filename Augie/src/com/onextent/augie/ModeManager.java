/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.store.CodeStore;


public interface ModeManager {

    static final CodeableName MODE_KEY_SYSTEM = new CodeableName("MODE/SYSTEM");
    static final CodeableName MODE_KEY_DEFAULT = new CodeableName(MODE_KEY_SYSTEM + "/DEFAULT");
    
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

    CodeStore getStore();
    
    List<String> getModeNameStrings() throws AugieStoreException,
            CodeableException;

    int getCurrentModePos(List<String> names);
}
