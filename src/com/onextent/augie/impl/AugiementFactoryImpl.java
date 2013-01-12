/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;

public class AugiementFactoryImpl implements AugiementFactory {

	private final Map<CodeableName, AugiementFactory.Meta> augieClasses;
	
    public AugiementFactoryImpl() {
	    
        augieClasses = new HashMap<CodeableName, AugiementFactory.Meta>();
    }

    @Override
    public void registerAugiement(AugiementFactory.Meta meta)
            throws AugiementException {
        
        augieClasses.put(meta.getCodeableName(), meta);
    }

    @Override
    public Set<CodeableName> getAugieNames() {

        return augieClasses.keySet();
    }

    @Override
    public Augiement newInstance(CodeableName augieName) {
        
        Log.d(Codeable.TAG, "newInstance " + augieName);
        
        AugiementFactory.Meta m = augieClasses.get(augieName);
        
        if (m == null) {
            throw new java.lang.NullPointerException("no class for " + augieName + " found");
            
        } else
        
            try {
                return m.getAugiementClass().newInstance();
            } catch (InstantiationException e) {
                Log.e(Codeable.TAG, e.toString(), e);
            } catch (IllegalAccessException e) {
                Log.e(Codeable.TAG, e.toString(), e);
            }
        throw new java.lang.NullPointerException("no instance for " + augieName);
    }

    @Override
    public Map<CodeableName, Meta> getAllMeta() {

        return augieClasses;
    }
}
