/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments;

import java.util.HashSet;
import java.util.Set;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.content.Context;
import android.hardware.Camera;
import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugPreviewCallback;

public class OpenCV implements AugPreviewCallback, Augiement {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/OPENCV");
    public static final String UI_NAME = "Computer Vision";
    public static final String DESCRIPTION = "A helper augiement to enable computer vision features like face recognition and pano stitching.";

    private AugieScape augieScape;
    private AugCamera camera;

    private boolean libsFound = false;

    private class OpenCVLoaderCallback extends BaseLoaderCallback {

        public OpenCVLoaderCallback(Context c) {
            super(c);
        }

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
            case LoaderCallbackInterface.SUCCESS:
            {
                AugLog.i("OpenCV loaded successfully");
                setLibsFound(true);
            } break;
            default:
            {
                super.onManagerConnected(status);
            } break;
            }
        }
    };

    @Override
    public void stop() {
        camera.removePreviewCallback(this);
    }
    @Override
    public void resume() {
        camera.addPreviewCallback(this);

        Context c = augieScape.getContext();
        BaseLoaderCallback cb = new OpenCVLoaderCallback(c);

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, c, cb))
        {
            AugLog.e("Cannot connect to OpenCV Manager");
        }
    }

    private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIE_NAME);
    }

    public OpenCV() {
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {

        augieScape = av;

        for (Augiement a : helpers) {
            if (a instanceof AugCamera) {
                camera = (AugCamera) a;
            }
        }
        if (camera == null) throw new AugiementException("camera is null");
    }

    @Override
    public void updateCanvas() {

        if (!camera.isOpen()) return;

    }

    @Override
    public void clear() {
    }

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() throws CodeableException {

        Code code = JSONCoder.newCode();
        code.put(AUGIE_NAME);

        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        if (code == null) return;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
    }

    @Override
    public Meta getMeta() {
        return META;
    }

    public static final Meta META =
            new Augiement.Meta() {

        @Override
        public Class<? extends Augiement> getAugiementClass() {

            return OpenCV.class;
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
        public int getMinSdkVer() {
            return 0;
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

    @Override
    public DialogFragment getUI() {
        return null;
    }
    public synchronized boolean isLibsFound() {
        return libsFound;
    }
    private synchronized void setLibsFound(boolean libsFound) {
        this.libsFound = libsFound;
    }
}
