package com.onextent.augie.camera;

import android.view.SurfaceHolder;

import com.onextent.augie.Augieable;
import com.onextent.augie.Augiement;

public interface AugCamera extends Augieable {

    public static final String TAG = Augiement.TAG;
    
    public void open() throws AugCameraException;
    
    public void close() throws AugCameraException;
    
    public void setPreviewDisplay(SurfaceHolder holder) throws AugCameraException;
    
    public void startPreview() throws AugCameraException;
    
    public void stopPreview() throws AugCameraException;
    
    public void takePicture(AugShutterCallback shutter, 
                            AugPictureCallback raw, 
                            AugPictureCallback jpeg);
    
    public AugCameraParameters getParameters();
}
