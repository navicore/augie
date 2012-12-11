package com.onextent.augie;

import java.util.Set;

import com.onextent.util.codeable.CodeableName;

public interface AugiementFactory {
    
    static final String TAG = Augiement.TAG;
    
    void registerAugiement(Class<? extends Augiement> augclass, CodeableName augieName) throws AugiementException;
    
    Augiement newInstance(CodeableName augieName);
    
    public Set<CodeableName> getAugieNames();
    
}
