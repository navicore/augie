package com.onextent.augie.impl;

import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;

public class Compass implements Augiement, SensorEventListener {

	private static final String DESCRIPTION = "Compas Augielay";
	public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/COMPASS");
	private static final String UI_NAME = "Compass";

	private final Paint paint;
	private AugieScape augieScape;
	private SensorManager sensorService;
	private Sensor accelerometer;
	private Sensor magnetometer;

	private boolean enabled;

	private final static Set<CodeableName> deps;
	static {
		deps = null;
	}

	public Compass() {
		paint = new Paint();
		paint.setColor(0xff00ff00);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setTextSize(30);
	}

	@Override
	public CodeableName getCodeableName() {

		return AUGIE_NAME;
	}

	@Override
	public Code getCode() throws CodeableException {
		return null;
	}

	@Override
	public void setCode(Code code) throws CodeableException {
	}

	@Override
	public void updateCanvas() { 

		if (!enabled) return;
		
		float azimut = mAngle0_filtered_azimuth; //ejs test
		int width = augieScape.getWidth();
		int xPoint = width / 2;
		int height = augieScape.getHeight();
		int yPoint = height / 2;
		float radius = (float) (Math.max(xPoint, yPoint) * 0.6);

		Canvas canvas = augieScape.getCanvas();
		if (canvas == null) return;
		
		canvas.drawCircle(xPoint, yPoint, radius, paint);
		canvas.drawRect(0, 0, width, height, paint);

		float f1 = (float) (xPoint + radius * Math.sin((double) (-azimut) / 180 * 3.143));
		float f2 = (float) (yPoint - radius * Math.cos((double) (-azimut) / 180 * 3.143));
		canvas.drawLine(xPoint, yPoint, f1, f2, paint);

		canvas.drawText(printDirection(azimut), xPoint + 20, yPoint + 20, paint);
		//drawCompass(canvas, azimut);
	}

	/*
	private void drawCompass(Canvas canvas, float bearing) {

		Bitmap arrowBitmap = BitmapFactory.decodeResource( 
				augieScape.getContext().getResources(), R.drawable.compassrose);
		Matrix matrix = new Matrix();
		matrix.postRotate(bearing);
		Bitmap rotatedBmp = Bitmap.createBitmap(
				arrowBitmap, 
				0, 0, 
				arrowBitmap.getWidth(), 
				arrowBitmap.getHeight(), 
				matrix, 
				true
				);

		canvas.drawBitmap(rotatedBmp, 20, 20, null );
	}
	 */

	@Override
	public void clear() { }

	@Override
	public void stop() {
		if (!enabled) return;
		sensorService.unregisterListener(this);
	}

	@Override
	public void resume() {
		if (!enabled) return;
		sensorService.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		sensorService.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {

		augieScape = av;

		Activity activity = (Activity) av.getContext();
		sensorService = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		if (sensorService == null) {
			Log.w(TAG, "can not get sensor service");
			return;
		}
		accelerometer = sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (accelerometer == null) {
			Log.w(TAG, "can not get accelerometer");
			return;
		}
		magnetometer = sensorService.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (magnetometer == null) {
			Log.w(TAG, "can not get magnetometer");
			return;
		}
		enabled = true;
	}

	@Override
	public DialogFragment getUI() {

		return null;
	}

	public Meta getMeta() {
		return META;
	}

	public final static Meta META = new Meta() {

		@Override
		public Class<? extends Augiement> getAugiementClass() {

			return Compass.class;
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
		public Set<CodeableName> getDependencyNames() {
			return deps;
		}
	};
	
    private float restrictAngle(float tmpAngle){
        while(tmpAngle>=180) tmpAngle-=360;
        while(tmpAngle<-180) tmpAngle+=360;
        return tmpAngle;
    }

    //x is a raw angle value from getOrientation(...)
    //y is the current filtered angle value
    private float calculateFilteredAngle(float x, float y){ 
        final float alpha = 0.3f;
        float diff = x-y;

        //here, we ensure that abs(diff)<=180
        diff = restrictAngle(diff);

        y += alpha*diff;
        //ensure that y stays within [-180, 180[ bounds
        y = restrictAngle(y);

        return y;
    }
    
    float mAngle0_azimuth=0;
    float mAngle1_pitch=0;
    float mAngle2_roll=0;

    float mAngle0_filtered_azimuth=0;
    float mAngle1_filtered_pitch=0;
    float mAngle2_filtered_roll=0;
    
    float Rmat[] = new float[9];
    float Imat[] = new float[9];
    float orientation[] = new float[3];
    
    public void processSensorData(){
        if (mGravity != null && mGeomagnetic != null) { 
            boolean success = SensorManager.getRotationMatrix(Rmat, Imat, mGravity, mGeomagnetic);
            if (success) {              
                SensorManager.getOrientation(Rmat, orientation);
                mAngle0_azimuth = (float)Math.toDegrees((double)orientation[0]); // orientation contains: azimut, pitch and roll
                mAngle1_pitch = (float)Math.toDegrees((double)orientation[1]); //pitch
                mAngle2_roll = -(float)Math.toDegrees((double)orientation[2]); //roll               
                mAngle0_filtered_azimuth = calculateFilteredAngle(mAngle0_azimuth, mAngle0_filtered_azimuth);
                mAngle1_filtered_pitch = calculateFilteredAngle(mAngle1_pitch, mAngle1_filtered_pitch);
                mAngle2_filtered_roll = calculateFilteredAngle(mAngle2_roll, mAngle2_filtered_roll);    
            }           
            mGravity=null; //oblige full new refresh
            mGeomagnetic=null; //oblige full new refresh
        }
    }

	float[] mGravity;
	float[] mGeomagnetic;
	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
			mGravity = event.values.clone();
			return;
		}

		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			
			mGeomagnetic = event.values.clone();
		}
		
		processSensorData();

		//don't paint every hit
		//augieScape.reset();
	}

	private static String printDirection(float myAzimuth) {

		String txtDirection;

		if (myAzimuth < 22)
			txtDirection = "N";
		else if (myAzimuth >= 22 && myAzimuth < 67)
			txtDirection = "NE";
		else if (myAzimuth >= 67 && myAzimuth < 112)
			txtDirection = "E";
		else if (myAzimuth >= 112 && myAzimuth < 157)
			txtDirection = "SE";
		else if (myAzimuth >= 157 && myAzimuth < 202)
			txtDirection = "S";
		else if (myAzimuth >= 202 && myAzimuth < 247)
			txtDirection = "SW";
		else if (myAzimuth >= 247 && myAzimuth < 292)
			txtDirection = "W";
		else if (myAzimuth >= 292 && myAzimuth < 337)
			txtDirection = "NW";
		else if (myAzimuth >= 337)
			txtDirection = "N";
		else
			txtDirection = "unknown";

		return txtDirection;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
