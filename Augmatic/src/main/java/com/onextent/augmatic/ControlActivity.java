/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.Mode;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraMeta;
import com.onextent.augie.camera.settings.ImageSettingsDialog;
import com.onextent.augie.camera.settings.ProcessingSettingsDialog;
import com.onextent.augie.camera.settings.ShootingSettingsDialog;

public class ControlActivity extends BaseAugmaticActivity {

    private final String[] camSettingCatagories = {"Processing", "Image File", "Shooting"};
    private final List<CodeableName> cameraNames = new ArrayList<CodeableName>();
    private ListView cameraList;
    private ListView cameraCatagoryList;
    private ListView augiementList;
    boolean isDualPane;
    int mCurCheckPosition = 0;
    
    int mCurAugiementPosition = 0;
    
    private AugiementListHelper helper;
    
    public ControlActivity() {
    }
   
    public AugiementListHelper getHelper() {
        if (helper == null) {
            helper = new AugiementListHelper(this);
            helper.init();
        }
        return helper;
    }

    @Override
    protected Button getControlLayout() {
        return null;
    }

    @Override
    protected int getLayoutId() {

        return R.layout.control;
    }

    @Override
    protected int getPreviewId() {
        return R.id.camera_settings_preview;
    }
    
    @Override
    protected View configMenuButton() { return null; }
    
    final private OnClickListener newModeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            showEmptySettingsDetails();
            unsetCameraCatagorySelection();
            unsetAugiementSelection();
            DialogFragment ald = new NewModeDialog();
            ald.show(getFragmentManager(), "New Mode Fragment");
        }
    };
    final private OnClickListener delModeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            showEmptySettingsDetails();
            unsetCameraCatagorySelection();
            unsetAugiementSelection();
            DialogFragment ald = new DeleteModeDialog();
            ald.show(getFragmentManager(), "Delete Mode Fragment");
        }
    };
    
    private void unsetAugiementSelection() {
        if (augiementList == null) return;
        int position = augiementList.getCheckedItemPosition();
        augiementList.setItemChecked(position, false);
    }
    private void unsetCameraCatagorySelection() {
        if (cameraCatagoryList == null) return;
        int position = cameraCatagoryList.getCheckedItemPosition();
        cameraCatagoryList.setItemChecked(position, false);
    }
    
    final private OnItemClickListener cameraListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            showEmptySettingsDetails();
            unsetCameraCatagorySelection();
            unsetAugiementSelection();
            CodeableName cn = cameraNames.get(position);
            setCamera(cn);
        }
    };
    
    final private OnItemClickListener augiementListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            showEmptySettingsDetails();
            unsetCameraCatagorySelection();
            getHelper().initDialogs(position);
        }
    };
    
    final private OnItemClickListener modeListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            
        Code code = null;
        try {
            code = modeManager.getAllModeCode().get(position);
            if (code == null) {
                AugAppLog.e("mode not found");
                return;
            }
        } catch (AugieStoreException e1) {
            AugAppLog.e(e1);
        }

        CodeableName modeName;
        try {
            showEmptySettingsDetails();
            unsetCameraCatagorySelection();
            unsetAugiementSelection();
            modeName = code.getCodeableName();
            setMode(modeName);
        } catch (CodeableException e) {
            AugAppLog.e( e.toString(), e);
        } catch (AugieException e) {
            AugAppLog.e( e.toString(), e);
        }

        }
    };
    
    final private OnItemClickListener cameraCatagoryListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            showEmptySettingsDetails();
            unsetAugiementSelection();
            showCameraDetails(position);
        }
    };
    
    private void setCamera(CodeableName cn) {
        
        Mode m = modeManager.getCurrentMode();
        try {
            AugCamera c = cameraFactory.getCamera(cn);
            m.deactivate();
            m.setCamera(c);
            m.activate();
        } catch (AugieException e) {
            AugAppLog.e( e.toString(), e);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        try {
            initModeList();
        } catch (AugieStoreException e) {
            AugAppLog.e(e);
            finish();
        } catch (CodeableException e) {
            AugAppLog.e(e);
            finish();
        }
        initCameraList();
        initAugiementList();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        
        View detailsFrame = findViewById(R.id.module_details);
        isDualPane = detailsFrame != null;
        
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            mCurAugiementPosition = savedInstanceState.getInt("curAugieChoice", 0);
        }
    }
  

    public void initModeList() throws AugieStoreException, CodeableException {
        
        List<String> names = modeManager.getModeNameStrings();
        int currentModePos = modeManager.getCurrentModePos(names);

        String[] items = new String[names.size()];
        names.toArray(items);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.row, items);
        cameraList = (ListView) findViewById(R.id.mode_list);
        cameraList.setAdapter(adapter);
        cameraList.setItemChecked(currentModePos, true);
        cameraList.setOnItemClickListener(modeListener);
        
        Button newb = (Button) findViewById(R.id.add_mode_btn);
        newb.setOnClickListener(newModeListener);
        Button delb = (Button) findViewById(R.id.del_mode_btn);
        delb.setOnClickListener(delModeListener);
    }
    
    public void initCameraList() {
        
        List<String> names = new ArrayList<String>();
        Collection<CameraMeta> cameras = cameraFactory.getCameras();
        CodeableName currentCodeableName = modeManager.getCurrentMode().getCamera().getCameraName();
        for (CameraMeta c : cameras) {
            CodeableName cn = c.getCn();
            cameraNames.add(cn);
            names.add(c.getUiname());
        }
        int currentCameraIdPos = cameraNames.indexOf(currentCodeableName); 

        String[] items = new String[names.size()];
        names.toArray(items);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.row, items);
        cameraList = (ListView) findViewById(R.id.camera_list);
        cameraList.setAdapter(adapter);
        cameraList.setItemChecked(currentCameraIdPos, true);
        cameraList.setOnItemClickListener(cameraListener);
        initCameraCatagoryList();
    }
    
    public void initCameraCatagoryList() {
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.row, camSettingCatagories);
        cameraCatagoryList = (ListView) findViewById(R.id.camera_catagory_list);
        cameraCatagoryList.setAdapter(adapter);
        cameraCatagoryList.setOnItemClickListener(cameraCatagoryListener);
    }
    
    public void initAugiementList() {
       
        getHelper();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.row, getHelper().getItems());
        augiementList = (ListView) findViewById(R.id.augiement_list);
        augiementList.setAdapter(adapter);
        augiementList.setOnItemClickListener(augiementListener);
    }
    
    private void showEmptySettingsDetails() {
        if (!isDualPane) return;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.module_status, new EmptySettingsDialog());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.module_details, new EmptySettingsDialog());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    
    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showCameraDetails(int index) {
        try {
            mCurCheckPosition = index;

            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            cameraCatagoryList.setItemChecked(index, true);

            FragmentManager fm = getFragmentManager();
            DialogFragment f = null;
            switch (index) {
            case 0:
                f = new ProcessingSettingsDialog();
                break;
            case 1:
                f = new ImageSettingsDialog();
                break;
            case 2:
                f = new ShootingSettingsDialog();
                break;
            default:
            }
            if (f != null) {
                if (isDualPane) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.module_details, f);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();

                } else {
                    f.show(fm, "Camera Settings");
                }
            }
        } catch (Throwable err) {
            AugAppLog.e( err.toString(), err);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
        outState.putInt("curAugieChoice", mCurAugiementPosition);
    }

    @Override
    protected void activateSwipeNav(boolean activate) { }
}
