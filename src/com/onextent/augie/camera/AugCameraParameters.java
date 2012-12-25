package com.onextent.augie.camera;

import java.util.List;

import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableException;

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
    
    ImageFmt getPictureFmt();
    void setPictureFmt(ImageFmt f);
    List<ImageFmt> getSupportedPictureFmts();
    
    ImageFmt getPreviewFmt();
    void setPreviewFmt(ImageFmt f);
    List<ImageFmt> getSupportedPreviewFmts();
}
