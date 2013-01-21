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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableHandler;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.EventManager;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugFocusCallback;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.impl.GPS;

public class Shutter implements Augiement {

	public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/SHUTTER");
	public static final String UI_NAME = "Camera Shutter";
	public static final String DESCRIPTION = "Operates the camera shutter, stores the picture files.";

	protected AugieScape augieScape;
	protected AugCamera camera;

	private Context context;
	private EventManager eventManager;
	private AugPictureCallback jpgCb;
	private AugPictureCallback rawCb;
	private AugPictureCallback userCb;

	private boolean registerImageWithOS = true;

	private boolean showFileSavedToast;
	private String picturesDir = "Augie";
	private String picturesRoot = Environment.DIRECTORY_DCIM;

	private final Map<String, String> exifData = new HashMap<String, String>();

	final static Set<CodeableName> deps;
	static {
		deps = new HashSet<CodeableName>();
		deps.add(AugCamera.AUGIENAME);
	}

	@Override
	public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {

		augieScape = av;

		for (Augiement a : helpers) {
			if (a instanceof AugCamera) {
				camera = (AugCamera) a;
			}
		}
		if (camera == null) throw new AugiementException("camera feature is null");

		context = av.getContext();
		jpgCb = new JpgCameraPictureCallback();
		rawCb = new RawCameraPictureCallback();

		eventManager = (EventManager) context;
	}

	public void setExif(String tag, String value) {
		if (value == null) {
			exifData.remove(tag);
		} else {
			exifData.put(tag, value);
		}
	}
	public String getExif(String tag) {
		return exifData.get(tag);
	}

	@Override
	public void updateCanvas() {
		//noop	
	}

	public void takePicture(AugPictureCallback userCb) throws AugCameraException {
		this.userCb = userCb;
		_takePicture();
	}
	public void takePicture() throws AugCameraException {
		takePicture(null);
	}

	private void handleUserCb(byte[] data, AugCamera camera) {
		if (userCb != null) userCb.onPictureTaken(null,  null);
		userCb = null;
	}

	private void rememberRotation() {

		int prevPicOrientation = ((AugieActivity)context).getOrientation();
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
		exifData.put(ExifInterface.TAG_ORIENTATION, Integer.toString(newOrientation));
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
		if (camera != null)  {
			AugCameraParameters p = camera.getParameters();
			if ("raw".equals(p.getXPictureFmt())) {
				camera.takePicture(null, rawCb, null);
			} else {
				camera.takePicture(null, null, jpgCb);
			}
		} else {
			handleUserCb(null, camera);
			Log.e(TAG, "camera not found");
			Toast.makeText(context, "error!  camera not found", Toast.LENGTH_LONG).show();
		}
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
				//Toast.makeText(context, "error: no image data", Toast.LENGTH_LONG).show();
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
				updateExif(pictureFile);
			} catch (IOException e) {
				Log.e(TAG, e.toString(), e);
			}
			if (isRegisterImageWithOS()) galleryAddPic(pictureFile);
			handleUserCb(data, camera);
		}
	}

	private void updateExif(File pictureFile) throws IOException {

		//todo: need to set timestamp exif tag ie: 2003:08:11 16:45:32

		if (exifData.isEmpty()) return;

		ExifInterface exif = new ExifInterface(pictureFile.getPath());

		for (String tag : exifData.keySet()) 
			exif.setAttribute(tag, exifData.get(tag));

		exif.saveAttributes();
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

		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					getFileName(suffix));
		} else {
			return null;
		}

		return mediaFile;
	}
	//private String fileNameTemplate = "Augie_Img_%y_%d_%t_%m_%s";
	private String fileNameTemplate = "Augie_%y%M%d_%h%m%s_image";
	public String getFileNameTemplate() {
		return fileNameTemplate;
	}

	public void setFileNameTemplate(String fileNameTemplate) {
		this.fileNameTemplate = fileNameTemplate;
	}

	private String getFileName(String suffix) {
		Date date = new Date();
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String year = new SimpleDateFormat("yyyy", Locale.US).format(date);
		String fn = fileNameTemplate.replace("%y", year);

		String dd = new SimpleDateFormat("dd", Locale.US).format(date);
		fn = fn.replace("%d", dd);

		String MM = new SimpleDateFormat("MM", Locale.US).format(date);
		fn = fn.replace("%M", MM);

		String HH = new SimpleDateFormat("HH", Locale.US).format(date);
		fn = fn.replace("%h", HH);

		String mm = new SimpleDateFormat("mm", Locale.US).format(date);
		fn = fn.replace("%m", mm);

		String ss = new SimpleDateFormat("ss", Locale.US).format(date);
		fn = fn.replace("%s", ss);

		return fn + suffix;
	}

	@Override
	public void stop() {
		Log.d(TAG, "stopping " + getClass().getName());
		if (eventManager == null) return;
		eventManager.unlisten(GPS.GPS_UPDATE_AUGIE_NAME, gpsEventHandler);
	}

	@Override
	public void resume() {
		if (eventManager == null) return;
		Log.d(TAG, "resuming " + getClass().getName());
		eventManager.listen(GPS.GPS_UPDATE_AUGIE_NAME, gpsEventHandler);
	}

	@Override
	public void clear() {
		Log.d(TAG, "clearing " + getClass().getName());
		//noop
	}

	CodeableHandler gpsEventHandler = new CodeableHandler() {

		@Override
		public void onCode(Code code) {
			double lat;
			double lon;
			try {
				lat = code.getDouble(GPS.LATITUDE_KEY);
				lon = code.getDouble(GPS.LONGITUDE_KEY);
			} catch (CodeableException e) {
				Log.e(TAG, e.toString(), e);
				return;
			}
			//String latitudeStr = "90/1,12/1,30/1";
			double alat = Math.abs(lat);
			String dms = Location.convert(alat, Location.FORMAT_SECONDS);
			String[] splits = dms.split(":");
			String[] secnds = (splits[2]).split("\\.");
			String seconds;
			if(secnds.length==0)
			{
				seconds = splits[2];
			}
			else
			{
				seconds = secnds[0];
			}

			String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
			exifData.put(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

			exifData.put(ExifInterface.TAG_GPS_LATITUDE_REF, lat>0?"N":"S");

			double alon = Math.abs(lon);


			dms = Location.convert(alon, Location.FORMAT_SECONDS);
			splits = dms.split(":");
			secnds = (splits[2]).split("\\.");

			if(secnds.length==0)
			{
				seconds = splits[2];
			}
			else
			{
				seconds = secnds[0];
			}
			String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";

			exifData.put(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
			exifData.put(ExifInterface.TAG_GPS_LONGITUDE_REF, lon>0?"E":"W");
		}
	};

	@Override
	public CodeableName getCodeableName() {
		return AUGIE_NAME;
	}

	private static final String SHOW_FILE_TOAST 		= "showFileToast";
	private static final String PICTURE_ROOT_DIR_KEY 	= "picRootDir";
	private static final String PICTURE_DIR_KEY 		= "picDir";
	private static final String REG_PICS_WITH_OS 		= "registerPics";
	private static final String FILENAME_TEMPLATE_KEY 	= "fnTemplate";

	@Override
	public Code getCode() throws CodeableException {

		Code code = JSONCoder.newCode();
		code.put(SHOW_FILE_TOAST, isShowFileSavedToast());
		code.put(PICTURE_ROOT_DIR_KEY, getPicturesRoot());
		code.put(PICTURE_DIR_KEY, getPicturesDir());
		code.put(REG_PICS_WITH_OS, isRegisterImageWithOS());
		code.put(FILENAME_TEMPLATE_KEY, getFileNameTemplate());

		return code;
	}

	@Override
	public void setCode(Code code) throws CodeableException {

		if (code.has(SHOW_FILE_TOAST))
			setShowFileSavedToast(code.getBoolean(SHOW_FILE_TOAST));
		if (code.has(PICTURE_ROOT_DIR_KEY))
			setPicturesRoot(code.getString(PICTURE_ROOT_DIR_KEY));
		if (code.has(PICTURE_DIR_KEY))
			setPicturesDir(code.getString(PICTURE_DIR_KEY));
		if (code.has(REG_PICS_WITH_OS))
			setRegisterImageWithOS(code.getBoolean(REG_PICS_WITH_OS));
		if (code.has(FILENAME_TEMPLATE_KEY))
			setFileNameTemplate(code.getString(FILENAME_TEMPLATE_KEY));
	}

	@Override
	public DialogFragment getUI() {

		return new ShutterDialog();
	}

	@Override
	public Meta getMeta() {
		return META;
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

	public static final Meta META =
			new Augiement.Meta() {

		@Override
		public Class<? extends Augiement> getAugiementClass() {

			return Shutter.class;
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
}
