package com.onextent.augie;

import java.util.Set;

import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableName;

public interface Mode extends Codeable {
    
    static final String KEY_MODE_UI_NAME = "uiname";

    String getName();

    void setName(String name);

    AugCamera getCamera();

    void setCamera(AugCamera camera) throws AugCameraException;

    void removeAugiement(Augiement a);

    void addAugiement(Augiement a);

    Set<Augiement> getAugiements();
    
    void activate() throws AugieException; //create and config SuperScape
    void deactivate() throws AugieException;

    void setCodeableName(CodeableName modeName);
}
