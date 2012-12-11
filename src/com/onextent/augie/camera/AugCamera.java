package com.onextent.augie.camera;

import android.view.SurfaceHolder;

import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;
import com.onextent.util.codeable.CodeableName;

public interface AugCamera extends Augiement {

    static final String TAG = Augiement.TAG;
    public static final CodeableName AUGIENAME = new AugiementName("AUGIE/FEATURES/CAMERA");
    
    CameraName getCameraName();
    
    void open() throws AugCameraException;
    
    void close() throws AugCameraException;
    
    void setPreviewDisplay(SurfaceHolder holder) throws AugCameraException;
    
    void startPreview() throws AugCameraException;
    
    void stopPreview() throws AugCameraException;
    
    void takePicture(AugShutterCallback shutter, 
                            AugPictureCallback raw, 
                            AugPictureCallback jpeg);
    
    AugCameraParameters getParameters();
}
