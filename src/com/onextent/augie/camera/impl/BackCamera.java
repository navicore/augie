package com.onextent.augie.camera.impl;

import org.json.JSONObject;

import android.content.Context;
import android.view.SurfaceHolder;

import com.onextent.augie.Augieable;
import com.onextent.augie.AugieableException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugShutterCallback;

public class BackCamera implements AugCamera {
    
    /*
     * wrapper to provide no-arg constructor for factory
     */
    
    public static final String AUGIE_NAME = AbstractPhoneCamera.BACK_CAMERA_NAME;
    
    private final AugCamera augcamera;
    
    protected BackCamera(String name) {
        
        augcamera = AbstractPhoneCamera.getInstance(name);
    }

    public BackCamera() {
        
        this(AUGIE_NAME);
    }

    @Override
    public String getAugieName() {
        return augcamera.getAugieName();
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
    public JSONObject getCode() {
        // TODO Auto-generated method stub
        return augcamera.getCode();
    }

    @Override
    public void setCode(JSONObject state) {
        // TODO Auto-generated method stub
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
}
