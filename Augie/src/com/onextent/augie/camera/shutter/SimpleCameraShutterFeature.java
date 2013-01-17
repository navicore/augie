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
import com.onextent.augie.AugieActivity;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
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
    private boolean registerImageWithOS = true;

	private int touchFocusSz = 10;
    private boolean showFileSavedToast;
    private String picturesDir = "Augie";
	private String picturesRoot = Environment.DIRECTORY_DCIM;
	private int prevPicOrientation = ExifInterface.ORIENTATION_NORMAL;

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
    
    private void rememberRotation() {
    	
    	prevPicOrientation = ((AugieActivity)context).getOrientation();
    }
    
    protected void _takePicture() throws AugCameraException {
        
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
		rememberRotation();
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

            try {
				updateExifOrientation(pictureFile);
			} catch (IOException e) {
				Log.e(TAG, e.toString(), e);
			}
            if (isRegisterImageWithOS()) galleryAddPic(pictureFile);
            handleUserCb(data, camera);
        }

		private void updateExifOrientation(File pictureFile) throws IOException {
			ExifInterface exif = new ExifInterface(pictureFile.getPath());
			int newOrientation = ExifInterface.ORIENTATION_NORMAL;
			switch (prevPicOrientation) {
			case Surface.ROTATION_90:
				newOrientation = ExifInterface.ORIENTATION_ROTATE_90;
				break;
			case Surface.ROTATION_180:
				newOrientation = ExifInterface.ORIENTATION_ROTATE_180;
				break;
			case Surface.ROTATION_270:
				newOrientation = ExifInterface.ORIENTATION_ROTATE_270;
				break;
			case Surface.ROTATION_0:
			default:
				newOrientation = ExifInterface.ORIENTATION_NORMAL;
			}
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(newOrientation));
			exif.saveAttributes();
		}
    }
    
    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    
    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type, String suffix){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //          Environment.DIRECTORY_DCIM) + "/Augie2");
        File mediaStorageDir;
    	if (picturesDir == null) {
    		
    		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  picturesRoot), ".");
    		
    	} else {
    		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  picturesRoot), picturesDir);
    		
    	}
       
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
    private static final String PICTURE_ROOT_DIR_KEY 	= "picRootDir";
    private static final String PICTURE_DIR_KEY 		= "picDir";
    private static final String REG_PICS_WITH_OS 		= "registerPics";
    
    @Override
    public Code getCode() throws CodeableException {

        Code code = JSONCoder.newCode();
        code.put(FOCUS_AREA_COLOR_KEY, getFocusAreaColor());
        code.put(METER_AREA_COLOR_KEY, getMeterAreaColor());
        code.put(ALWAYS_FOCUS_AREA_KEY, isAlways_set_focus_area());
        code.put(DEFAULT_FOCUS_SZ_KEY, getTouchFocusSz());
        code.put(SHOW_FILE_TOAST, isShowFileSavedToast());
        code.put(PICTURE_ROOT_DIR_KEY, getPicturesRoot());
        code.put(PICTURE_DIR_KEY, getPicturesDir());
        code.put(REG_PICS_WITH_OS, isRegisterImageWithOS());
        
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
    	if (code.has(PICTURE_ROOT_DIR_KEY))
    		setPicturesRoot(code.getString(PICTURE_ROOT_DIR_KEY));
    	if (code.has(PICTURE_DIR_KEY))
    		setPicturesDir(code.getString(PICTURE_DIR_KEY));
    	if (code.has(REG_PICS_WITH_OS))
    		setRegisterImageWithOS(code.getBoolean(REG_PICS_WITH_OS));
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
    public String getPicturesDir() {
		return picturesDir;
	}

	public void setPicturesDir(String d) {
		this.picturesDir = d;
	}

	public String getPicturesRoot() {
		return picturesRoot;
	}

	public void setPicturesRoot(String r) {
		this.picturesRoot = r;
	}
	
    public boolean isRegisterImageWithOS() {
		return registerImageWithOS;
	}

	public void setRegisterImageWithOS(boolean r) {
		this.registerImageWithOS = r;
	}
}
