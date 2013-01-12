/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import com.onextent.android.codeable.CodeableName;

public class ModeName extends CodeableName {

    public ModeName(String name) {
        super(name);
    }
    
    @Override
    public int hashCode() {
        
        return name.hashCode() + ModeName.class.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (o instanceof ModeName) {
            
            return ((ModeName) o).name.equals(name);
        }
        
        return false;
    }
}
