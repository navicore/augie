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

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CameraShutterFeature implements AugmentedViewFeature {
	
	static final String TAG = TestCameraActivity.TAG;
	
	private final AugCamera augcamera;
	private final AugDrawFeature augdraw;
	private Point startP;
	
	public CameraShutterFeature(AugCamera c, AugDrawFeature d) {
	    super();
	    augcamera = c;
	    augdraw = d;
    }
	
	public void redraw() {
		//noop	
	}

	public boolean onTouch(View v, MotionEvent event) {

		try {
			
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
            startP = new Point((int) event.getX(), (int) event.getY());
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
            //Toast.makeText(cameraActivity, "action ptr down", Toast.LENGTH_SHORT).show();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:

            Point endP = new Point((int) event.getX(), (int) event.getY());
            double dist = Math.sqrt(Math.pow(startP.x-endP.x, 2) + Math.pow(startP.y-endP.y, 2));
            
            if (dist < 50) {
            	Camera c = augcamera.getCamera();
            	if (c != null)  {
            		//Toast.makeText(cameraActivity, "shooting...", Toast.LENGTH_SHORT).show();
            		c.takePicture(null, null, mPicture);
            		augdraw.undoLastScrible();
            	} else {
            		//Toast.makeText(cameraActivity, "error!  camera not found", Toast.LENGTH_SHORT).show();
            	}
            }
			break;
		case MotionEvent.ACTION_POINTER_UP:
            //Toast.makeText(cameraActivity, "action ptr up", Toast.LENGTH_SHORT).show();
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

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                //Toast.makeText(cameraActivity, "picture saved", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            Camera c = augcamera.getCamera();
            if (c != null)
            	c.startPreview();
        }
    };
    
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MikesCamera");
       
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
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
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
