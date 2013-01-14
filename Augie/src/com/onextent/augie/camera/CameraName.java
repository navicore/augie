/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera;

import com.onextent.android.codeable.CodeableName;

public class CameraName extends CodeableName {

    public CameraName(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object o) {
        
        if (o instanceof CameraName) {
            
            return ((CameraName) o).name.equals(name);
        }
        
        return false;
    }
}
