/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.system;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugSysLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementDependencyRegistry;
import com.onextent.augie.AugiementException;

	//todo:
	//  UNREG
	//  removes module and any modules that depend on it and puts 'em in inactive reg

public class AugiementRegistryImpl extends AbstractSet<Augiement> implements AugiementDependencyRegistry {
    
    private final Map<CodeableName, Augiement> active;
    private final Map<CodeableName, Augiement> waiting;
    private final AugieScape augieScape;
    
    public AugiementRegistryImpl(AugieScape av) {
        active = new LinkedHashMap<CodeableName, Augiement>();
        waiting = new LinkedHashMap<CodeableName, Augiement>();
        augieScape = av;
    }

    private void create(Augiement a) {
        try {
            a.onCreate(augieScape, this);
        } catch (AugiementException e) {
            AugSysLog.e( "augiement onCreate error", e);
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
        Set<CodeableName> dependencyNames = object.getMeta().getDependencyNames();
        
        if (dependencyNames == null) {
            
            active.put(object.getCodeableName(), object);
            create(object);
            AugSysLog.d( "augiement " + object.getCodeableName() + " registered w/no deps");
            tryWaiting();
            
        } else {

            for (CodeableName dname : dependencyNames) {
                if (!active.containsKey(dname)) {
                    AugSysLog.d( "augiement " + object.getCodeableName() + " waiting for " + dname);
                    waiting.put(object.getCodeableName(), object);
                    dependsAllmet = false;
                    break;
                }
            }

            if (dependsAllmet) {
                active.put(object.getCodeableName(), object);
                create(object);
                AugSysLog.d( "augiement " + object.getCodeableName() + " registered w/deps");
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
