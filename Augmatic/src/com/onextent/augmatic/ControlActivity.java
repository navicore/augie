/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraName;
import com.onextent.augmatic.camera.ImageSettingsDialog;
import com.onextent.augmatic.camera.ProcessingSettingsDialog;
import com.onextent.augmatic.camera.ShootingSettingsDialog;

public class ControlActivity extends BaseAugmaticActivity {

    private final String[] camSettingCatagories = {"Processing", "Image File", "Shooting"};
    private final List<CameraName> cameraNames = new ArrayList<CameraName>();
    private List<Code> modeCode;
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
        return helper;
    }

    @Override
    protected Button getMenuButton() {
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
    protected void configMenuButton() { }
    
    final private OnClickListener newModeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            SherlockDialogFragment ald = new NewModeDialog();
            ald.show(getSupportFragmentManager(), "New Mode Fragment");
        }
    };
    final private OnClickListener delModeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            SherlockDialogFragment ald = new DeleteModeDialog();
            ald.show(getSupportFragmentManager(), "Delete Mode Fragment");
        }
    };
    
    final private OnItemClickListener cameraListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            CameraName cn = cameraNames.get(position);
            setCamera(cn);
            initCameraCatagoryList();
            showEmptySettingsDetails();
        }
    };
    
    final private OnItemClickListener augiementListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            helper.initDialogs(position);
            initCameraCatagoryList(); //todo: wasteful, just wanna deselect
        }
    };
    
    final private OnItemClickListener modeListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            
        Code code = modeCode.get(position);

        CodeableName modeName;
        try {
            modeName = code.getCodeableName(Codeable.CODEABLE_NAME_KEY);
            setMode(modeName);
            initCameraList();
            initAugiementList();
            showEmptySettingsDetails();
        } catch (CodeableException e) {
            AugLog.e( e.toString(), e);
        } catch (AugieException e) {
            AugLog.e( e.toString(), e);
        }

        }
    };
    
    final private OnItemClickListener cameraCatagoryListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (isDualPane) {
                showEmptySettingsDetails();
                showCameraDetails(position);
                initAugiementList(); //todo: wasteful, just wanna deselect
            } else {
                showCameraDetails(position);
            }
        }
    };
    
    private void setMode(CodeableName cn) throws CodeableException, AugieException {

        ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
        Mode m = modeManager.getMode(cn);
        if (m == null) throw new AugieException("mode not found") {
            private static final long serialVersionUID = -6373280937418946550L;
        };
        modeManager.setCurrentMode(m);
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
        
        View detailsFrame = getActivity().findViewById(R.id.module_details);
        isDualPane = detailsFrame != null;
        
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            mCurAugiementPosition = savedInstanceState.getInt("curAugieChoice", 0);
        }
        initCameraList();
        try {
            initModeList();
        } catch (AugieStoreException e) {
            AugLog.e(e);
            finish();
        } catch (CodeableException e) {
            AugLog.e(e);
            finish();
        }
        initAugiementList();
    }
   
    public void initModeList() throws AugieStoreException, CodeableException {
        
        List<String> names = new ArrayList<String>();
        ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
        modeCode = modeManager.getAllModeCode();
        String currentModeUIName = modeManager.getCurrentMode().getName();
        for (Code c : modeCode) {
            String n = c.getString(Codeable.UI_NAME_KEY);
            names.add(n);
        }
        int currentModePos = names.indexOf(currentModeUIName); 

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
        final ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
        Collection<AugCamera> cameras = modeManager.getCameraFactory().getCameras();
        CameraName currentCameraName = modeManager.getCurrentMode().getCamera().getCameraName();
        for (AugCamera c : cameras) {
            CameraName cn = c.getCameraName();
            cameraNames.add(cn);
            names.add(c.getName());
        }
        int currentCameraIdPos = cameraNames.indexOf(currentCameraName); 

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
        
        helper = new AugiementListHelper(this);
        helper.init();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.row, helper.getItems());
        augiementList = (ListView) findViewById(R.id.augiement_list);
        augiementList.setAdapter(adapter);
        augiementList.setOnItemClickListener(augiementListener);
    }
    
    private void showEmptySettingsDetails() {
        if (!isDualPane) return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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

            FragmentManager fm = getSupportFragmentManager();
            SherlockDialogFragment f = null;
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
            AugLog.e( err.toString(), err);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
        outState.putInt("curAugieChoice", mCurAugiementPosition);
    }
}
