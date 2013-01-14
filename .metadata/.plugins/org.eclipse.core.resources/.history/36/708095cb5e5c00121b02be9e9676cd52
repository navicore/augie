package com.onextent.augie;

import java.util.Map;
import java.util.Set;

import com.onextent.android.codeable.CodeableName;

public interface AugiementFactory {
    
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
