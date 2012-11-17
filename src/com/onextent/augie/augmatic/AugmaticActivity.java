/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augie.augmatic;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
import com.onextent.augie.augmatic.R;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Augmatic is the reference implementation camara for Augie
 * 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * 
 */
public class AugmaticActivity extends SherlockActivity {
	
	private AugmentedView augmentedView;
	
	static final String TAG = AugmentedViewFeature.TAG;
    Button menu_btn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

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
        shakeReseter.registerTwoShakeReset(drawer);
        
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.camera_preview);
        layout.addView(camPreview); //bottom layer
        layout.addView(augmentedView); //transparent top layer
        layout.setOnTouchListener(augmentedView);

        menu_btn=new Button(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        menu_btn.setMinimumHeight(30);
        menu_btn.setMinimumWidth(30);
        menu_btn.setBackgroundResource(R.drawable.abs__ic_menu_moreoverflow_holo_dark);
        menu_btn.setLayoutParams(params);
        layout.addView(menu_btn);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getSupportActionBar().show();
                menu_btn.setVisibility(View.GONE);
            }
        });
        getSupportActionBar().setBackgroundDrawable(null);
        getSupportActionBar().hide();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_mode:
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, AugmaticPreferences.class));
                return true;
            case R.id.menu_overlays:
                return true;
            case R.id.menu_hide:
                getSupportActionBar().hide();
                menu_btn.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
}
