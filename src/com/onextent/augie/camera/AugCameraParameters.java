package com.onextent.augie.camera;

import java.util.List;

import android.hardware.Camera;

import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.Size;

public interface AugCameraParameters extends Codeable {
    
    void rollback() throws CodeableException;
    
    int getMaxNumFocusAreas();

    int getMaxNumMeteringAreas();
    
    String getFlashMode();
    void setFlashMode(String m);
    List<String> getSupportedFlashModes();

    String getColorMode();
    void setColorMode(String m);
    List<String> getSupportedColorModes();

    String getWhiteBalance();
    void setWhiteBalance(String m);
    List<String> getSupportedWhiteBalances();

    String getSceneMode();
    void setSceneMode(String m);
    List<String> getSupportedSceneModes();

    String getFocusMode();
    void setFocusMode(String m);
    List<String> getSupportedFocusModes();

    String getAntibanding();
    void setAntibanding(String m);
    List<String> getSupportedAntibanding();
    
    NamedInt getPictureFmt();
    void setPictureFmt(NamedInt f);
    List<NamedInt> getSupportedPictureFmts();
    
    String getXPictureFmt();
    void setXPictureFmt(String f);
    List<String> getXSupportedPictureFmts();
    
    NamedInt getPreviewFmt();
    void setPreviewFmt(NamedInt f);
    List<NamedInt> getSupportedPreviewFmts();
    
    boolean getShutterSound();
    void setShutterSound(boolean enable);

    Size getPictureSize();
    void setPictureSize(Size sz);
    List<Size> getSupportedPictureSizes();

    Size getPreviewSize();
    void setPreviewSize(Size sz);
    List<Size> getSupportedPreviewSizes();
    
    int getJpegQuality();
    void setJpegQuality(int q);

    int getJpegThumbnailQuality();

    void setJpegThumbnailQuality(int q);
    
    List<Camera.Area> getFocusAreas();
    void setFocusAreas(List<Camera.Area> areas);
    
    List<Camera.Area> getMeteringAreas();
    void setMeteringAreas(List<Camera.Area> areas);
    
    int getMinExposureCompensation();
    int getMaxExposureCompensation();
    int getExposureCompensation();
    float getExposureCompensationStep();
    void setExposureCompensation(int ec);
}
