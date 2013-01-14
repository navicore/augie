package com.onextent.augie;

import java.util.Map;
import java.util.Set;

import com.onextent.augie.camera.AugCamera;
import com.onextent.util.codeable.CodeableName;

public interface AugiementFactory {
    
    static final String TAG = AugCamera.TAG;
    
    void registerAugiement(Meta meta) throws AugiementException;
    
    Augiement newInstance(CodeableName augieName);
    
    public Set<CodeableName> getAugieNames();
    
    public Map<CodeableName, Meta> getAllMeta();
    
    interface Meta {
        
        Class<? extends Augiement> getAugiementClass();
        CodeableName getCodeableName();
        String getUIName();
    }
}
