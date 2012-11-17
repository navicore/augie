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
import java.util.Locale;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class SimpleCameraShutterFeature extends CameraShutterFeature implements OnTouchListener {
	
	protected final SharedPreferences prefs;
	protected final AugCamera augcamera;
	protected final AugDrawFeature augdraw;
	
	private final Context context;
	private final PictureCallback jpgCb;
	private final PictureCallback rawCb;
	
	public SimpleCameraShutterFeature(Context ctx, AugCamera c, AugDrawFeature d, SharedPreferences p) {
	    super();
	    prefs = p;
	    context = ctx;
	    augcamera = c;
	    augdraw = d;
	    jpgCb = new CameraPictureCallback(".jpg");
	    rawCb = new CameraPictureCallback(".raw");
    }
	
	@Override
	public void updateBmp() {
		//noop	
	}

	protected void takePicture() {
	    if (!prefs.getBoolean("TOUCH_SHOOT_ENABLED", true)) return;
	    Camera c = augcamera.getCamera();
	    if (c != null)  {
	        if (prefs.getBoolean("SAVE_RAW_ENABLED", false))
	            c.takePicture(null, rawCb, jpgCb);
	        else
	            c.takePicture(null, null, jpgCb);
	        augdraw.undoLastScrible();
	    } else {
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

    private class CameraPictureCallback implements PictureCallback {
        
        private final String suffix;
        
        CameraPictureCallback(String suffix) {
            this.suffix = suffix;
        }

        public void onPictureTaken(byte[] data, Camera camera) {

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
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            Camera c = augcamera.getCamera();
            if (c != null)
            	c.startPreview();
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
        Log.d(TAG, "SimpleCameraShutterFeature resume");
		//noop
	}

	@Override
	public void clear() {
		//noop
    }
}
