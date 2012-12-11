package com.onextent.augie.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.util.codeable.CodeableName;

public class AugiementFactoryImpl implements AugiementFactory {

	private final Map<CodeableName, Class<? extends Augiement>> augieClasses;
	
    public AugiementFactoryImpl() {
	    
        augieClasses = new HashMap<CodeableName, Class<? extends Augiement>>();
    }

    @Override
    public void registerAugiement(Class<? extends Augiement> augclass, CodeableName name)
            throws AugiementException {
        
        augieClasses.put(name, augclass);
    }

    @Override
    public Set<CodeableName> getAugieNames() {

        return augieClasses.keySet();
    }

    @Override
    public Augiement newInstance(CodeableName augieName) {
        
        Log.d(TAG, "newInstance " + augieName);
        
        Class<? extends Augiement> c = augieClasses.get(augieName);
        
        if (c == null) {
            throw new java.lang.NullPointerException("no class for " + augieName + " found");
            
        } else
        
            try {
                return c.newInstance();
            } catch (InstantiationException e) {
                Log.e(TAG, e.toString(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.toString(), e);
            }
        throw new java.lang.NullPointerException("no instance for " + augieName);
    }
}
