package com.onextent.augmatic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;

public class NewModeDialog extends DialogFragment {

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
                AugAppLog.d( "new mode name " + text);

                ControlActivity activity = (ControlActivity) getActivity();
                ModeManager modeManager = activity.getModeManager();

                try {
                    Mode mode = modeManager.newMode();
                    mode.setCode(modeManager.getCurrentMode().getCode()); //clone
                    //fix new names
                    mode.setName(text); 
                    mode.setCodeableName(new CodeableName("MODE/USER/" + text));
                    modeManager.addMode(mode);
                    modeManager.setCurrentMode(mode);

                    ((AugieActivity) getActivity()).getModeManager().setCurrentMode(mode);
                    activity.initModeList();
                } catch (AugieException e1) {
                    AugAppLog.e( e1.toString(), e1);
                } catch (CodeableException e1) {
                    AugAppLog.e( e1.toString(), e1);
                }
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
