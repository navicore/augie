/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera;

import android.view.SurfaceHolder;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;

public interface AugCamera extends Augiement {

    public static final CodeableName AUGIENAME = new AugiementName("AUGIE/FEATURES/CAMERA");
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_NAME_KEY = "cameraName";
    public static final String CAMERA_UINAME_KEY = "name";
    
    void open() throws AugCameraException;
    
    void close() throws AugCameraException;
    
    void setPreviewDisplay(SurfaceHolder holder) throws AugCameraException;
    
    void startPreview() throws AugCameraException;
    
    void stopPreview() throws AugCameraException;
    
    void takePicture(AugShutterCallback shutter, 
                            AugPictureCallback raw, 
                            AugPictureCallback jpeg) throws AugCameraException;
    
    AugCameraParameters getParameters();
    
    //called by open and by settings UI
    //  1 - updates camera with whatever is in code
    //  2 - refreshes code with the real/final camera settings in case
    //      camera impl overrides
    void applyParameters() throws AugCameraException;
    
    int getId();
    
    String getName(); //ui name
    CodeableName getCodeableName(); //camera factory name

    void initParams();
    
    String flatten();

    void focus(AugFocusCallback cb) throws AugCameraException;
    
    void addPreviewCallback(AugPreviewCallback cb);
    void removePreviewCallback(AugPreviewCallback cb);
    //void addPreviewCallbackWithBuffer(AugPreviewCallback cb);
    void addCallbackBuffer(byte[] b);

    boolean isOpen();

    CodeableName getCameraName();

    void startFaceDetection();

    void stopFaceDetection();

    void setFaceDetectionListener(AugFaceListener faceListener);

    void setDisplayOrientation(int i);

    //return true if this (this) now holds the impl ref of augcamera(that)
    boolean open(AugCamera augcamera) throws AugCameraException;
}
