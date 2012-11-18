/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augie.augmatic;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.onextent.augie.AugDrawFeature;
import com.onextent.augie.AugieViewImpl;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.FrameLevelerFeature;
import com.onextent.augie.HorizonCheckFeature;
import com.onextent.augie.HorizonFeature;
import com.onextent.augie.ShakeResetFeature;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraPreview;
import com.onextent.augie.camera.CameraShutterFeature;
import com.onextent.augie.augmatic.R;

import android.content.Intent;
import android.graphics.PixelFormat;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.os.Bundle;

/**
 * Augmatic is the reference implementation camara for Augie
 * 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * T H E   A U G I E   A U G M A T I C   1 0 0 0 
 * 
 */
public class AugmaticActivity extends SherlockActivity {
	
	private AugieViewImpl augmentedView;
	
	static final String TAG = Augiement.TAG;
    Button menu_btn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

        setContentView(R.layout.main);

        augmentedView = new AugieViewImpl(this);

        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        //todo: think about camera, how to hide tethering vs front vs back vs streamer api
        AugCamera augcamera = new AugCamera();
        try {
            augmentedView.addFeature(augcamera);
            CameraPreview camPreview = new CameraPreview(this, augcamera);

            AugDrawFeature drawer = new AugDrawFeature();
            augmentedView.addFeature(drawer);

            HorizonFeature horizon = new HorizonFeature();
            augmentedView.addFeature(horizon);
            
            //todo: reimpl horizonChecker as CheckedHorizon so that red lines
            // are painted under white lines and only if 'correcting'

            Augiement horizonChecker = new HorizonCheckFeature();
            augmentedView.addFeature(horizonChecker);
            Augiement frameLeveler = new FrameLevelerFeature();
            augmentedView.addFeature(frameLeveler);

            Augiement shutter = CameraShutterFeature.getInstance(augcamera, drawer, augmentedView);
            augmentedView.addFeature(shutter);

            ShakeResetFeature shakeReseter = new ShakeResetFeature();
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
        } catch (AugiementException e) {
            Log.e(TAG, "can not create augmatic", e);
            e.printStackTrace();
            //todo: how to fail???
        } catch (Throwable err) {
            Log.e(TAG, "can not create augmatic", err);
            err.printStackTrace();
            //todo: how to fail???
        }
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
