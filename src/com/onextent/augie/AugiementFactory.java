package com.onextent.augie;

import java.util.Set;

public interface AugiementFactory {
    
    static final String TAG = Augiement.TAG;
    
    void registerAugiement(Class<? extends Augiement> augclass, AugieName augieName) throws AugiementException;
    
    Augiement newInstance(AugieName augieName);
    
    public Set<AugieName> getAugieNames();
    
}
