/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import com.onextent.augmatic.R;
import com.onextent.augmatic.camera.CameraSelectionFrag;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;

public class CameraSettingsActivity extends BaseAugmaticActivity {

    public CameraSettingsActivity() {
    }
   
    @Override
    protected Button getMenuButton() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.settings;
    }

    @Override
    protected int getPreviewId() {
        return R.id.camera_settings_preview;
    }
    
    @Override
    protected void configMenuButton() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
            
        FragmentTransaction txn = getSupportFragmentManager().beginTransaction();
        
        CameraSelectionFrag cameraList;
        cameraList = new CameraSelectionFrag();
        txn.replace(R.id.main_list, cameraList);
        
        txn.commit();
    }
}
