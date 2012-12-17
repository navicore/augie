package com.onextent.util.codeable;

public abstract class CodeableName {
    
    protected final String name;
    
    protected CodeableName(String name) {
        if (name == null) throw new java.lang.NullPointerException("no name");
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CodeableName)) return false;
        CodeableName on = (CodeableName) o;
        return on.name.equals(name);
    }

    @Override
    public int hashCode() {
        
        return name.hashCode() + CodeableName.class.hashCode();
    }

    @Override
    public String toString() {
        
        return name;
    }
}
