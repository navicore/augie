package com.onextent.augie.camera;

import com.onextent.augie.AugieName;

public class CameraName extends AugieName {

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
