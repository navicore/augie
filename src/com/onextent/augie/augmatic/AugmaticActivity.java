/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augie.augmatic;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.onextent.augie.AugieView;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.CameraPreview;
import com.onextent.augie.camera.CameraShutterFeature;
import com.onextent.augie.camera.impl.AugCameraFactoryImpl;
import com.onextent.augie.camera.impl.BackCamera;
import com.onextent.augie.camera.impl.FrontCamera;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.augie.impl.AugieViewImpl;
import com.onextent.augie.impl.FrameLevelerFeature;
import com.onextent.augie.impl.HorizonCheckFeature;
import com.onextent.augie.impl.HorizonFeature;
import com.onextent.augie.impl.ShakeResetFeature;
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

    private AugieView augmentedView;
    private CameraPreview camPreview;
    private AugCameraFactory cameraFactory;

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

        cameraFactory = new AugCameraFactoryImpl();
        cameraFactory.registerCamera(BackCamera.class, BackCamera.AUGIE_NAME);
        cameraFactory.registerCamera(FrontCamera.class, FrontCamera.AUGIE_NAME);
        try {
            augmentedView.addFeature(cameraFactory);
            //ejs, temporary, you will have to recreate CameraPreview when camera is changed
            //ejs, temporary, need to get camera name from UI
            AugCamera augcamera = cameraFactory.getCamera(null); 
            camPreview = new CameraPreview(this, augcamera);

            AugDrawFeature drawer = new AugDrawFeature();
            augmentedView.addFeature(drawer);

            augmentedView.addFeature(new HorizonFeature());

            //todo: reimpl horizonChecker as CheckedHorizon so that red lines
            // are painted under white lines and only if 'correcting'

            augmentedView.addFeature(new HorizonCheckFeature());

            augmentedView.addFeature(new FrameLevelerFeature());

            augmentedView.addFeature(CameraShutterFeature.getInstance(cameraFactory, drawer, augmentedView));

            ShakeResetFeature shakeReseter = new ShakeResetFeature();
            augmentedView.addFeature(shakeReseter);
            shakeReseter.registerTwoShakeReset(drawer);

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.camera_preview);
            //FrameLayout layout = (FrameLayout) findViewById(R.id.camera_preview);
            layout.addView(camPreview); //bottom layer
            layout.addView((View) augmentedView); //transparent top layer
            layout.setOnTouchListener(augmentedView);

            menu_btn=new Button(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            menu_btn.setLayoutParams(params);
            //FrameLayout.LayoutParams fl_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            //fl_params.addRule(FrameLayout.ALIGN_PARENT_RIGHT, FrameLayout.TRUE);
            //menu_btn.setLayoutParams(fl_params);
            menu_btn.setMinimumHeight(30);
            menu_btn.setMinimumWidth(30);
            menu_btn.setBackgroundResource(R.drawable.abs__ic_menu_moreoverflow_holo_dark);
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
        } catch (Throwable err) {
            Log.e(TAG, "can not create augmatic", err);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, getClass().getName() + " onPause");
        super.onPause();
        augmentedView.stop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, getClass().getName() + " onResume");
        super.onResume();
        augmentedView.resume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, getClass().getName() + " onStop");
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

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            startActivity(new Intent(this, AugmaticPreferences.class));
            return super.onPrepareOptionsMenu(menu);
        } else  {
            return super.onPrepareOptionsMenu(menu);
        }
    }
     */
}
