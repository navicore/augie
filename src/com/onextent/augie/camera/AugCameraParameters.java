package com.onextent.augie.camera;

import com.onextent.util.codeable.Codeable;
import com.onextent.util.codeable.CodeableException;

public interface AugCameraParameters extends Codeable {
    
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)
    //todo: make confluence of the camera params api, sqlite, ui form = (an adapter?)

    void rollback() throws CodeableException;
    
    int getMaxNumFocusAreas();

    int getMaxNumMeteringAreas();
    
    String getFlashMode();
    void setFlashMode(String m);

    String getColorMode();
    void setColorMode(String m);

    String getWhiteBalMode();
    void setWhiteBalMode(String m);

    String getSceneMode();
    void setSceneMode(String m);
}
