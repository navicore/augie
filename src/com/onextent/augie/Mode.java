package com.onextent.augie;

import java.util.Set;

import com.onextent.augie.camera.AugCamera;
import com.onextent.util.codeable.Codable;

public interface Mode extends Codable {

    String getName();

    void setName(String name);

    AugCamera getCamera();

    void setCamera(AugCamera camera);

    void removeAugiement(Augiement a);

    void addAugiement(Augiement a);

    Set<Augiement> getAugiements();
    
    void activate() throws AugieException; //create and config SuperScape
    void deactivate() throws AugieException;

    void setAugieName(ModeName modeName);
}
