package com.onextent.augie.ments.cvface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugPreviewCallback;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;
import com.onextent.augie.ments.Draw;
import com.onextent.augie.ments.OpenCV;
import com.onextent.augie.ments.shutter.Shutter;
import com.onextent.augie.ments.shutter.SimpleCameraShutter;

public class CvFaceFinder extends SimpleCameraShutter implements AugPreviewCallback, Augiement {
    
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/CVFACEFINDER");
    public static final String UI_NAME = "(CV) Face Finder";
    public static final String DESCRIPTION = "Face recognition focusing based on Open Computer Vision.";

	final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIE_NAME);
        deps.add(OpenCV.AUGIE_NAME);
        deps.add(Shutter.AUGIE_NAME);
        deps.add(Draw.AUGIE_NAME);
    }
    public enum DETECTION_TYPE {JAVA, NATIVE};
    private Mat                    mFrameMat;
    private Mat                    mBaseMat;
    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private DetectionBasedTracker  mNativeDetector;

    private int faceSizePct = 20;
    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private DETECTION_TYPE dtype = DETECTION_TYPE.JAVA;

    public CvFaceFinder() {
        updateMinFaceSize(getFaceSizePct());
    }
    
    @Override
    public CodeableName getCodeableName() {

        return AUGIE_NAME;
    }

    private static final String FACE_SZ_PCT_KEY = "face_sz_pct";
    private static final String NATIVE_DTYPE_KEY = "dtype_native";

    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        if (dtype == DETECTION_TYPE.NATIVE) code.put(NATIVE_DTYPE_KEY, true);
        code.put(FACE_SZ_PCT_KEY, faceSizePct);
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        if (code == null) return;
        if (code.has(NATIVE_DTYPE_KEY)) {
            dtype = DETECTION_TYPE.NATIVE;
        } else {
            dtype = DETECTION_TYPE.JAVA;
        }
        if (code.has(FACE_SZ_PCT_KEY)) {
            setFaceSizePct(code.getInt(FACE_SZ_PCT_KEY));
        }
    }

    @Override
    public void updateCanvas() {

        MatOfRect ma = getFacesArray();
        if (ma == null) return;
        Rect[] fa = ma.toArray();
        if (fa == null) return;
        
        List<Camera.Area> caa = new ArrayList<Camera.Area>();
       
        int mfa = camera.getParameters().getMaxNumFocusAreas();
        for (Rect r : fa) {
            
            augieScape.getCanvas().drawRect(getCanvasRect(r), augieScape.getPaint());
            
            if (caa.size() >= mfa) continue;
            
            android.graphics.Rect rect = getRelCoord(getCanvasRect(r));
            Camera.Area a = new Camera.Area(rect, 500);
            caa.add(a);
        }
        
        if (!caa.isEmpty()) {
            
            camera.getParameters().setFocusAreas(caa);
            try {
                camera.applyParameters();
            } catch (AugCameraException e) {
                AugLog.w(e);
            }
        }
    }
    
    private android.graphics.Rect getCanvasRect(Rect r) {
        float cw = augieScape.getWidth();
        float ch = augieScape.getHeight();
        com.onextent.android.codeable.Size prevSz = camera.getParameters().getPreviewSize();
        float pw = prevSz.getWidth();
        float ph = prevSz.getHeight();
        
        float xmulti = cw / pw;
        float ymulti = ch / ph;
        
        android.graphics.Rect arect = new android.graphics.Rect((int)(r.x * xmulti), (int)(r.y * ymulti), (int)((r.x + r.width) * xmulti), (int)((r.y + r.height) * ymulti));
        
        return arect;
    }

    @Override
    public void clear() {

    }

    protected void updateFocusAreas(List<Rect> faces) {
        //noop on pre-ICS devices 
    }

    @Override
    public void stop() {
        camera.removePreviewCallback(this);
        if (mGray != null) mGray.release();
        if (mRgba != null) mRgba.release();
    }
    @Override
    public void resume() {

        Context c = augieScape.getContext();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, c, new LoaderCallback(c));
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
        super.onCreate(av, helpers);
        for (Augiement a : helpers) {
            if (a instanceof AugCamera) {
                camera = (AugCamera) a;
                break;
            }
        }
        if (camera == null) throw new AugiementException("camera feature is null");
    }

    @Override
    public DialogFragment getUI() {

        return new CvFaceFinderDialog();
    }

    public DETECTION_TYPE getDtype() {
        return dtype;
    }

    public void setDtype(DETECTION_TYPE dtype) {
        this.dtype = dtype;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        mBaseMat.put(0, 0, data);
        Imgproc.cvtColor(mBaseMat, mFrameMat, Imgproc.COLOR_YUV2RGBA_NV21, 4);

        mFrameMat.copyTo(mRgba);
        Imgproc.cvtColor(mFrameMat, mGray, Imgproc.COLOR_RGBA2GRAY);

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (dtype == DETECTION_TYPE.JAVA) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (dtype == DETECTION_TYPE.NATIVE) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        else {
            AugLog.e("Detection method is not selected!");
        }

        setFacesArray(faces);
    }
    
    private MatOfRect faces;

    public synchronized MatOfRect getFacesArray() {
        return faces;
    }

    public synchronized void setFacesArray(MatOfRect faces) {
        if (this.faces != null) {
            this.faces.release();
        }
        this.faces = faces;
    }

    public int getFaceSizePct() {
        return faceSizePct;
    }

    public void updateMinFaceSize(int sz) {
        if (sz == 50)
            setMinFaceSize(0.5f);
        else if (sz == 40)
            setMinFaceSize(0.4f);
        else if (sz == 30)
            setMinFaceSize(0.3f);
        else if (sz == 20)
            setMinFaceSize(0.2f);
    }
    
    public void setFaceSizePct(int sz) {
        this.faceSizePct = sz;
        updateMinFaceSize(sz);
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    //static final String HAAR_FRONTAL_FILENANE = "haarcascade_frontalface_default.xml";
    static final String LBP_FRONTAL_FILENAME = "lbpcascade_frontalface.xml";
    static final String LBP_PROFILE_FILENAME = "lbpcascade_profileface.xml";
    
    private class LoaderCallback extends BaseLoaderCallback {

        private final Context context;
    
        public LoaderCallback(Context c) {
            super(c);
            context = c;
        }

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
            case LoaderCallbackInterface.SUCCESS:
            {
                AugLog.i("OpenCV loaded successfully");

                // Load native library after(!) OpenCV initialization
                System.loadLibrary("detection_based_tracker");

                try {
                    // load cascade file from application resources
                    InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
                    File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
                    mCascadeFile = new File(cascadeDir, LBP_FRONTAL_FILENAME);
                    //mCascadeFile = new File(cascadeDir, LBP_PROFILE_FILENAME);
                    FileOutputStream os = new FileOutputStream(mCascadeFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();

                    mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                    if (mJavaDetector.empty()) {
                        AugLog.e("Failed to load cascade classifier");
                        mJavaDetector = null;
                    } else
                        AugLog.i("Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                    mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                    cascadeDir.delete();

                    CvFaceFinder.this.camera.addPreviewCallback(CvFaceFinder.this);
                    mGray = new Mat();
                    mRgba = new Mat();
                    int h = camera.getParameters().getPreviewSize().getHeight();
                    int w = camera.getParameters().getPreviewSize().getWidth();
                    mBaseMat = new Mat(h + (h/2), w, CvType.CV_8UC1);
                    mFrameMat = new Mat();
                    AugLog.i("OpenCV Face Finder has started");

                } catch (IOException e) {
                    e.printStackTrace();
                    AugLog.e("Failed to load cascade. Exception thrown: " + e);
                }

            } break;
            default:
            {
                super.onManagerConnected(status);
            } break;
            }
        }
    };
    
    public static final Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return CvFaceFinder.class;
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

            @Override
            public int getMinSdkVer() {
                return Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            }
        };

    @Override
    public Meta getMeta() {

        return META;
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
                handleGesture(scrible);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            default:
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(augieScape.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            AugLog.e( e.toString(), e);
        }
        return true;
    }
    
    protected void handleGesture(AugScrible scrible) throws AugCameraException {

        if (scrible == null)  {
            AugLog.w("no current scrible / gesture");
            return;
        }
        GESTURE_TYPE g_type = scrible.getGestureType();
        switch (g_type) {

        case TAP:
            takePicture();
            break;
        case CLOCKWISE_AREA:
            break;
        case COUNTER_CLOCKWISE_AREA:
            break;
        default:
            AugLog.w("unrecognized gesture");
        }
    }
    
    protected void takePicture() throws AugCameraException {

        try {

            super.takePicture(new AugPictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, AugCamera c) {
                    AugLog.d("CvFaceFinder took picture");
                }
            });
        } catch (AugCameraException e) {

            throw e;
        }
    }
}
