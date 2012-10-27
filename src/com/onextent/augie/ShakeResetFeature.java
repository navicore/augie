/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.testcamera.TestCameraActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class ShakeResetFeature implements AugmentedViewFeature {

	static final String TAG = TestCameraActivity.TAG;
	private final SensorManager mSensorManager;
    private float mAccel; 			// acceleration apart from gravity
    private float mAccelCurrent; 	// current acceleration including gravity
    private float mAccelLast; 		// last acceleration including gravity
    final AugmentedView augview;
    final List<AugmentedViewFeature> oneShakeRegistry;
    final List<AugmentedViewFeature> twoShakeRegistry;
	
	public ShakeResetFeature(AugmentedView v, Activity activity) {
        
		augview = v;
	    mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        oneShakeRegistry = new ArrayList<AugmentedViewFeature>();
        twoShakeRegistry = new ArrayList<AugmentedViewFeature>();
	}
	
	public void registerOneShakeReset(AugmentedViewFeature f) {
		oneShakeRegistry.add(f);
	}
	public void registerTwoShakeReset(AugmentedViewFeature f) {
		twoShakeRegistry.add(f);
		throw new java.lang.UnsupportedOperationException("not yet :(");
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		//noop
		return false;
	}

	public void redraw() {
		//noop
	}

	public void clear() {
		//noop
	}

	public void stop() {
		//noop
	}

	public void resume() {
		//noop
	}

    private final SensorEventListener mSensorListener = new SensorEventListener() {

      public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = FloatMath.sqrt((x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        if (mAccel > 4) {
        	for (AugmentedViewFeature f : oneShakeRegistry) {
        		f.clear();
        	}
        	augview.reset();
        }
      }

      public void onAccuracyChanged(Sensor sensor, int accuracy) {
      }
    };
}
