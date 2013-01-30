package com.onextent.augmatic;

import android.os.Build;

import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.ments.myface.FaceFinder;
//import com.onextent.augie.camera.shutter.RoboShutter;
import com.onextent.augie.ments.shutter.Shutter;
import com.onextent.augie.ments.shutter.TouchShutter;
import com.onextent.augie.ments.Draw;
import com.onextent.augie.ments.Compass;
import com.onextent.augie.ments.GPS;
import com.onextent.augie.ments.Histogram;
import com.onextent.augie.ments.HorizonCheck;
import com.onextent.augie.ments.Horizon;
import com.onextent.augie.ments.PinchZoom;
import com.onextent.augie.ments.ShakeReset;
import com.onextent.augie.system.AugiementFactoryImpl;

public class AugmaticAugiementFactory extends AugiementFactoryImpl implements
        AugiementFactory {

    public AugmaticAugiementFactory() throws AugiementException {
        super();
        
        //register built-in augiements

        registerAugiement(Draw.META);


        registerAugiement(Horizon.META);
        registerAugiement(HorizonCheck.META);
        registerAugiement(TouchShutter.META);
        registerAugiement(ShakeReset.META);
        registerAugiement(PinchZoom.META);
        registerAugiement(Histogram.META);
        registerAugiement(Shutter.META);
        registerAugiement(GPS.META);
        registerAugiement(Compass.META);
        //registerAugiement(RoboShutter.META);  //buggy, loses camera on htc 2.3
        
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= FaceFinder.META.getMinSdkVer() ) {
            registerAugiement(FaceFinder.META);
        }
    }
}
