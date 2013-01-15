/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugFocusCallback;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class SimpleCameraShutterFeature extends CameraShutterFeature implements OnTouchListener {
	
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/SIMPLE_SHUTTER");
    
    protected AugieScape augieScape;
	protected SharedPreferences prefs;
	protected AugCamera camera;
	protected AugDrawFeature augdraw;
	
	private Context context;
	private AugPictureCallback jpgCb;
	private AugPictureCallback rawCb;
	private AugPictureCallback userCb;
	
    private int meterAreaColor = Color.GRAY;
    private int focusAreaColor = Color.GREEN;
    private boolean always_set_focus_area = true;
    private int touchFocusSz = 10;
    private boolean showFileSavedToast;

	final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIENAME);
        deps.add(AugDrawFeature.AUGIE_NAME);
    }

	@Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
	    
	    augieScape = av;
	    
        for (Augiement a : helpers) {
            if (a instanceof AugCamera) {
                camera = (AugCamera) a;
            }
            else if (a instanceof AugDrawFeature) {
                augdraw = (AugDrawFeature) a;
            }
        }
        if (camera == null) throw new AugiementException("camera feature is null");
        if (augdraw == null) throw new AugiementException("draw feature is null");
        
	    context = av.getContext();
	    jpgCb = new JpgCameraPictureCallback();
	    rawCb = new RawCameraPictureCallback();
        prefs = PreferenceManager.getDefaultSharedPreferences(context); //todo: stop doing this
    }
	
	@Override
	public void updateCanvas() {
		//noop	
	}

    protected void takePicture(AugPictureCallback userCb) throws AugCameraException {
        this.userCb = userCb;
        _takePicture();
	}
    protected void takePicture() throws AugCameraException {
        _takePicture();
	}
   
    private void handleUserCb(byte[] data, AugCamera camera) {
        if (userCb != null) userCb.onPictureTaken(null,  null);
        userCb = null;
    }
    
    protected void _takePicture() throws AugCameraException {
        Log.d(TAG, "ejs _take pic");
        
        try {

        String focusmode =  camera.getParameters().getFocusMode();
        if (focusmode.equals( Camera.Parameters.FOCUS_MODE_AUTO)) {

            camera.focus(new AugFocusCallback() {

                @Override
                public void onFocus(boolean success) {
                    Log.d(TAG, "auto focused: " + success);
                    if (!success) {
                        if (augieScape != null)
                        Toast.makeText(augieScape.getContext(), "can not focus", Toast.LENGTH_SHORT).show();
                        handleUserCb(null, camera);
                        return;
                    }

                    try {
                        __takePicture();
                        
                    } catch (AugCameraException e) {
                        handleUserCb(null, camera);
                        Log.d(TAG, e.toString(), e);
                    } 
                }
            });

        } else {
            __takePicture();
        }
        }  catch (AugCameraException e) {
            throw e;
        }
    }
    
	protected void __takePicture() throws AugCameraException {
	    if (!prefs.getBoolean("TOUCH_SHOOT_ENABLED", true)) return;
	    if (camera != null)  {
	        if (prefs.getBoolean("SAVE_RAW_ENABLED", false))
	            camera.takePicture(null, rawCb, jpgCb);
	        else
	            camera.takePicture(null, null, jpgCb);
	        augdraw.undoLastScrible();
	    } else {
	        handleUserCb(null, camera);
	        Log.e(TAG, "camera not found");
	        Toast.makeText(context, "error!  camera not found", Toast.LENGTH_LONG).show();
	    }
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:

            AugScrible scrible = augdraw.getCurrentScrible();
            if (scrible.getGestureType() == GESTURE_TYPE.TAP) takePicture();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		default:
			return false;
		}
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
	    return true;
    }

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

    class RawCameraPictureCallback extends CameraPictureCallback {
        public RawCameraPictureCallback() {
            super(".raw");
        }
    }
    class JpgCameraPictureCallback extends CameraPictureCallback {
        public JpgCameraPictureCallback() {
            super(".jpg");
        }
    }
    class CameraPictureCallback implements AugPictureCallback {
       
        final String suffix;
        CameraPictureCallback(String suffix) {
            this.suffix = suffix;
        }

        public void onPictureTaken(byte[] data, AugCamera camera) {

            if (data == null){
                Toast.makeText(context, "error: no image data", Toast.LENGTH_LONG).show();
                handleUserCb(data, camera);
                return;
            }
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, suffix);
            if (pictureFile == null){
                String msg = "Error storing file, check storage permissions";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Log.d(TAG, msg);
                handleUserCb(data, camera);
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                if (showFileSavedToast)
                	Toast.makeText(context, "file saved as " + 
                        pictureFile.getName(), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Error accessing file: " + e.getMessage());
            }
        
            if (camera != null)
                try {
                    //ejs todo: it is wrong to do this for both callbacks
                    Log.d(TAG, "restarting preview after taking pic");
                    camera.startPreview();
                    Log.d(TAG, "restarted preview after taking pic");
                } catch (AugCameraException e) {
                    Log.e(TAG, "Error starting preview after taking picture: " + e.getMessage(), e);
                }

            handleUserCb(data, camera);
        }
    }
    
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, String suffix){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_DCIM), "Augie");
//                  Environment.DIRECTORY_PICTURES), "Camera");
       
        if ( !mediaStorageDir.exists() ){
            if (! mediaStorageDir.mkdirs()){
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + suffix);
        //} else if(type == MEDIA_TYPE_VIDEO) {
        //    mediaFile = new File(mediaStorageDir.getPath() + File.separator +
        //    "VID_"+ timeStamp + suf);
        } else {
            return null;
        }

        return mediaFile;
    }
    
	@Override
	public void stop() {
        Log.d(TAG, "stopping " + getClass().getName());
		//noop
	}
	
	@Override
	public void resume() {
        Log.d(TAG, "resuming " + getClass().getName());
		//noop
	}

	@Override
	public void clear() {
        Log.d(TAG, "clearing " + getClass().getName());
		//noop
    }
	
	@Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    private static final String DEFAULT_FOCUS_SZ_KEY 	= "defaultFocusAreaSize";
    private static final String ALWAYS_FOCUS_AREA_KEY 	= "alwaysSetFocusArea";
    private static final String FOCUS_AREA_COLOR_KEY 	= "focusAreaColor";
    private static final String METER_AREA_COLOR_KEY 	= "meterAreaColor";
    private static final String SHOW_FILE_TOAST 		= "showFileToast";
    
    @Override
    public Code getCode() throws CodeableException {

        Code code = JSONCoder.newCode();
        code.put(FOCUS_AREA_COLOR_KEY, getFocusAreaColor());
        code.put(METER_AREA_COLOR_KEY, getMeterAreaColor());
        code.put(ALWAYS_FOCUS_AREA_KEY, isAlways_set_focus_area());
        code.put(DEFAULT_FOCUS_SZ_KEY, getTouchFocusSz());
        code.put(SHOW_FILE_TOAST, isShowFileSavedToast());
        
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {

    	if (code.has(FOCUS_AREA_COLOR_KEY)) 
    		setFocusAreaColor(code.getInt(FOCUS_AREA_COLOR_KEY));
    	if (code.has(METER_AREA_COLOR_KEY)) 
    		setMeterAreaColor(code.getInt(METER_AREA_COLOR_KEY));
    	if (code.has(ALWAYS_FOCUS_AREA_KEY)) 
    		setAlways_set_focus_area(code.getBoolean(ALWAYS_FOCUS_AREA_KEY));
    	if (code.has(DEFAULT_FOCUS_SZ_KEY))
    		setTouchFocusSz(code.getInt(DEFAULT_FOCUS_SZ_KEY));
    	if (code.has(SHOW_FILE_TOAST))
    		setShowFileSavedToast(code.getBoolean(SHOW_FILE_TOAST));
    }

    @Override
    public DialogFragment getUI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Meta getMeta() {
        return null;
    }
    
    public int getMeterAreaColor() {
        return meterAreaColor;
    }
    public void setMeterAreaColor(int meterAreaColor) {
        this.meterAreaColor = meterAreaColor;
    }
    public int getFocusAreaColor() {
        return focusAreaColor;
    }
    public void setFocusAreaColor(int focusAreaColor) {
        this.focusAreaColor = focusAreaColor;
    }
    public boolean isAlways_set_focus_area() {
        return always_set_focus_area;
    }
    public void setAlways_set_focus_area(boolean always_set_focus_area) {
        this.always_set_focus_area = always_set_focus_area;
    }

    public int getTouchFocusSz() {
        return touchFocusSz;
    }
    public void setTouchFocusSz(int sz) {
        this.touchFocusSz = sz;
    }
    
    public boolean isShowFileSavedToast() {
		return showFileSavedToast;
	}
	public void setShowFileSavedToast(boolean showFileSavedToast) {
		this.showFileSavedToast = showFileSavedToast;
	}
}
