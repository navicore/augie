package com.onextent.augie.impl;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.onextent.augie.AugieName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementDependencyRegistry;

import android.util.Log;

	//todo:
	//  UNREG
	//  removes module and any modules that depend on it and puts 'em in inactive reg

public class AugiementRegistryImpl extends AbstractSet<Augiement> implements AugiementDependencyRegistry {
    
    private final Map<AugieName, Augiement> active;
    private final Map<AugieName, Augiement> waiting;
    private final AugieScape augview;
    
    public AugiementRegistryImpl(AugieScape av) {
        active = new LinkedHashMap<AugieName, Augiement>();
        waiting = new LinkedHashMap<AugieName, Augiement>();
        augview = av;
    }

    private void create(Augiement a) {
        try {
            a.onCreate(augview, this);
        } catch (AugiementException e) {
            Log.e(TAG, "augiement onCreate error", e);
            //todo: make error visible in ui
        }
    }
    
    private void tryWaiting() {
        Set<Augiement> trymes = new HashSet<Augiement>();
        for (Augiement a : waiting.values()) {
            trymes.add(a);
        }
        for (Augiement a : trymes) {
            waiting.values().remove(a);
            add(a);
        }
    }

    @Override
    public boolean add(Augiement object) {

        assert(object != null);

        boolean dependsAllmet = true;
        Set<AugieName> dependencyNames = object.getDependencyNames();
        
        if (dependencyNames == null) {
            
            active.put(object.getAugieName(), object);
            create(object);
            Log.d(TAG, "augiement " + object.getAugieName() + " registered w/no deps");
            tryWaiting();
            
        } else {

            for (AugieName dname : dependencyNames) {
                if (!active.containsKey(dname)) {
                    Log.d(TAG, "augiement " + object.getAugieName() + " waiting for " + dname);
                    waiting.put(object.getAugieName(), object);
                    dependsAllmet = false;
                    break;
                }
            }

            if (dependsAllmet) {
                active.put(object.getAugieName(), object);
                create(object);
                Log.d(TAG, "augiement " + object.getAugieName() + " registered w/deps");
                tryWaiting();
            }
        }
        
        return dependsAllmet;
    }

    private class ARIterator implements Iterator<Augiement> {
       
        final Iterator<Augiement> actives;
        Augiement last;
        
        ARIterator() {
            
            actives = active.values().iterator();
            last = null;
        }

        @Override
        public boolean hasNext() {
            return actives.hasNext();
        }

        @Override
        public Augiement next() {
            last = actives.next();
            return last;
        }

        @Override
        public void remove() {
            Augiement a = active.remove(last);
            if (a != null) {
                //move dependencies to waiting
                
            }
        }
    }
    
    @Override
    public Iterator<Augiement> iterator() {
        return new ARIterator();
    }

    @Override
    public int size() {
        return active.size();
    }

    @Override
    public boolean contains(Object object) {
        return active.values().contains(object);
    }

    @Override
    public boolean isEmpty() {
        return active.isEmpty();
    }
    
    @Override
    public void clear() {
        super.clear();
        active.clear();
        waiting.clear();
    }
}
