package com.onextent.augie;

import java.util.Set;

import org.json.JSONObject;

import com.onextent.augie.camera.AugCamera;

public interface Mode {

    JSONObject getCode();

    void setCode(JSONObject code);

    String getName();

    void setName(String name);

    AugieName getAugieName();

    void setAugieName(AugieName augieName);

    AugCamera getCamera();

    void setCamera(AugCamera camera);

    void removeAugiement(Augiement a);

    void addAugiement(Augiement a);

    Set<Augiement> getAugiements();
    
    void activate() throws AugieException;
    void deactivate() throws AugieException;
}
