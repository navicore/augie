/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.fonecam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.DialogFragment;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.android.codeable.Size;
import com.onextent.augie.AugSysLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugFaceListener;
import com.onextent.augie.camera.AugFocusCallback;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.camera.AugPreviewCallback;
import com.onextent.augie.camera.AugShutterCallback;
import com.onextent.augie.camera.ImageFmt;
import com.onextent.augie.camera.NamedInt;

public class FoneCam implements AugCamera {

    //public static final Integer BACK_CAMERA_ID = 0;
    //public static final Integer FRONT_CAMERA_ID = 1;
    
    static final CodeableName PARAMS_CODEABLE_NAME = new CodeableName(
            "/AUGIE/CAMERA/PARAMS") {
    };
    protected Camera camera;
    private boolean facing = false;

    protected final int cameraId;
    private boolean addWithBuffer = false;
    private final List<AugPreviewCallback> previewCallbacks = new ArrayList<AugPreviewCallback>();;

    private CamParams params;
    private List<byte[]> buffers;

    private String uiname;
    private CodeableName cname; //cameraName is different from codeable uiname
  
    public FoneCam(int id, CodeableName cameraName, String uiname) {
 
        this.cname = cameraName;
        this.cameraId = id;
        this.uiname = uiname;
    }
    
    private final static int NBUFFERS = 3;

    private void initBuffers() {
        Parameters p = camera.getParameters();

        android.hardware.Camera.Size sz = p.getPreviewSize();
        int format = p.getPreviewFormat();
        int bsz;
        if (format == ImageFormat.YV12) {
            int width = sz.width;
            int height = sz.height;
            int yStride = (int) Math.ceil(width / 16.0) * 16;
            int uvStride = (int) Math.ceil((yStride / 2) / 16.0) * 16;
            int ySize = yStride * height;
            int uvSize = uvStride * height / 2;
            bsz = ySize + uvSize * 2;
        } else {
            float bpp = ImageFormat.getBitsPerPixel(format);
            float bytepp = bpp / 8;
            bsz = (int) (sz.width * sz.height * bytepp);
        }
        for (int i = 0; i < NBUFFERS; i++) {
            byte b[] = new byte[bsz];
            camera.addCallbackBuffer(b);
        }
    }

    private final PreviewCallback prevCb = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            for (AugPreviewCallback cb : previewCallbacks) {
                cb.onPreviewFrame(data, camera);
            }
            if (addWithBuffer) {
                //todo: re-add buffer
            }
        }
    };

    private void initPreviewCallbacks() {

        if (!previewCallbacks.isEmpty()) {
            if (addWithBuffer) {
                initBuffers();
                camera.setPreviewCallbackWithBuffer(prevCb);
            } else {
                camera.setPreviewCallback(prevCb);
            }
        }

    }
    
    @Override
    public void open() throws AugCameraException {

        if (camera != null) return;

        try {
            AugSysLog.d("open camera with id: " + getId());
            camera = Camera.open(getId());
            
            initPreviewCallbacks();

            if (params == null) {
                initParams();
            } else {
                applyParameters();
            }

        } catch (Throwable e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public final CodeableName getCodeableName() {
        return AugCamera.AUGIE_NAME;
    }

    public final int getId() {
        return cameraId;
    }

    @Override
    public void close() throws AugCameraException {
        if (camera == null)
            return;
        stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void setPreviewDisplay(SurfaceHolder holder)
            throws AugCameraException {
        open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public void startPreview() throws AugCameraException {
        open();
        try {
            AugSysLog.d("starting preview...");
            camera.startPreview();
            AugSysLog.d("...preview started");
        } catch (Exception e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public void stopPreview() throws AugCameraException {
        if (camera == null)
            return;
        try {
            AugSysLog.d("stopping preview...");
            camera.setPreviewCallback(null);
            camera.stopPreview();
            AugSysLog.d("...preview stopped");
        } catch (Exception e) {
            throw new AugCameraException(e);
        }
    }

    @Override
    public void focus(final AugFocusCallback cb) throws AugCameraException {

        if (camera == null)
            throw new AugCameraException("camera is null");

        AutoFocusCallback fcb = new AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {

                try {

                    cb.onFocus(success);
                } catch (Throwable err) {
                    AugSysLog.e(err);
                }
            }
        };

        try {
            camera.autoFocus(fcb);
        } catch (Throwable err) {
            throw new AugCameraException(err);
        }
    }

    @Override
    public void takePicture(final AugShutterCallback shutter,
            final AugPictureCallback raw, final AugPictureCallback jpeg)
                    throws AugCameraException {
        Camera.ShutterCallback scb = null;
        Camera.PictureCallback rcb = null;
        Camera.PictureCallback jcb = null;

        if (shutter != null)
            scb = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

                AugSysLog.d("shutter callback");
                if (shutter != null)
                    shutter.onShutter();
            }
        };
        if (raw != null)
            rcb = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                try {
                    AugSysLog.d("raw callback");
                    if (raw != null)
                        raw.onPictureTaken(data, FoneCam.this);
                } finally {
                    unlock();
                }
            }
        };
        if (jpeg != null)
            jcb = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera c) {
                try {
                    AugSysLog.d("jpg callback");
                    if (jpeg != null)
                        jpeg.onPictureTaken(data, FoneCam.this);
                } finally {
                    unlock();
                }
            }
        };
        if (rcb == null && jcb == null)
            throw new AugCameraException("no callbacks");
        boolean locked = lock();
        if (!locked)
            throw new AugCameraException("camera busy");
        try {
            AugSysLog.d("camera taking picture");
            camera.takePicture(scb, rcb, jcb);
        } catch (Throwable err) {
            unlock();
        }
    }

    private boolean isLocked = false;

    private synchronized boolean lock() {
        if (isLocked)
            return false;
        AugSysLog.d("locking camera");
        isLocked = true;
        return true;
    }

    private synchronized void unlock() {
        AugSysLog.d("unlocking camera");
        isLocked = false;
    }

    @Override
    public AugCameraParameters getParameters() {

        return params;
    }

    protected CamParams newParams() {
        CamParams p = new CamParams(this);
        return p;
    }

    @Override
    public void initParams() {
        try {
            params = newParams();
            Camera.Parameters cp = camera.getParameters();

            // todo: update each setting
            String m = cp.getFlashMode();
            if (m != null)
                params.setFlashMode(m);

            m = cp.getColorEffect();
            if (m != null)
                params.setColorMode(m);

            m = cp.getWhiteBalance();
            if (m != null)
                params.setWhiteBalance(m);

            m = cp.getFocusMode();
            if (m != null)
                params.setFocusMode(m);

            m = cp.getAntibanding();
            if (m != null)
                params.setAntibanding(m);

            int v = cp.getPictureFormat();
            NamedInt f = new ImageFmt(v);
            params.setPictureFmt(f);

            v = cp.getPreviewFormat();
            f = new ImageFmt(v);
            params.setPreviewFmt(f);

            m = cp.get("picture-format");
            if (m != null) {
                params.setPictureFmt(null);
                params.setXPictureFmt(m);
            }
            m = cp.get("iso");
            if (m != null) {
                params.setXISO(m);
            }
            Camera.Size sz = cp.getPictureSize();
            if (sz != null)
                params.setPictureSize(new Size(sz));

            sz = cp.getPreviewSize();
            if (sz != null)
                params.setPreviewSize(new Size(sz));

            int i = cp.getJpegQuality();
            if (i > 0)
                params.setJpegQuality(i);

            i = cp.getJpegThumbnailQuality();
            if (i > 0)
                params.setJpegThumbnailQuality(i);

            i = cp.getExposureCompensation();
            if (i != 0)
                params.setExposureCompensation(i);

            i = cp.getZoom();
            if (i != 0)
                params.setZoom(i);

            int[] range = new int[2];
            cp.getPreviewFpsRange(range);
            params.setPreviewFPSRange(range[Parameters.PREVIEW_FPS_MIN_INDEX],
                    range[Parameters.PREVIEW_FPS_MAX_INDEX]);

        } catch (Throwable err) {
            params = null;
            AugSysLog.e(err.toString(), err);
        }
    }

    static ArrayList<String> split(String str, Character c) {
        if (str == null)
            return null;
        TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(
                c);
        splitter.setString(str);
        ArrayList<String> substrings = new ArrayList<String>();
        for (String s : splitter) {
            substrings.add(s);
        }
        return substrings;
    }

    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        code.put(cname);
        code.put(CAMERA_UINAME_KEY, uiname);
        code.put(CAMERA_ID_KEY, getId());
        if (params != null) {
            Code pcode = params.getCode();
            //code.put(Codeable.CODEABLE_NAME_KEY, getCodeableName());
            code.put("params", pcode);
        }
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        /*
        try {
            close();
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
         */
        cname = code.getCodeableName();
        uiname = code.getString(CAMERA_UINAME_KEY);
        //int id = code.getInt(CAMERA_ID_KEY);
        params = newParams();
        if (code.has("params")) {
            Code pcode = code.get("params");
            params.setCode(pcode);
        }
    }

    @Override
    public String getName() {
        return uiname;
    }

    @Override
    public void updateCanvas() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
    }

    protected Camera.Parameters getUpdatedCameraParameters() {
        Camera.Parameters cp = null;
        try {
            cp = camera.getParameters();
            AugCameraParameters p = getParameters();
            if (p == null || cp == null) {
                throw new java.lang.NullPointerException(
                        "can not update params");
            }
            if (p != null && cp != null) {
                // todo: update with each 2.3 setting
                String m;
                m = p.getFlashMode();
                if (m != null)
                    cp.setFlashMode(m);

                m = p.getColorMode();
                if (m != null)
                    cp.setColorEffect(m);

                m = p.getWhiteBalance();
                if (m != null)
                    cp.setWhiteBalance(m);

                m = p.getSceneMode();
                if (m != null)
                    cp.setSceneMode(m);

                m = p.getFocusMode();
                if (m != null)
                    cp.setFocusMode(m);

                m = p.getAntibanding();
                if (m != null)
                    cp.setAntibanding(m);

                NamedInt f = p.getPictureFmt();
                if (f != null)
                    cp.setPictureFormat(f.toInt());

                f = p.getPreviewFmt();
                if (f != null)
                    cp.setPreviewFormat(f.toInt());

                m = p.getXPictureFmt();
                if (m != null)
                    cp.set("picture-format", m);

                m = p.getXISO();
                if (m != null)
                    cp.set("iso", m);

                Size sz = p.getPictureSize();
                if (sz != null)
                    cp.setPictureSize(sz.getWidth(), sz.getHeight());

                sz = p.getPreviewSize();
                if (sz != null)
                    cp.setPreviewSize(sz.getWidth(), sz.getHeight());

                int i = p.getJpegQuality();
                if (i > 0)
                    cp.setJpegQuality(i);

                i = p.getJpegThumbnailQuality();
                if (i > 0)
                    cp.setJpegThumbnailQuality(i);

                i = p.getExposureCompensation();
                if (i != 0)
                    cp.setExposureCompensation(i);

                i = p.getZoom();
                if (i != 0)
                    cp.setZoom(i);

                int[] r = p.getPreviewFPSRange();
                if (r != null)
                    cp.setPreviewFpsRange(r[0], r[1]);
                
                List<Camera.Area> fa = p.getFocusAreas();
                if (fa != null) cp.setFocusAreas(fa);
                
                List<Camera.Area> ma = p.getMeteringAreas();
                if (ma != null) cp.setMeteringAreas(ma);
            }

        } catch (Throwable err) {
            AugSysLog.e(err.toString(), err);
        }

        return cp;
    }

    protected final void __applyParameters(Camera.Parameters cp)
            throws AugCameraException {
        if (cp != null) {
            try {

                camera.setParameters(getUpdatedCameraParameters());

            } catch (Throwable err) {
                AugSysLog.w("can not set camera parameters: " + err);
                try {
                    getParameters().rollback();
                } catch (CodeableException e) {
                    AugSysLog.w("rollback camera parameters failed: " + e);
                    //throw new AugCameraException(e);
                }
                //throw new AugCameraException(err);
            }
        }
    }

    @Override
    public void applyParameters() throws AugCameraException {
        Camera.Parameters cp = getUpdatedCameraParameters();
        __applyParameters(cp);
        
        //Camera.CameraInfo info = new Camera.CameraInfo();
        //Camera.getCameraInfo(getId(), info);
        //todo: ICS 17+ only
        //if (info.canDisableShutterSound) {
        //}
    }

    @Override
    public String flatten() {
        return camera.getParameters().flatten();
    }

    @Override
    public boolean isOpen() {
        return camera != null;
    }

    @Override
    public void addPreviewCallback(AugPreviewCallback cb) {
        AugSysLog.d("adding preview frame callback");
        previewCallbacks.add(cb);
        if (camera != null && previewCallbacks.size() == 1) {
            initPreviewCallbacks();
        }
    }

    @Override
    public void removePreviewCallback(AugPreviewCallback cb) {
        AugSysLog.d("removing preview frame callback");
        previewCallbacks.remove(cb);
    }

    @Override
    public void addCallbackBuffer(byte[] b) {
        if (camera == null) {
            buffers = new ArrayList<byte[]>();
            buffers.add(b);
        } else {
            camera.addCallbackBuffer(b);
        }
    }

    @Override
    public DialogFragment getUI() {
        return null;
    }

    public static final Meta META = new Meta() {

        @Override
        public Class<? extends Augiement> getAugiementClass() {
            return FoneCam.class;
        }

        @Override
        public CodeableName getCodeableName() {
            return AugCamera.AUGIE_NAME;
        }

        @Override
        public String getUIName() {
            return null;
        }

        @Override
        public String getDescription() {
            return "Phone Camera";
        }

        @Override
        public int getMinSdkVer() {
            return 0;
        }

        @Override
        public Set<CodeableName> getDependencyNames() {
            return null;
        }
    };

    @Override
    public Meta getMeta() {

        return new Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
                return null;
            }

            @Override
            public CodeableName getCodeableName() {
                return AugCamera.AUGIE_NAME;
            }

            @Override
            public String getUIName() {
                
                return "Phone Camera";
            }

            @Override
            public int getMinSdkVer() {
                return 0;
            }
            @Override
            public String getDescription() {
                return "Phone Camera";
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                return null;
            }
        };
    }
    
    @Override
    public CodeableName getCameraName() {
        return cname;
    }

    @Override
    public void setDisplayOrientation(int i) {
        if (camera != null)
            camera.setDisplayOrientation(i);
    }

    @Override
    public boolean open(AugCamera augcamera) throws AugCameraException {
        return false;
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        // broken, this does not work across mode changes or new activities yet
        /*
         * if (augcamera instanceof SimpleFoneCam) {
         * 
         * SimpleFoneCam thatcamera = (SimpleFoneCam) augcamera; if (getId() ==
         * thatcamera.getId()) { camera = thatcamera.camera;
         * 
         * previewCb = thatcamera.previewCb; previewCbWB =
         * thatcamera.previewCbWB; applyParameters(); return true; } } return
         * false;
         */
    }
    
    @Override
    public void startFaceDetection() {
        facing = true;
        camera.startFaceDetection();
    }
    @Override
    public void stopFaceDetection() {
        if (facing)
            camera.stopFaceDetection();
    }
    @Override
    public void setFaceDetectionListener(AugFaceListener faceListener) {
        if (camera == null) {
            AugSysLog.w("camera is null while trying to set face listener");
        } else {
            camera.setFaceDetectionListener(faceListener);
        }
    }
}
