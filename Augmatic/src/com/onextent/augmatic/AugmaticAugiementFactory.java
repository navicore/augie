package com.onextent.augmatic;

import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.camera.HistogramFeature;
import com.onextent.augie.camera.PinchZoom;
import com.onextent.augie.camera.shutter.Shutter;
import com.onextent.augie.camera.shutter.TouchShutter;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.augie.impl.AugiementFactoryImpl;
import com.onextent.augie.impl.GPS;
import com.onextent.augie.impl.HorizonCheckFeature;
import com.onextent.augie.impl.HorizonFeature;
import com.onextent.augie.impl.ShakeResetFeature;

public class AugmaticAugiementFactory extends AugiementFactoryImpl implements
        AugiementFactory {

    public AugmaticAugiementFactory() throws AugiementException {
        super();
        
        //register built-in augiements

        registerAugiement(AugDrawFeature.META);


        registerAugiement(HorizonFeature.META);
        registerAugiement(HorizonCheckFeature.META);
        registerAugiement(TouchShutter.META);
        registerAugiement(ShakeResetFeature.META);
        registerAugiement(PinchZoom.META);
        registerAugiement(HistogramFeature.META);
        registerAugiement(Shutter.META);
        registerAugiement(GPS.META);
    }
}
