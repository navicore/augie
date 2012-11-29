package com.onextent.augie;

public class AugiementName extends AugieName {

    public AugiementName(String name) {
        super(name);
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (o instanceof AugiementName) {
            
            return ((AugiementName) o).name.equals(name);
        }
        
        return false;
    }
}
