/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.system;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugSysLog;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;

public class AugiementFactoryImpl implements AugiementFactory {

	private final Map<CodeableName, Augiement.Meta> augieClasses;
	
    public AugiementFactoryImpl() {
	    
        //augieClasses = new HashMap<CodeableName, Augiement.Meta>();
        augieClasses = new TreeMap<CodeableName, Augiement.Meta>();
    }

    @Override
    public void registerAugiement(Augiement.Meta meta)
            throws AugiementException {
        
        augieClasses.put(meta.getCodeableName(), meta);
    }

    @Override
    public Set<CodeableName> getAugieNames() {

        return augieClasses.keySet();
    }

    @Override
    public Augiement newInstance(CodeableName augieName) {
        
        AugSysLog.d( "newInstance " + augieName);
        
        Augiement.Meta m = augieClasses.get(augieName);
        
        if (m == null) {
            throw new java.lang.NullPointerException("no class for " + augieName + " found");
            
        } else
        
            try {
                return m.getAugiementClass().newInstance();
            } catch (InstantiationException e) {
                AugSysLog.e( e.toString(), e);
            } catch (IllegalAccessException e) {
                AugSysLog.e( e.toString(), e);
            }
        throw new java.lang.NullPointerException("no instance for " + augieName);
    }

    @Override
    public Map<CodeableName, Augiement.Meta> getAllMeta() {

        return augieClasses;
    }
}
