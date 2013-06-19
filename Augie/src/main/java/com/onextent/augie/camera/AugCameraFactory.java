/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera;

import java.util.Collection;
import java.util.Set;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;

public interface AugCameraFactory extends Augiement {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/CAMERA/FACTORY");
    public static final CodeableName AUGIE_FRONT_CAMERA = new CodeableName("AUGIE/FEATURES/CAMERA/FRONT_CAMERA");
    public static final CodeableName AUGIE_BACK_CAMERA = new CodeableName("AUGIE/FEATURES/CAMERA/BACK_CAMERA");
	
    AugCamera getCamera(CodeableName cameraName) throws AugCameraException;
    
    Set<CodeableName> getCodeableNames();
    
    void registerCamera(Class<? extends AugCamera> camclass, CodeableName name);
    
    void registerCamera(int id, CodeableName augname, String name); //phone cameras

    Collection<CameraMeta> getCameras();
}
