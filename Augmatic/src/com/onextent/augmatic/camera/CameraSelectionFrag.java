package com.onextent.augmatic.camera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraName;
import com.onextent.augmatic.R;

public class CameraSelectionFrag extends SherlockListFragment {
    

    final List<CameraName> cameraNames = new ArrayList<CameraName>();
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
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

        //android.R.layout.simple_list_item_activated_1, 
        setListAdapter(
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.item, 
                        items)
                        );
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setItemChecked(currentCameraIdPos, true);
        
        updateUI();
    }
    
    private void updateUI() {
        
        FragmentTransaction txn = getActivity().getSupportFragmentManager().beginTransaction();
        CameraSettingsListFrag settingsList = new CameraSettingsListFrag();
        txn.replace(R.id.module_list, settingsList);
        txn.commit();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        CameraName cn = cameraNames.get(position);
        setCamera(cn);
        updateUI();
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
}
