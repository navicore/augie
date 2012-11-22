package com.onextent.augie;

import java.util.Set;

public interface AugiementFactory {
    
    static final String TAG = Augiement.TAG;
    
    Augieable.Meta getMeta(String augieName);

    void registerAugiement(Class<? extends Augiement> augclass, String name) throws AugiementException;
    
    void activate(String name) throws AugiementException;
    
    void deactivate(String name) throws AugiementException;
    
    boolean isActive(String name);

    public Set<String> getAugieNames();
    
}
