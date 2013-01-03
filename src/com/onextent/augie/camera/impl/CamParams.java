package com.onextent.augie.camera.impl;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Area;

import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.ImageFmt;
import com.onextent.augie.camera.NamedInt;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.JSONCoder;
import com.onextent.util.codeable.Size;

class CamParams implements AugCameraParameters {
    
    private final SimplePhoneCamera augcamera;

    /**
     * @param simplePhoneCamera
     */
    CamParams(SimplePhoneCamera simplePhoneCamera) {
        augcamera = simplePhoneCamera;
    }
    
    private String flashMode, colorMode, whiteBalMode, sceneMode, focusMode, antibanding;
    private String xpictureFmt;
    private NamedInt pictureFmt, previewFmt;
    private Size pictureSize, previewSize;
    private Code initCode;
    private boolean shutterSnd = true;
    private int jpegQuality = 0;
    private int jpegThumbnailQuality = 0;
    private List<Camera.Area> focusAreas; //transient, not codeable
    private List<Camera.Area> meterAreas; //transient, not codeable

    //
    // Codeable ifc
    //
    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        //todo: update each setting
        if (getFlashMode() != null) code.put("flashMode", getFlashMode());
        if (getColorMode() != null) code.put("colorMode", getColorMode());
        if (getWhiteBalance() != null) code.put("whiteBal", getWhiteBalance());
        if (getSceneMode() != null) code.put("sceneMode", getSceneMode());
        if (getFocusMode() != null) code.put("focusMode", getFocusMode());
        if (getAntibanding() != null) code.put("antibanding", getAntibanding());
        if (getPictureFmt() != null && getXPictureFmt() == null) code.put("pictureFmt", getPictureFmt().toInt());
        if (getPreviewFmt() != null) code.put("previewFmt", getPreviewFmt().toInt());
        if (getXPictureFmt() != null) code.put("xpictureFmt", getXPictureFmt());
        code.put("shutterSnd", shutterSnd);
        if (getPictureSize() != null) code.put("pictureSize", getPictureSize().getCode());
        if (getPreviewSize() != null) code.put("previewSize", getPreviewSize().getCode());
        if (getJpegQuality() != 0) code.put("jpegQuality", getJpegQuality());
        if (getJpegThumbnailQuality() != 0) code.put("jpegThumbnailQuality", getJpegThumbnailQuality());
        return code;
    }
    public void setCode(Code code) throws CodeableException {
        if (code != null) {
            //todo: update each setting
            if (code.has("flashMode")) setFlashMode(code.getString("flashMode"));
            if (code.has("colorMode")) setColorMode(code.getString("colorMode"));
            if (code.has("whiteBal")) setWhiteBalance(code.getString("whiteBal"));
            if (code.has("sceneMode")) setSceneMode(code.getString("sceneMode"));
            if (code.has("focusMode")) setFocusMode(code.getString("focusMode"));
            if (code.has("antibanding")) setAntibanding(code.getString("antibanding"));
            if (code.has("pictureFmt")) setPictureFmt(new ImageFmt(code.getInt("pictureFmt")));
            if (code.has("previewFmt")) setPreviewFmt(new ImageFmt(code.getInt("previewFmt")));
            if (code.has("shutterSnd")) setShutterSound(code.getBoolean("shutterSnd"));
            if (code.has("xpictureFmt")) setXPictureFmt(code.getString("xpictureFmt"));
            if (code.has("pictureSize")) {
                Size sz = new Size();
                sz.setCode(code.get("pictureSize"));
                setPictureSize(sz);
            }
            if (code.has("previewSize")) {
                Size sz = new Size();
                sz.setCode(code.get("previewSize"));
                setPreviewSize(sz);
            }
            if (code.has("jpegQuality")) setJpegQuality(code.getInt("jpegQuality"));
            if (code.has("jpegThumbnailQuality")) setJpegThumbnailQuality(code.getInt("jpegThumbnailQuality"));
        }
        initCode = code; //save for rollback
    }
    @Override
    public CodeableName getCodeableName() {
        return SimplePhoneCamera.PARAMS_CODEABLE_NAME;
    }

    //
    // ICS ifc
    //
    @Override
    public int getMaxNumFocusAreas() {
        return 0;
    }
    @Override
    public int getMaxNumMeteringAreas() {
        return 0;
    }
    @Override
    public void rollback() throws CodeableException {
        if (initCode != null) {
            setCode(initCode);
        }
    }
    
    //
    // ifc
    //
    @Override
    public String getFlashMode() {
        return flashMode;
    }
    @Override
    public void setFlashMode(String m) {
        this.flashMode = m;
    }
    @Override
    public List<String> getSupportedFlashModes() {
        return augcamera.camera.getParameters().getSupportedFlashModes();
    }
    
    @Override
    public String getColorMode() {
        return colorMode;
    }
    @Override
    public void setColorMode(String m) {
        this.colorMode = m;
    }
    @Override
    public List<String> getSupportedColorModes() {
        return augcamera.camera.getParameters().getSupportedColorEffects();
    }
    
    @Override
    public String getWhiteBalance() {
        return whiteBalMode;
    }
    @Override
    public void setWhiteBalance(String m) {
        this.whiteBalMode = m;
    }
    @Override
    public List<String> getSupportedWhiteBalances() {
        return augcamera.camera.getParameters().getSupportedWhiteBalance();
    }
    
    @Override
    public String getSceneMode() {
        return sceneMode;
    }
    @Override
    public void setSceneMode(String m) {
       sceneMode = m; 
    }
    @Override
    public List<String> getSupportedSceneModes() {
        return augcamera.camera.getParameters().getSupportedSceneModes();
    }
    
    @Override
    public String getFocusMode() {
        return focusMode;
    }
    @Override
    public void setFocusMode(String m) {
       focusMode = m; 
    }
    @Override
    public List<String> getSupportedFocusModes() {
        return augcamera.camera.getParameters().getSupportedFocusModes();
    }
    
    @Override
    public String getAntibanding() {
        return antibanding;
    }
    @Override
    public void setAntibanding(String m) {
        antibanding = m; 
    }
    @Override
    public List<String> getSupportedAntibanding() {
        return augcamera.camera.getParameters().getSupportedAntibanding();
    }
    
    @Override
    public NamedInt getPictureFmt() {
        return pictureFmt;
    }
    @Override
    public void setPictureFmt(NamedInt f) {
        pictureFmt = f;
    }
    @Override
    public List<NamedInt> getSupportedPictureFmts() {
        List<Integer> cfmts = augcamera.camera.getParameters().getSupportedPictureFormats();
        List<NamedInt> list = new ArrayList<NamedInt>();
        for (int i : cfmts) {
            NamedInt f = new ImageFmt(i);
            list.add(f);
        }
        return list;
    }
    
    @Override
    public NamedInt getPreviewFmt() {
        return previewFmt;
    }
    @Override
    public void setPreviewFmt(NamedInt f) {
        previewFmt = f;            
    }
    @Override
    public List<NamedInt> getSupportedPreviewFmts() {
        List<Integer> cfmts = augcamera.camera.getParameters().getSupportedPreviewFormats();
        List<NamedInt> list = new ArrayList<NamedInt>();
        for (int i : cfmts) {
            NamedInt f = new ImageFmt(i);
            list.add(f);
        }
        return list;
    }
    @Override
    public void setShutterSound(boolean enable) {
        shutterSnd = enable;
    }
    @Override
    public boolean getShutterSound() {
        return shutterSnd;
    }
    
    //
    //begin X methods... 
    // warning: these keys are not android api safe and may change in the future
    //
    @Override
    public String getXPictureFmt() {
        return xpictureFmt;
    }
    @Override
    public void setXPictureFmt(String f) {
        xpictureFmt = f;
    }
    @Override
    public List<String> getXSupportedPictureFmts() {
        String sl = augcamera.camera.getParameters().get("picture-format-values");
        return SimplePhoneCamera.split(sl, ',');
    }

    @Override
    public Size getPictureSize() {
       return pictureSize; 
    }
    @Override
    public void setPictureSize(Size sz) {
        pictureSize = sz;
    }
    @Override
    public List<Size> getSupportedPictureSizes() {
        List<Camera.Size> csz = augcamera.camera.getParameters().getSupportedPictureSizes();
        List<Size> l = new ArrayList<Size>();
        for (Camera.Size s : csz) {
            Size sz = new Size(s);
            l.add(sz);
        }
        return l;
    }
    
    @Override
    public Size getPreviewSize() {
       return previewSize; 
    }
    @Override
    public void setPreviewSize(Size sz) {
        previewSize = sz;
    }
    @Override
    public List<Size> getSupportedPreviewSizes() {
        List<Camera.Size> csz = augcamera.camera.getParameters().getSupportedPreviewSizes();
        List<Size> l = new ArrayList<Size>();
        for (Camera.Size s : csz) {
            Size sz = new Size(s);
            l.add(sz);
        }
        return l;
    }
    
    @Override
    public int getJpegQuality() {
        return jpegQuality;
    }
    @Override
    public void setJpegQuality(int q) {
       jpegQuality = q;
    }
    
    @Override
    public int getJpegThumbnailQuality() {
        return jpegThumbnailQuality;
    }
    @Override
    public void setJpegThumbnailQuality(int q) {
       jpegThumbnailQuality = q;
    }
    @Override
    public List<Area> getFocusAreas() {
        return focusAreas;
    }
    @Override
    public void setFocusAreas(List<Area> areas) {
       focusAreas = areas;
    }
    @Override
    public List<Area> getMeteringAreas() {
        return meterAreas;
    }
    @Override
    public void setMeteringAreas(List<Area> areas) {
        meterAreas = areas;
    }
}