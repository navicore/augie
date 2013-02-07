package com.onextent.augie.ments.cvface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugPreviewCallback;
import com.onextent.augie.ments.cvface.CvFaceFinder.DETECTION_TYPE;

public class SimpleCvFaceFinder extends AbstractCvFaceFinder 
implements AugPreviewCallback {

    private AugieScape augieScape;
    private AugCamera camera;
   
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
        mGray.release();
        mRgba.release();
	}
	@Override
	public void resume() {
	    
	    Context c = augieScape.getContext();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, c, new LoaderCallback(c));
	}
   
    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
        augieScape = av;
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

    @Override
    public Meta getMeta() {
        return null; //wrapper class must return meta
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
   
        Mat inputFrame = mFrameMat;
        inputFrame.copyTo(mRgba);
        Imgproc.cvtColor(inputFrame, mGray, Imgproc.COLOR_RGBA2GRAY);

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
        
        Rect[] facesArray = faces.toArray();
        AugLog.d("ejs number of faces: " + facesArray.length);

        //for (int i = 0; i < facesArray.length; i++)
        //    Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

    }

    @Override
    public int getFaceSizePct() {
        return faceSizePct;
    }

    @Override
    public void setFaceSizePct(int sz) {
        this.faceSizePct = sz;
        if (sz == 50)
            setMinFaceSize(0.5f);
        else if (sz == 40)
            setMinFaceSize(0.4f);
        else if (sz == 30)
            setMinFaceSize(0.3f);
        else if (sz == 20)
            setMinFaceSize(0.2f);
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }
    
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
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
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

                        SimpleCvFaceFinder.this.camera.addPreviewCallback(SimpleCvFaceFinder.this);
                        mGray = new Mat();
                        mRgba = new Mat();
                        int h = augieScape.getHeight();
                        int w = augieScape.getWidth();
                        mBaseMat = new Mat(h + (h/2), w, CvType.CV_8UC1);
                        mFrameMat = new Mat();
                        
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
}
