package com.onextent.augie;

/**
 * subclasses must implemented updateBmp
 * 
 */
import java.util.HashMap;
import java.util.Map;

import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.MarkerFactory;
import com.onextent.augie.marker.impl.AugLineImpl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

        /** X1 = 0 
            Y1 = Yc + Xc * tan(a) 
            X2 = screenWidth 
            Y2 = Yc - (screenWidth - Xc) * tan(a)
         */
public abstract class LevelerBase implements AugmentedViewFeature, SensorEventListener {

    protected static final String TAG = AugmentedViewFeature.TAG;
    protected static final Map<Double, Double> tanCache;
    protected static final Map<String, AugLine> vlineCache;
    protected static final Map<String, AugLine> hlineCache;
    static {
        tanCache = new HashMap<Double, Double>();
        hlineCache = new HashMap<String, AugLine>();
        vlineCache = new HashMap<String, AugLine>();
    }
    protected final SharedPreferences prefs;
    protected final SensorManager mSensorManager;
    protected final AugmentedView augview;
    protected final HorizonFeature horizonFeture;
    private float[] mGravs = new float[3];
    private float[] mGeoMags = new float[3];
    private float[] mOrientation = new float[3];
    private float[] mRotationM = new float[9];
    private float[] mRemapedRotationM = new float[9];
    protected double mAngle;
    protected long lastUpdateTime;

    public LevelerBase(AugmentedView v, HorizonFeature h, Context activity, SharedPreferences p) {
        super();
        prefs           = p;
        lastUpdateTime  = 0;
        mAngle          = 0;
        augview         = v;
        horizonFeture   = h;
        mSensorManager  = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        registerSensorListeners();
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

    public boolean onTouch(View v, MotionEvent event) {
        //noop
        return false;
    }

    public void clear() {
        //noop
    }

    public void stop() {
        Log.d(TAG, "stopping " + HorizonCheckFeature.class.getName());
        unregisterSensorListeners();
        if (vlineCache.size() > 1000)  vlineCache.clear();
        if (hlineCache.size() > 1000)  hlineCache.clear();
        if (tanCache.size() > 1000)  tanCache.clear();
    }

    public void resume() {
        Log.d(TAG, "resuming " + HorizonCheckFeature.class.getName());
        registerSensorListeners();
    }

    public void onSensorChanged(SensorEvent event) {
    
        if (horizonFeture.getLines() == null) return;
    
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