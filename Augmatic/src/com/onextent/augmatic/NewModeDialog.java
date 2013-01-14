package com.onextent.augmatic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.ModeManager;
import com.onextent.augie.ModeName;
import com.onextent.augie.Mode;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;

public class NewModeDialog extends SherlockDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Enter new mode name");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(com.onextent.augmatic.R.layout.mode_new_layout, null));

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                EditText e = (EditText) getDialog().findViewById(com.onextent.augmatic.R.id.new_mode_name);
                String text = e.getText().toString();
                Log.d(Codeable.TAG, "new mode name " + text);

                ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();

                try {
                    Mode mode = modeManager.newMode();
                    mode.setCode(modeManager.getCurrentMode().getCode()); //clone
                    //fix new names
                    mode.setName(text); 
                    mode.setCodeableName(new ModeName("MODE/USER/" + text));
                    modeManager.addMode(mode);
                    modeManager.setCurrentMode(mode);

                    ((AugieActivity) getActivity()).getModeManager().setCurrentMode(mode);
                } catch (AugieException e1) {
                    Log.e(Codeable.TAG, e1.toString(), e1);
                } catch (CodeableException e1) {
                    Log.e(Codeable.TAG, e1.toString(), e1);
                }
                ((BaseAugmaticActivity) getActivity()).getSupportActionBar().setSelectedNavigationItem(modeManager.getCurrentModeIdx());
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        return builder.create();
    }
}