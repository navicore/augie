package com.onextent.augie;

import com.onextent.util.codeable.CodeableName;

public class AugiementName extends CodeableName {

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
