/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.testcamera;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugmentedView;
import com.onextent.augie.AugmentedViewFeature;
import com.onextent.augie.HorizonFeature;
import com.onextent.augie.ShakeResetFeature;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraPreview;
import com.onextent.augie.camera.CameraShutterFeature;
import com.onextent.augie.testcamera.R;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;

import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.util.Log;
import android.os.Bundle;

public class TestCameraActivity extends Activity {
	
	private AugmentedView augmentedView;
	
	public static final String TAG = "OETEST";  //for logging
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);

        augmentedView = new AugmentedView(this);
        
        AugCamera augcamera = new AugCamera();
        augmentedView.addFeature(augcamera);
        CameraPreview camPreview = new CameraPreview(this, augcamera);
        
        AugmentedViewFeature drawer = new AugDrawFeature(augmentedView, this);
        augmentedView.addFeature(drawer);
        
        AugmentedViewFeature horizon = new HorizonFeature(augmentedView);
        augmentedView.addFeature(horizon);
        
        AugmentedViewFeature shutter = new CameraShutterFeature(augcamera);
        augmentedView.addFeature(shutter);
        
        ShakeResetFeature shakeReseter = new ShakeResetFeature(augmentedView, this);
        augmentedView.addFeature(shakeReseter);
        shakeReseter.registerOneShakeReset(drawer);
        shakeReseter.registerOneShakeReset(horizon); //todo: on 2nd shake only
        
        FrameLayout layout = (FrameLayout) findViewById(R.id.camera_preview);
        layout.addView(camPreview); //bottom layer
        layout.addView(augmentedView); //transparent top layer
        layout.setOnTouchListener(augmentedView);
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		augmentedView.stop();
	}

    @Override
    protected void onResume() {
      super.onResume();
      augmentedView.resume();
    }

    @Override
    protected void onStop() {
      augmentedView.stop();
      super.onStop();
    }
}
