package com.onextent.augie.camera;

import java.util.Set;

import com.onextent.augie.Augiement;

public interface AugCameraFactory extends Augiement {

    public String TAG = Augiement.TAG;
    
    public AugCamera getCamera(CameraName name);
    
    public Set<CameraName> getCameraNames();
    
    public void registerCamera(Class<? extends AugCamera> camclass, CameraName name);
}
