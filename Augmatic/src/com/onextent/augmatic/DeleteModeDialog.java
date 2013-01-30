package com.onextent.augmatic;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;

public class DeleteModeDialog extends SherlockDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Modes");

        final ControlActivity activity = (ControlActivity) getActivity();
        final ModeManager modeManager = activity.getModeManager();
        List<Code> modes;
        try {
            modes = modeManager.getAllModeCode();
            List<String> nameList = new ArrayList<String>();
            final List<CodeableName> augieNameList = new ArrayList<CodeableName>();
            for (Code m : modes) {
                CodeableName augieName = m.getCodeableName(Codeable.CODEABLE_NAME_KEY);
                if (augieName.toString().indexOf("/SYSTEM/") < 0) {
                    nameList.add(m.getString(Codeable.UI_NAME_KEY));
                    augieNameList.add(augieName);
                }
            }

            final String[] modeNames = new String[nameList.size()];
            nameList.toArray(modeNames);
            boolean[] checkedItems = new boolean[nameList.size()];
            for (int i = 0; i < nameList.size(); i++) {
                checkedItems[i] = false;
            }

            final List<CodeableName> augieNamesToDelete = new ArrayList<CodeableName>();
            
            builder.setMultiChoiceItems(modeNames, checkedItems, new Dialog.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    
                    CodeableName  augieName = augieNameList.get(which);
                    AugAppLog.d( "sched to delete mode " + augieName + " which " + which);
                    if (isChecked) {
                        if (!augieNamesToDelete.contains(augieName)) {
                            augieNamesToDelete.add(augieName);
                        }
                    } else {
                        if (augieNamesToDelete.contains(augieName)) {
                            augieNamesToDelete.remove(augieName);
                        }
                    }
                }
            });

            builder.setPositiveButton("Delete Modes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    
                    for (CodeableName augieName : augieNamesToDelete) {
                        
                        AugAppLog.d( "trying to delete mode " + augieName);
                        CodeableName currName = modeManager.getCurrentMode().getCodeableName();
                        if (currName.toString().equals(augieName.toString())) {
                            AugAppLog.d( "deactivating mode " + augieName);
                            try {
                                activity.getModeManager().setCurrentMode(modeManager.getMode(new ModeName(ModeManager.MODE_KEY_DEFAULT)));
                            } catch (CodeableException e) {
                                AugAppLog.e( e.toString(), e);
                            } catch (AugieException e) {
                                AugAppLog.e( e.toString(), e);
                            }
                        }
                        AugAppLog.d( "delete mode " + augieName);
                        try {
                            modeManager.deleteMode(augieName);
                            activity.initModeList();
                        } catch (CodeableException e) {
                            AugAppLog.e( e.toString(), e);
                        } catch (AugieStoreException e) {
                            AugAppLog.e( e.toString(), e);
                        }
                    }
                }
            });
            
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            
            return builder.create();
            
        } catch (AugieStoreException e) {
            AugAppLog.e( e.toString(), e);
        } catch (CodeableException e) {
            AugAppLog.e( e.toString(), e);
        }
        return null;
    }
}
