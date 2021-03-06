/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.shutter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.R;

public class ShutterDialog extends DialogFragment {

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

            setFileSavedToastUI(v);
            setPicFileRootDirUI(v);
            setPicFileDirUI(v);
            setRegisterImage(v);
            setFileNameTemplateUI(v);
            setMaxFocusTriesUI(v);

        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private void setFileSavedToastUI(View v) {

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
  
    private void setRegisterImage(View v) {

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
  
    private void setPicFileRootDirUI(View v) {

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
    
    private void setPicFileDirUI(View v) {

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
    
    private void setFileNameTemplateUI(View v) {

        final EditText te = (EditText) v.findViewById(R.id.fileNameTemplate);
        te.setText(augiement.getFileNameTemplate());
        te.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				augiement.setFileNameTemplate(s.toString());
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
        });
    }
    
    private void setMaxFocusTriesUI(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.maxFocusTries);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 1; i <= 3; i++  ) {
            sizes.add(i);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = augiement.getMaxFocusAttempts();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setMaxFocusAttempts(m);
            }
        };
        sui.init();
    }
}
