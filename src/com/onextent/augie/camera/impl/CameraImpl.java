package com.onextent.augie.camera.impl;

import java.util.Set;

import android.content.Context;
import android.view.SurfaceHolder;

import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.augie.camera.CameraName;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.JSONCoder;

public class CameraImpl implements AugCamera {
    
    private AugCamera augcamera;
    private CameraName cameraName;
    private String name;
  
    public CameraImpl(int id, CameraName augname, String name) {
    
        this.cameraName = augname;
        this.name = name;
        //if no valid id, the camera will be created by setCode(code)
        if (id >= 0) augcamera = AbstractPhoneCamera.getInstance(id);
    }

    public CameraImpl() {
        
        this(-1, null, null);
    }

    @Override
    public CameraName getCameraName() {
        return cameraName;
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
    public Code getCode() throws CodeableException {
        Code code = augcamera.getCode();
        if (code == null) {
            code = JSONCoder.newCode();
        }
        code.put("cameraName", cameraName);
        code.put("name", name);
        code.put(CAMERA_ID_KEY, getId());
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        int id = code.getInt(CAMERA_ID_KEY);
        if (augcamera != null) augcamera.setCode(code);
        else {
            augcamera = AbstractPhoneCamera.getInstance(id);
            if (augcamera == null) {
                throw new CodeableException("camera not found");
            }
        }
        cameraName = new CameraName(code.getString("cameraName"));
        name = code.getString("name");
    }

    @Override
    public void edit(Context context, EditCallback cb) throws AugieableException {
    
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean isEditable() {
        return false;
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
    public Set<CodeableName> getDependencyNames() { return null; }

    @Override
    public String getName() {
        
        return name;
    }

    @Override
    public int getId() {
        
        return augcamera.getId();
    }

    @Override
    public Meta getMeta() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void applyParameters() {
        augcamera.applyParameters();
    }

    @Override
    public CodeableName getCodeableName() {
        return augcamera.getCodeableName();
    }
}