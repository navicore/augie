/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.camera;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.impl.AugCameraFactoryImpl;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class SimpleCameraShutterFeature extends CameraShutterFeature implements OnTouchListener {
	
    protected AugieView augview;
	protected SharedPreferences prefs;
	protected AugCameraFactory cameraFactory;
	protected AugDrawFeature augdraw;
	
	private Context context;
	private AugPictureCallback jpgCb;
	private AugPictureCallback rawCb;
	
    private final static Set<String> deps;
    static {
        deps = new HashSet<String>();
        deps.add(AugCameraFactoryImpl.AUGIE_NAME);
        deps.add(AugDrawFeature.AUGIE_NAME);
    }

	@Override
    public Set<String> getDependencyNames() {
        return deps;
    }
	    
	@Override
    public void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException {
	    
	    augview = av;
	    
        for (Augiement a : helpers) {
            if (a instanceof AugCameraFactoryImpl) {
                cameraFactory = (AugCameraFactory) a;
            }
            else if (a instanceof AugDrawFeature) {
                augdraw = (AugDrawFeature) a;
            }
        }
        if (cameraFactory == null) throw new AugiementException("camera factory feature is null");
        if (augdraw == null) throw new AugiementException("draw feature is null");
        
	    context = av.getContext();
	    jpgCb = new CameraPictureCallback(".jpg");
	    rawCb = new CameraPictureCallback(".raw");
        prefs = PreferenceManager.getDefaultSharedPreferences(context); //todo: stop doing this
    }
	
	@Override
	public void updateCanvas() {
		//noop	
	}

	protected void takePicture() {
	    if (!prefs.getBoolean("TOUCH_SHOOT_ENABLED", true)) return;
	    AugCamera augcamera = null;
	    //todo: get name of current camera from ui
	    augcamera = cameraFactory.getCamera(null);
	    if (augcamera != null)  {
	        if (prefs.getBoolean("SAVE_RAW_ENABLED", false))
	            augcamera.takePicture(null, rawCb, jpgCb);
	        else
	            augcamera.takePicture(null, null, jpgCb);
	        augdraw.undoLastScrible();
	    } else {
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

    private class CameraPictureCallback implements AugPictureCallback {
        
        private final String suffix;
        
        CameraPictureCallback(String suffix) {
            this.suffix = suffix;
        }

        public void onPictureTaken(byte[] data, AugCamera camera) {

            if (data == null){
                Log.d(TAG, suffix + " data is null");
                Toast.makeText(context, "this device does not support " + suffix, Toast.LENGTH_LONG).show();
                return;
            }
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, suffix);
            if (pictureFile == null){
                String msg = "Error storing file, check storage permissions";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Log.d(TAG, msg);
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Toast.makeText(context, "file saved as " + pictureFile.getName(), Toast.LENGTH_LONG).show();
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
        }
    }
    
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, String suf){
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
            "IMG_"+ timeStamp + suf);
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + suf);
        } else {
            return null;
        }

        return mediaFile;
    }
    
	@Override
	public void stop() {
		//noop
	}

	@Override
	public void resume() {
		//noop
	}

	@Override
	public void clear() {
		//noop
    }
	
    public static final String AUGIE_NAME = "AUGIE/FEATURES/SIMPLE_SHUTTER";
	@Override
    public String getAugieName() {
        return AUGIE_NAME;
    }

}
