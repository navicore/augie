package com.onextent.augie.camera.impl;

import java.util.Set;

import android.content.Context;
import android.view.SurfaceHolder;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augieable;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.augie.camera.CameraName;
import com.onextent.util.codeable.Code;

public class BackCamera implements AugCamera {
    
    /*
     * wrapper to provide no-arg constructor for factory
     */
    
    public static final CameraName CAMERA_NAME = AbstractPhoneCamera.BACK_CAMERA_NAME;
    
    private final AugCamera augcamera;
    
    protected BackCamera(CameraName name) {
        
        augcamera = AbstractPhoneCamera.getInstance(name);
    }

    public BackCamera() {
        
        this(CAMERA_NAME);
    }

    @Override
    public AugieName getAugieName() {
        return AUGIENAME;
    }

    @Override
    public void open() throws AugCameraException {
        augcamera.open();
    }

    @Override
    public void close() throws AugCameraException {
        augcamera.close();
    }

    @Override
    public void setPreviewDisplay(SurfaceHolder holder) throws AugCameraException {
        augcamera.setPreviewDisplay(holder);
    }

    @Override
    public void startPreview() throws AugCameraException {
        augcamera.startPreview();
    }

    @Override
    public void stopPreview() throws AugCameraException {
        augcamera.stopPreview();
    }

    @Override
    public void takePicture(AugShutterCallback shutter, 
                            AugPictureCallback raw, 
                            AugPictureCallback jpeg) {
        
        augcamera.takePicture(shutter, raw, jpeg);
    }

    @Override
    public AugCameraParameters getParameters() {
        return augcamera.getParameters();
    }

    @Override
    public Code getCode() {
        return augcamera.getCode();
    }

    @Override
    public void setCode(Code state) {
        augcamera.setCode(state);
    }

    @Override
    public void edit(Context context, EditCallback cb) throws AugieableException {
        // TODO Auto-generated method stub
        augcamera.edit(null, cb);
    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
    }

    static private final class BackMeta implements Augieable.Meta {

        @Override
        public String getTitle() {
            return "Back Camera";
        }

        @Override
        public String getDescription() {
            return "Interal Camera Facing Away from You";
        }

        @Override
        public String getCatagory() {
            return "Camera";
        }
        
    }
    static private final Augieable.Meta mymeta;
    static {
        mymeta = new BackMeta();
    }
    @Override
    public Meta getMeta() {
        return mymeta;
    }

    //NOOP stubs just here so that dependency manager can 
    // give cameras to shutter and config augiements
    @Override
    public void updateCanvas() { }

    @Override
    public void clear() { }

    @Override
    public void stop() { }

    @Override
    public void resume() { }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException { }

    @Override
    public Set<AugieName> getDependencyNames() { return null; }

    @Override
    public CameraName getCameraName() {
        
        return CAMERA_NAME;
    }
}
