/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraName;
import com.onextent.augmatic.camera.CameraSettingsListFrag;

public class CameraSettingsActivity extends BaseAugmaticActivity {

    public CameraSettingsActivity() {
    }
   
    @Override
    protected Button getMenuButton() {
        return null;
    }

    @Override
    protected int getLayoutId() {

        return R.layout.mode_and_camera_settings;
    }

    @Override
    protected int getPreviewId() {
        return R.id.camera_settings_preview;
    }
    
    @Override
    protected void configMenuButton() { }

    final List<CameraName> cameraNames = new ArrayList<CameraName>();
    
    final private OnItemClickListener mainListClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            CameraName cn = cameraNames.get(position);
            setCamera(cn);
            updateUI();
            showEmptySettingsDetails();
        }
    };
    
    private void updateUI() {
        
        FragmentTransaction txn = getSupportFragmentManager().beginTransaction();
        CameraSettingsListFrag settingsList = new CameraSettingsListFrag();
        txn.replace(R.id.module_list, settingsList);
        txn.commit();
    }
    
    private void setCamera(CameraName cn) {
        
        ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
        Mode m = modeManager.getCurrentMode();
        try {
            AugCamera c = modeManager.getCameraFactory().getCamera(cn);
            m.deactivate();
            m.setCamera(c);
            m.activate();
        } catch (AugieException e) {
            AugLog.e( e.toString(), e);
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        
        initCameraList();
    }
    
    public void initCameraList() {
        
        List<String> names = new ArrayList<String>();
        final ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
        Collection<AugCamera> cameras = modeManager.getCameraFactory().getCameras();
        CameraName currentCameraName = modeManager.getCurrentMode().getCamera().getCameraName();
        int currentCameraIdPos = -1;
        for (AugCamera c : cameras) {
            CameraName cn = c.getCameraName();
            cameraNames.add(cn);
            names.add(c.getName());
        }
        currentCameraIdPos = cameraNames.indexOf(currentCameraName); 

        String[] items = new String[names.size()];
        names.toArray(items);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.row, items);
        ListView listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(adapter);
        listView.setItemChecked(currentCameraIdPos, true);
        listView.setOnItemClickListener(mainListClickListener);
        listView.setSelector(R.drawable.row_selector);
        updateUI();
    }
    
    private void showEmptySettingsDetails() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.module_details, new EmptySettingsDialog());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
