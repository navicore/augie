package com.onextent.augie;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AugiementRegistry extends AbstractSet<Augiement> {
    
    private final Map<String, Augiement> active;
    private final Map<String, Augiement> waiting;
    
    public AugiementRegistry() {
        active = new LinkedHashMap<String, Augiement>();
        waiting = new LinkedHashMap<String, Augiement>();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public boolean add(Augiement object) {
        // TODO Auto-generated method stub
        //make sure deps are satisfied, if no, waiting
        //if yes, active, then loop through waiting again
        return super.add(object);
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

    ////////////////////////////////////////////////////////////////////////////////////
    
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
}
