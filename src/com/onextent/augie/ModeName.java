package com.onextent.augie;

public class ModeName extends AugieName {

    public ModeName(String name) {
        super(name);
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (o instanceof ModeName) {
            
            return ((ModeName) o).name.equals(name);
        }
        
        return false;
    }
}
