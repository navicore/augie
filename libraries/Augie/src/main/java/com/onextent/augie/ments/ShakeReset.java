/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.ments;

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
import android.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.Mode;

public class ShakeReset implements Augiement, SensorEventListener {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/SHAKE_RESET");
    public static final String UI_NAME = "Shake Reset";
    public static final String DESCRIPTION = "Shake the device to clear scribles.";
    
    private SensorManager mSensorManager;
    private float mAccel; 			// acceleration apart from gravity
    private float mAccelCurrent; 	// current acceleration including gravity
    private float mAccelLast; 		// last acceleration including gravity
    private AugieScape augieScape;
    List<Augiement> oneShakeRegistry;
    List<Augiement> twoShakeRegistry;
    Calendar last, now;
    boolean doing_double = false;
	private boolean _init = false;

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        augieScape = av;
        
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
     
    private void unregisterAllAugiements() {
        oneShakeRegistry.clear();
        twoShakeRegistry.clear();
    }
    private void registerAllAugiements() {
		
        AugieActivity activity = (AugieActivity) augieScape.getContext();
        Mode m = activity.getModeManager().getCurrentMode();

    	for (Augiement a : m.getAugiements().values()) {
    		registerOneShakeReset(a);
    	}
	}

	//todo: think about callback api for this sort of feature.  the shaker shouldn't care if
    // it is for a reset or something else
    
    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    private void registerSensorListeners() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void unregisterSensorListeners() {
        mSensorManager.unregisterListener(this);
    }

    public void registerOneShakeReset(Augiement f) {
    	AugLog.d( "shake reset feature register " + 
    			f.getMeta().getUIName() + 
    			" for single shake");
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
        AugLog.d( "stopping " + getClass().getName());
        unregisterSensorListeners();
        unregisterAllAugiements();
        _init = false;
    }

    public void resume() {
        AugLog.d( "resuming " + getClass().getName());
        registerAllAugiements();
        registerSensorListeners();
        _init = true;
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
            augieScape.reset();
        }
    }

    public void onSensorChanged(SensorEvent se) {


        //todo: a generic UI Widget that lets Augiments list and multi select Augiements
    	if (!_init) {
    		return;
    	}
    	
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

    @Override
    public Code getCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCode(Code code) {
        // TODO Auto-generated method stub
        
    }

    public static final Meta META =
        new Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return ShakeReset.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }

            @Override
            public String getDescription() {

                return DESCRIPTION;
            }
            @Override
            public int getMinSdkVer() {
                return 0;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                // TODO Auto-generated method stub
                return null;
            }
        };

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta getMeta() {

        return META;
    }
}
