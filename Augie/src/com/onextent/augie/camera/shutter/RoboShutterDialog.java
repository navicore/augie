/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.R;

public class RoboShutterDialog extends SherlockDialogFragment {

    RoboShutter augiement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (RoboShutter) 
        			activity.getModeManager().getCurrentMode()
        			.getAugiements()
        			.get(RoboShutter.AUGIE_NAME);

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.robo_shutter_settings, container, false);
        setBlackoutUI(v);
        setInitIntervalUI(v);
        setIntervalUI(v);
        setDurationUI(v);

        return v;
    }

    private void setBlackoutUI(View v) {

        CheckBox cbox = (CheckBox) v.findViewById(R.id.robo_blackout);

        boolean isEnabled = augiement.isBlackout();
        cbox.setChecked(isEnabled);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                augiement.setBlackout(isChecked);
            }
        });
    }
    
    private void setDurationUI(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.robo_duration);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 120; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = augiement.getDuration();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setDuration(m);
            }
        };
        sui.init();
    }
    
    private void setIntervalUI(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.robo_interval);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 20; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = augiement.getInterval();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setInterval(m);
            }
        };
        sui.init();
    }
    
    private void setInitIntervalUI(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.robo_init_interval);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 20; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = augiement.getInitInterval();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setInitInterval(m);
            }
        };
        sui.init();
    }
}
