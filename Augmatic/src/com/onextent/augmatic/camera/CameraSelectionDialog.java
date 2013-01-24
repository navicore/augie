package com.onextent.augmatic.camera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.CameraName;

public class CameraSelectionDialog extends SherlockDialogFragment {
    
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        final List<CameraName> cameraNames = new ArrayList<CameraName>();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] items = new String[names.size()];
        names.toArray(items);
        builder.setTitle("Choose Camera").setSingleChoiceItems(items, currentCameraIdPos, new Dialog.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
               
                CameraName cn = cameraNames.get(which);
                setCamera(cn);
            }
        })
        .setNeutralButton("Camera Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               
                CameraSettingsListDialog f = new CameraSettingsListDialog();
                f.show(getActivity().getSupportFragmentManager(), "Camera Settings");
                f.show((getActivity()).getSupportFragmentManager(), "Camera Settings");
            }
        });
        
        return builder.create();
    }
}
