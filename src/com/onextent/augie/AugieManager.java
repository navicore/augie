package com.onextent.augie;

import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.impl.AugCameraFactoryImpl;
import com.onextent.augie.impl.AugieViewImpl;
import com.onextent.augie.impl.AugiementFactoryImpl;

import android.content.Context;

/*
 * all this thing does is hide implementation classes
 */
public class AugieManager {
   
    private final AugCameraFactory camFactory;
    private final AugiementFactory augFactory;
    private final AugieView augView;
    
    public AugieManager(Context c) {
   
        camFactory = new AugCameraFactoryImpl();
        augView = new AugieViewImpl(c);
        augFactory = new AugiementFactoryImpl(augView);
    }
    
    public AugiementFactory getAugiementFactory() {
        return augFactory;
    }
     
    public AugCameraFactory getCameraFactory() {
        return camFactory;
    }
    
    public AugieView getAugView() {
        return augView;
    }
}
