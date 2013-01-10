package com.onextent.augie.camera;

import java.util.Collection;
import java.util.Set;

import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;
import com.onextent.util.codeable.CodeableName;

public interface AugCameraFactory extends Augiement {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/CAMERA/FACTORY");
    public static final CameraName AUGIE_FRONT_CAMERA = new CameraName("AUGIE/FEATURES/CAMERA/FRONT_CAMERA");
    public static final CameraName AUGIE_BACK_CAMERA = new CameraName("AUGIE/FEATURES/CAMERA/BACK_CAMERA");
	
    AugCamera getCamera(CameraName cn) throws AugCameraException;
    
    Set<CameraName> getCameraNames();
    
    void registerCamera(Class<? extends AugCamera> camclass, CameraName name);
    
    void registerCamera(int id, CameraName augname, String name); //phone cameras

    Collection<AugCamera> getCameras();
}
