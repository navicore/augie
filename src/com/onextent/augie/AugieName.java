package com.onextent.augie;

public abstract class AugieName {
    
    protected final String name;
    
    protected AugieName(String name) {
        this.name = name;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public int hashCode() {
        
        return name.hashCode() + super.hashCode();
    }

    @Override
    public String toString() {
        
        return name;
    }
}
