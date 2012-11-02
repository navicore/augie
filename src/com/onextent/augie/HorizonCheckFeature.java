/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import com.onextent.augie.AugDrawBase.HLine;
import com.onextent.augie.AugDrawBase.Line;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HorizonCheckFeature implements AugmentedViewFeature, SensorEventListener {

    protected static final String TAG = AugmentedView.TAG;
    private final SensorManager mSensorManager;
    private final AugmentedView augview;
    private final HorizonFeature horizonFeture;
    private float[] mGravs = new float[3];
    private float[] mGeoMags = new float[3];
    private float[] mOrientation = new float[3];
    private float[] mRotationM = new float[9];               // Use [16] to co-operate with android.opengl.Matrix
    private float[] mRemapedRotationM = new float[9];
    private double mAngle;
    private long lastUpdateTime;

    public HorizonCheckFeature(AugmentedView v, HorizonFeature h, Activity activity) {

        lastUpdateTime = 0;
        mAngle = 0;
        augview = v;
        horizonFeture = h;
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, 
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, 
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public boolean onTouch(View v, MotionEvent event) {
        //noop
        return false;
    }

    private void correctVertical(Line line) {

        int x1 = (int) (line.center.x - line.center.y * Math.tan(mAngle)); // + 90?
        int y1 = 0;
        int x2 = (int) (line.center.x + (augview.getHeight() - line.center.y) * Math.tan(mAngle));
        int y2 = augview.getHeight();

        augview.getCanvas().drawLine(x1, y1, x2, y2, augview.getPaint());
    }
    private void correctHorizontal(Line line) {

        int x1 = 0;
        int y1 = (int) (line.center.y + line.center.x * Math.tan(mAngle));
        int x2 = augview.getWidth();
        int y2 = (int) (line.center.y - (augview.getWidth() - line.center.x) * Math.tan(mAngle));

        augview.getCanvas().drawLine(x1, y1, x2, y2, augview.getPaint());
    }
    public void updateBmp() {
        //todo: paint red

        /*
             X1 = 0 
             Y1 = Yc + Xc * tan(a) 
             X2 = screenWidth 
             Y2 = Yc - (screenWidth - Xc) * tan(a)
         */
        for (Line line : horizonFeture.getLines()) {
            if (line instanceof HLine) correctHorizontal(line);
            else correctVertical(line);
        }
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

    public void onSensorChanged(SensorEvent event) {

        if ((lastUpdateTime != 0) && 
                ((System.currentTimeMillis() - lastUpdateTime) < 200)) return;

        lastUpdateTime = System.currentTimeMillis();

        if (horizonFeture.getLines().isEmpty()) return;

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

        if ( SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags) ) {
            SensorManager.remapCoordinateSystem(mRotationM, 
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, 
                    mRemapedRotationM);
            SensorManager.getOrientation(mRemapedRotationM, mOrientation);
            mAngle = mOrientation[2];
            augview.reset();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
