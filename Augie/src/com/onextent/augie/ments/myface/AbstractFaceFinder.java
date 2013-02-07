/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.myface;

import java.util.HashSet;
import java.util.Set;

import android.os.Build;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;

public abstract class AbstractFaceFinder implements Augiement {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/FACEFINDER");
    public static final String UI_NAME = "Face Finder";
    public static final String DESCRIPTION = "Face recognition focusing.";

	final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIE_NAME);
    }
    
    public static AbstractFaceFinder getInstance( ) {
        
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            return new IcsFaceFinder();
        } else  {
            throw new java.lang.UnsupportedOperationException("pre-ics not supported");
        }
    }
}
