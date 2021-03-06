/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import com.onextent.android.codeable.CodeableName;

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
