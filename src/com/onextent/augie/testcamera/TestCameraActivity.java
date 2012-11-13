/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.testcamera;

import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugmentedView;
import com.onextent.augie.AugmentedViewFeature;
import com.onextent.augie.FrameLevelerFeature;
import com.onextent.augie.HorizonCheckFeature;
import com.onextent.augie.HorizonFeature;
import com.onextent.augie.ShakeResetFeature;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraPreview;
import com.onextent.augie.camera.CameraShutterFeature;
import com.onextent.augie.testcamera.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.util.Log;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class TestCameraActivity extends Activity {
	
	private AugmentedView augmentedView;
	
	static final String TAG = AugmentedViewFeature.TAG;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);

        augmentedView = new AugmentedView(this, prefs);
        
        AugCamera augcamera = new AugCamera();
        augmentedView.addFeature(augcamera);
        CameraPreview camPreview = new CameraPreview(this, augcamera);
        
        AugDrawFeature drawer = new AugDrawFeature(augmentedView, this);
        augmentedView.addFeature(drawer);
        
        HorizonFeature horizon = new HorizonFeature(augmentedView, drawer, prefs);
       
        AugmentedViewFeature horizonChecker = new HorizonCheckFeature(augmentedView, 
                horizon, this, prefs);
        augmentedView.addFeature(horizonChecker);
        AugmentedViewFeature frameLeveler = new FrameLevelerFeature(augmentedView, 
                horizon, this, prefs);
        augmentedView.addFeature(frameLeveler);

        augmentedView.addFeature(horizon); //paint over checker
        
        AugmentedViewFeature shutter = CameraShutterFeature.getInstance(this, augcamera, drawer, prefs, augmentedView);
        augmentedView.addFeature(shutter);
        
        ShakeResetFeature shakeReseter = new ShakeResetFeature(augmentedView, this);
        augmentedView.addFeature(shakeReseter);
        //shakeReseter.registerOneShakeReset(drawer);
        //shakeReseter.registerTwoShakeReset(horizon);
        shakeReseter.registerTwoShakeReset(drawer);
        shakeReseter.registerTwoShakeReset(shutter);
        
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        startActivity(new Intent(this, TestCameraPreferences.class));
        return(true);
    }
}
