/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;

public class ShutterDialog extends SherlockDialogFragment {

    Shutter augiement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (Shutter) 
        			activity.getModeManager().getCurrentMode()
        			.getAugiements()
        			.get(Shutter.AUGIE_NAME);

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.shutter_settings, container, false);
        try {
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setFileSavedToastUI(v, camera);
            setPicFileRootDirUI(v, camera);
            setPicFileDirUI(v, camera);
            setRegisterImage(v, camera);

        } catch (Exception e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }

        return v;
    }

    private void setFileSavedToastUI(View v, AugCamera camera) {

        CheckBox cbox = (CheckBox) v.findViewById(R.id.showFileSavedToast);

        boolean isEnabled = augiement.isShowFileSavedToast();
        cbox.setChecked(isEnabled);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                augiement.setShowFileSavedToast(isChecked);
            }
        });
    }
  
    private void setRegisterImage(View v, AugCamera camera) {

        CheckBox cbox = (CheckBox) v.findViewById(R.id.fireMediaStoreIntent);

        boolean isEnabled = augiement.isRegisterImageWithOS();
        cbox.setChecked(isEnabled);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                augiement.setRegisterImageWithOS(isChecked);
            }
        });
    }
  
    private void setPicFileRootDirUI(View v, final AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.picFileRootDir);
        final List<String> list = new ArrayList<String>();
        list.add(Environment.DIRECTORY_DCIM);
        list.add(Environment.DIRECTORY_DOWNLOADS);
        list.add(Environment.DIRECTORY_PICTURES);
        list.add(Environment.DIRECTORY_MUSIC);
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return list.indexOf(augiement.getPicturesRoot());
            }
            @Override
            public void setMode(String m) {
                augiement.setPicturesRoot(m);
            }
        };
        sui.init();
    }
    
    private void setPicFileDirUI(View v, final AugCamera camera) {

        final EditText te = (EditText) v.findViewById(R.id.picFileDir);
        te.setText(augiement.getPicturesDir());
        te.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				augiement.setPicturesDir(s.toString());
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
        });
    }
}
