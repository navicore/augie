package com.onextent.augie.camera;

import android.view.SurfaceHolder;

import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;
import com.onextent.util.codeable.CodeableName;

public interface AugCamera extends Augiement {

    static final String TAG = Augiement.TAG;
    public static final CodeableName AUGIENAME = new AugiementName("AUGIE/FEATURES/CAMERA");
    public static final String CAMERA_ID_KEY = "camera_id";
    
    void open() throws AugCameraException;
    
    void close() throws AugCameraException;
    
    void setPreviewDisplay(SurfaceHolder holder) throws AugCameraException;
    
    void startPreview() throws AugCameraException;
    
    void stopPreview() throws AugCameraException;
    
    void takePicture(AugShutterCallback shutter, 
                            AugPictureCallback raw, 
                            AugPictureCallback jpeg);
    
    AugCameraParameters getParameters();
    
    //called by open and by settings UI
    //  1 - updates camera with whatever is in code
    //  2 - refreshes code with the real/final camera settings in case
    //      camera impl overrides
    void applyParameters() throws AugCameraException;
    
    int getId();
    
    String getName(); //ui name
    CameraName getCameraName(); //camera factory name

    void initParams();
}
