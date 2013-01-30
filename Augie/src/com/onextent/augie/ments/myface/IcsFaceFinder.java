package com.onextent.augie.ments.myface;

import java.util.Set;

import android.annotation.TargetApi;
import android.graphics.Paint;
import android.hardware.Camera.Face;
import android.os.Build;
import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.AugFaceListener;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsFaceFinder extends AbstractFaceFinder {

    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    /////////////// UNTESTED, NEED ICS DEVICE THAT SUPPORTS FACES /////////////
    
    private AugieScape augieScape;
    private AugCamera camera;
    private Face[] drawfaces;

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
    
        if (drawfaces == null) return;

        Paint p = augieScape.getPaint();
        for (Face h : drawfaces) {
            augieScape.getCanvas().drawRect(h.rect, p);
            //todo: eyeball glasses and mustaches
        }
    }

    @Override
    public void clear() {
        drawfaces = null;
    }

    @Override
    public void stop() {
        camera.setFaceDetectionListener(null);
        camera.stopFaceDetection();
    }

    @Override
    public void resume() {
        // Try starting Face Detection
        AugCameraParameters p = camera.getParameters();

        // start face detection only *after* preview has started
        if (p.getMaxNumDetectedFaces() > 0){
            // camera supports face detection, so can start it:
            camera.setFaceDetectionListener(new FaceListener());
            camera.startFaceDetection();
        }
    }
   
    class FaceListener implements AugFaceListener {

        @Override
        public void onFaceDetection(Face[] faces, android.hardware.Camera camera) {
            if (faces.length > 0){
                AugLog.d("face detected: "+ faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY() );
            }
            setFaces(faces);
        }
    }
    private void setFaces(Face[] faces) {
       
        drawfaces = faces;
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

        return new FaceFinderDialog();
    }

    @Override
    public Meta getMeta() {
        return null; //wrapper class must return meta
    }
}
