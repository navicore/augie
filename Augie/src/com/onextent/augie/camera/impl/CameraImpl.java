/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.impl;

import java.util.Set;

import android.support.v4.app.DialogFragment;
import android.view.SurfaceHolder;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugFocusCallback;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugPreviewCallback;
import com.onextent.augie.camera.AugShutterCallback;

public class CameraImpl implements AugCamera {
    
    private AugCamera augcamera;
    private String name;
    private CodeableName cname; //cameraname is different from codeable name
  
    public CameraImpl(int id, CodeableName cameraname, String name) {
   
        this.cname = cameraname;
        this.name = name;
        //if no valid id, the camera will be created by setCode(code)
        if (id >= 0) augcamera = AbstractPhoneCamera.getInstance(id);
    }

    public CameraImpl() {
        
        this(-1, null, null);
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
                            AugPictureCallback jpeg) throws AugCameraException {
        
        augcamera.takePicture(shutter, raw, jpeg);
    }

    @Override
    public AugCameraParameters getParameters() {
        return augcamera.getParameters();
    }

    @Override
    public Code getCode() throws CodeableException {
        Code icode = augcamera.getCode();
        Code code = JSONCoder.newCode();
        if (icode != null)
            code.put("impl", icode);
        code.put(cname);
        code.put(CAMERA_UINAME_KEY, name);
        code.put(CAMERA_ID_KEY, getId());
        return code;
    }

    private void release() throws CodeableException {
        
        if (augcamera != null) {
            try {
                augcamera.close();
                augcamera = null;
            } catch (AugCameraException e) {
                throw new CodeableException(e);
            }
        }
    }
    @Override
    public void setCode(Code code) throws CodeableException {
        release();
        cname = code.getCodeableName();
        name = code.getString(CAMERA_UINAME_KEY);
        int id = code.getInt(CAMERA_ID_KEY);
        augcamera = AbstractPhoneCamera.getInstance(id);
        if (augcamera == null) {
            throw new CodeableException("camera not found");
        }
        Code icode = code.get("impl");
        if (icode != null) 
            augcamera.setCode(icode);
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
    public String getName() {
        
        return name;
    }

    @Override
    public int getId() {
        
        return augcamera.getId();
    }

    @Override
    public void applyParameters() throws AugCameraException {
        augcamera.applyParameters();
    }

    /**
     * this name is unique to type of camera, ie FRONT / BACK / CANON_MKIII_TETHER
     */
    @Override
    public CodeableName getCameraName() {
        return cname;
    }

    /**
     * this name is unique to type of camera type of augiement, all cameras return the same
     * so that apps looking or cameras can find 'em
     */
    @Override
    public CodeableName getCodeableName() {
        return augcamera.getCodeableName();
    }

    @Override
    public void initParams() {
        augcamera.initParams();
    }

    @Override
    public String flatten() {
        return augcamera.flatten();
    }

    @Override
    public void focus(AugFocusCallback cb) throws AugCameraException {
        augcamera.focus(cb);
    }

    @Override
    public void setPreviewCallback(AugPreviewCallback cb) {
        augcamera.setPreviewCallback(cb);
    }

    @Override
    public boolean isOpen() {
        return augcamera.isOpen();
    }

    @Override
    public void setPreviewCallbackWithBuffer(AugPreviewCallback cb) {
        augcamera.setPreviewCallbackWithBuffer(cb);
    }

    @Override
    public void addCallbackBuffer(byte[] b) {
        augcamera.addCallbackBuffer(b);
    }

    @Override
    public DialogFragment getUI() {
        
        return augcamera.getUI();
    }

    @Override
    public Meta getMeta() {

        //return augcamera.getMeta();
        return new Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
                return null;
            }

            @Override
            public CodeableName getCodeableName() {
                return AugCamera.AUGIENAME;
            }

            @Override
            public String getUIName() {
                
                return "Phone Camera";
            }

            @Override
            public String getDescription() {
                return "Phone Camera";
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
    }
}
