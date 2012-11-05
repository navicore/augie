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

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugmentedViewFeature;
import com.onextent.augie.testcamera.TestCameraActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CameraShutterFeature implements AugmentedViewFeature {
	
    private static final int MAX_SCRIBLE_LEN = 10;
    private static final int MAX_SCRIBLE_END_DISTANCE = 50;

	private final AugCamera augcamera;
	private final AugDrawFeature augdraw;
	private Point startP;
	private final SharedPreferences prefs;
	private final Context context;
	private final PictureCallback jpgCb;
	private final PictureCallback rawCb;
	
	public CameraShutterFeature(Context ctx, AugCamera c, AugDrawFeature d, SharedPreferences p) {
	    super();
	    prefs = p;
	    context = ctx;
	    augcamera = c;
	    augdraw = d;
	    jpgCb = new CameraPictureCallback(".jpg");
	    rawCb = new CameraPictureCallback(".raw");
    }
	
	public void updateBmp() {
		//noop	
	}

	public boolean onTouch(View v, MotionEvent event) {

		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
            startP = new Point((int) event.getX(), (int) event.getY());
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:

            Point endP = new Point((int) event.getX(), (int) event.getY());
            double dist = Math.sqrt(Math.pow(startP.x-endP.x, 2) + Math.pow(startP.y-endP.y, 2));
           
            int scrlen = augdraw.getScribleLength();
            if (scrlen < MAX_SCRIBLE_LEN && dist < MAX_SCRIBLE_END_DISTANCE) {
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

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
	public void stop() {
		//noop
	}

	public void resume() {
		//noop
	}

	public void clear() {
		//noop
    }
}
