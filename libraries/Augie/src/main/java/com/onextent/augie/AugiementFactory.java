/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import java.util.Map;
import java.util.Set;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.Augiement.Meta;

public interface AugiementFactory {
    
    void registerAugiement(Meta meta) throws AugiementException;
    
    Augiement newInstance(CodeableName augieName);
    
    public Set<CodeableName> getAugieNames();
    
    public Map<CodeableName, Meta> getAllMeta();
    
}
