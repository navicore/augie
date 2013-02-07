/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.cvface;

import java.util.HashSet;
import java.util.Set;

import android.os.Build;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.ments.OpenCV;
import com.onextent.augie.ments.cvface.CvFaceFinder.DETECTION_TYPE;

public abstract class AbstractCvFaceFinder implements Augiement {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/CVFACEFINDER");
    public static final String UI_NAME = "(CV) Face Finder";
    public static final String DESCRIPTION = "Face recognition focusing based on Open Computer Vision.";

	final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIE_NAME);
        deps.add(OpenCV.AUGIE_NAME);
    }
    
    public static AbstractCvFaceFinder getInstance( ) {
        
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            return new IcsCvFaceFinder();
        } else  {
            return new SimpleCvFaceFinder();
        }
    }
    
    abstract public DETECTION_TYPE getDtype();

    abstract public void setDtype(DETECTION_TYPE dtype);

    abstract public int getFaceSizePct();
    abstract public void setFaceSizePct(int sz);
}
