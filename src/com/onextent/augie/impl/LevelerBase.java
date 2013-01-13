/**
 * copyright Ed Sweeney, 2012, 2013, all rights reserved
 */
package com.onextent.augie.impl;

/**
 * subclasses must implemented updateBmp
 * 
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.MarkerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

        /** X1 = 0 
            Y1 = Yc + Xc * tan(a) 
            X2 = screenWidth 
            Y2 = Yc - (screenWidth - Xc) * tan(a)
         */
public abstract class LevelerBase implements Augiement, SensorEventListener {

    protected static final Map<Double, Double> tanCache;
    protected static final Map<String, AugLine> vlineCache;
    protected static final Map<String, AugLine> hlineCache;
    static {
        tanCache = new HashMap<Double, Double>();
        hlineCache = new HashMap<String, AugLine>();
        vlineCache = new HashMap<String, AugLine>();
    }
    protected SharedPreferences prefs;
    protected SensorManager mSensorManager;
    protected AugieScape augview;
    protected HorizonFeature horizonFeature;
    private float[] mGravs = new float[3];
    private float[] mGeoMags = new float[3];
    private float[] mOrientation = new float[3];
    private float[] mRotationM = new float[9];
    private float[] mRemapedRotationM = new float[9];
    protected double mAngle;
    protected long lastUpdateTime;
    
    protected final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(HorizonFeature.AUGIE_NAME);
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {

        for (Augiement a : helpers) {
            if (a instanceof HorizonFeature) {
                horizonFeature = (HorizonFeature) a;
            }
        }
        if (horizonFeature == null) throw new AugiementException("horizonFeature is null");
        
        augview = av;
        prefs = PreferenceManager.getDefaultSharedPreferences(av.getContext());
        mSensorManager  = (SensorManager) av.getContext().getSystemService(Context.SENSOR_SERVICE);
        registerSensorListeners();
        
        lastUpdateTime  = 0;
        mAngle          = 0;
    }

    protected double getTan(double angle) {
    
        //todo: use array 
        double v;
        if (tanCache.containsKey(angle)) {
            v = tanCache.get(angle);
        } else {
            v = Math.tan(angle);
            tanCache.put(angle, v);
        }
        return v;
    }

    protected AugLine correctVertical(AugLine line) {

        AugLine cline;
        String key = line.getCenter().x + "." + mAngle;
        if (vlineCache.containsKey(key)) {
            cline = vlineCache.get(key);
        } else {
            int x1 = (int) (line.getCenter().x - line.getCenter().y * getTan(mAngle));
            int y1 = 0;
            int x2 = (int) (line.getCenter().x + (augview.getHeight() - line.getCenter().y) * getTan(mAngle));
            int y2 = augview.getHeight();
            cline = MarkerFactory.createLine( new Point(x1, y1), new Point(x2, y2) );
            vlineCache.put(key, cline);
        }

        return cline;
    }
    protected AugLine correctHorizontal(AugLine line) {

        AugLine cline;
        String key = line.getCenter().y + "." + mAngle;
        if (hlineCache.containsKey(key)) {
            cline = hlineCache.get(key);
        } else {
            int x1 = 0;
            int y1 = (int) (line.getCenter().y + line.getCenter().x * getTan(mAngle));
            int x2 = augview.getWidth();
            int y2 = (int) (line.getCenter().y - (augview.getWidth() - line.getCenter().x) * getTan(mAngle));
            cline = MarkerFactory.createLine(new Point(x1, y1), new Point(x2, y2));
            hlineCache.put(key, cline);
        }

        return cline;
    }
    private void unregisterSensorListeners() {
        mSensorManager.unregisterListener(this);
    }

    protected void registerSensorListeners() {
        mSensorManager.registerListener(this, 
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, 
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void clear() {
        //noop
    }

    @Override
    public void stop() {
        Log.d(TAG, "stopping " + getClass().getName());
        unregisterSensorListeners();
        if (vlineCache.size() > 1000)  vlineCache.clear();
        if (hlineCache.size() > 1000)  hlineCache.clear();
        if (tanCache.size() > 1000)  tanCache.clear();
    }

    @Override
    public void resume() {
        Log.d(TAG, "resuming " + getClass().getName());
        registerSensorListeners();
    }

    public void onSensorChanged(SensorEvent event) {
    
        if (horizonFeature.getLines() == null) return;
    
        switch (event.sensor.getType()) { 
        case Sensor.TYPE_ACCELEROMETER:
            System.arraycopy(event.values, 0, mGravs, 0, 3);
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
    
            System.arraycopy(event.values, 0, mGeoMags, 0, 3);
            break;
        default:
            return; 
        }
        if ( ( lastUpdateTime != 0 ) && 
                ((System.currentTimeMillis() - lastUpdateTime) < 300)) return;
        lastUpdateTime = System.currentTimeMillis();
    
        if ( SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags) ) {
            SensorManager.remapCoordinateSystem(mRotationM, 
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, 
                    mRemapedRotationM);
            SensorManager.getOrientation(mRemapedRotationM, mOrientation);
            //mAngle = mOrientation[2];
            mAngle = Math.round(mOrientation[2]*100.0) / 100.0;
            augview.reset();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
