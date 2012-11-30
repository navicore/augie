package com.onextent.augie;

import java.util.List;

import com.onextent.augie.impl.ModeImpl;

import android.content.Context;


public interface ModeManager {

    public abstract void onCreate(Context context) throws AugieStoreException;

    public abstract void stop();

    public abstract void setCurrentMode(ModeImpl mode);

    public abstract ModeImpl getCurrentMode();

    public abstract ModeImpl getMode(AugieName augieName);

    public abstract List<ModeImpl> getModes() throws AugieStoreException;

    public abstract void deleteMode(AugieName augieName);

    public abstract void addMode(ModeImpl mode);
    
    public abstract ModeImpl newMode();

    //ugh
    public abstract int getCurrentModeIdx();

}