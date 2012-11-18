/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.lang.Math;
import android.util.Log;

public class ShakeResetFeature implements Augiement, SensorEventListener {

    protected static final String TAG = AugieView.TAG;
    private SensorManager mSensorManager;
    private float mAccel; 			// acceleration apart from gravity
    private float mAccelCurrent; 	// current acceleration including gravity
    private float mAccelLast; 		// last acceleration including gravity
    private AugieView augview;
    List<Augiement> oneShakeRegistry;
    List<Augiement> twoShakeRegistry;
    Calendar last, now;
    boolean doing_double = false;

    @Override
    public Set<String> getDependencyNames() {
        return null;
    }
     
    @Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
        augview = av;
        
        mSensorManager = (SensorManager) av.getContext().getSystemService(Context.SENSOR_SERVICE);        registerSensorListeners();
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        oneShakeRegistry = new ArrayList<Augiement>();
        twoShakeRegistry = new ArrayList<Augiement>();
        last = Calendar.getInstance(); 
        last.setTime(new Date()); 
        now = Calendar.getInstance(); 
    }
     
    //todo: think about callback api for this sort of feature.  the shaker shouldn't care if
    // it is for a reset or something else
    
    public static final String AUGIE_NAME = "AUGIE/FEATURES/SHAKE_RESET";
    @Override
    public String getAugieName() {
        return AUGIE_NAME;
    }

    private void registerSensorListeners() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void unregisterSensorListeners() {
        mSensorManager.unregisterListener(this);
    }

    public void registerOneShakeReset(Augiement f) {
        oneShakeRegistry.add(f);
    }
    public void registerTwoShakeReset(Augiement f) {
        twoShakeRegistry.add(f);
    }

    public void updateCanvas() {
        //noop
    }

    public void clear() {
        //noop
    }

    public void stop() {
        Log.d(TAG, "stopping " + ShakeResetFeature.class.getName());
        unregisterSensorListeners();
    }

    public void resume() {
        Log.d(TAG, "resuming " + ShakeResetFeature.class.getName());
        registerSensorListeners();
    }

    private boolean testAccel(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        return mAccel > 4;
    }

    private void handleAccel(SensorEvent se, List<Augiement> reg) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        if (mAccel > 4) {
            for (Augiement f : reg) {
                f.clear();
            }
            augview.reset();
        }
    }

    public void onSensorChanged(SensorEvent se) {

        if (testAccel(se)) {

            Date new_date = new Date();
            now.setTime(new_date); 
            long diff = now.getTimeInMillis() - last.getTimeInMillis();
            //TODO settings need to be configurable.  false doubles too easy right now
            if(diff > 1500) { 
                doing_double = true;
                handleAccel(se, oneShakeRegistry);
                last.setTime(new_date); 

            } else if (doing_double && diff > 400) {
                doing_double = false;
                handleAccel(se, twoShakeRegistry);
                last.setTime(new_date); 
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}

