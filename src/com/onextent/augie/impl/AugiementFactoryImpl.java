package com.onextent.augie.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.AugieName;

public class AugiementFactoryImpl implements AugiementFactory {

	private final Map<AugieName, Class<? extends Augiement>> augieClasses;
	
    public AugiementFactoryImpl(AugieScape av) {
	    
        augieClasses = new HashMap<AugieName, Class<? extends Augiement>>();
    }

    @Override
    public void registerAugiement(Class<? extends Augiement> augclass, AugieName name)
            throws AugiementException {
        
        augieClasses.put(name, augclass);
    }

    @Override
    public Set<AugieName> getAugieNames() {

        return augieClasses.keySet();
    }

    @Override
    public Augiement newInstance(AugieName augieName) {
        
        Class<? extends Augiement> c = augieClasses.get(augieName);
        
        if (c != null)
            try {
                return c.newInstance();
            } catch (InstantiationException e) {
                Log.e(TAG, e.toString(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.toString(), e);
            }
        return null;
    }
}
