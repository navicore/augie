package com.onextent.augie.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.onextent.augie.AugieView;
import com.onextent.augie.Augieable.Meta;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementDependencyRegistry;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;

public class AugiementFactoryImpl implements AugiementFactory {

	private final Map<String, Class<? extends Augiement>> augieClasses;
	private final Map<String, Augiement> augieInstances;
	
    private final AugiementDependencyRegistry registry;
    
    public AugiementFactoryImpl(AugieView av) {
	    
        augieClasses = new HashMap<String, Class<? extends Augiement>>();
        augieInstances = new HashMap<String, Augiement>();
        registry = new AugiementRegistryImpl(av);
    }

    @Override
    public Meta getMeta(String name) {

        Augiement a = augieInstances.get(name);
        if (a == null) return null;
        
        return a.getMeta();
    }

    @Override
    public void registerAugiement(Class<? extends Augiement> augclass, String name)
            throws AugiementException {
        
        augieClasses.put(name, augclass);
        try {
            augieInstances.put(name, augclass.newInstance());
        } catch (InstantiationException e) {
            throw new AugiementException(e);
        } catch (IllegalAccessException e) {
            throw new AugiementException(e);
        }
    }

    @Override
    public void activate(String name) throws AugiementException {
        
        Augiement a = augieInstances.get(name);
        if (a == null) throw new AugiementException("augiement not found");
        registry.add(a);
    }

    @Override
    public void deactivate(String name) throws AugiementException {

        Augiement a = augieInstances.get(name);
        if (a == null) throw new AugiementException("augiement not found");
        registry.remove(a);
    }

    @Override
    public boolean isActive(String name) {
        
        Augiement a = augieInstances.get(name);
        return (a != null);
    }

    @Override
    public Set<String> getAugieNames() {

        return augieClasses.keySet();
    }
}
