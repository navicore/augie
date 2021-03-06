/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.shutter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.onextent.android.ui.SpinnerUI;
import com.onextent.android.ui.UiUtil;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.NamedInt;

public class TouchFocusShutterDialog extends DialogFragment {

    TouchFocusShutter augiement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (TouchFocusShutter) 
        			activity.getModeManager().getCurrentMode()
        			.getAugiements()
        			.get(TouchFocusShutter.AUGIE_NAME);

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.touch_shutter_settings, container, false);
        try {
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setFocusAreaColorUI(v, camera);
            setMeterAreaColorUI(v, camera);
            setAlwaysSetFaUI(v, camera);
            setDefaultFocusSzUI(v, camera);

        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private void setMeterAreaColorUI(View v, AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.meter_area_camera_colors);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        SpinnerUI<NamedInt> sui = new SpinnerUI<NamedInt>(spinner, UiUtil.COLOR_LIST) {
            @Override
            public int calculatePos() {
                int c = augiement.getMeterAreaColor();
                for (int i = 0; i < UiUtil.COLOR_LIST.size(); i++) {
                    if (UiUtil.COLOR_LIST.get(i).toInt() == c) return i;
                }
                return 0;
            }
            @Override
            public void setMode(NamedInt m) {
                augiement.setMeterAreaColor(m.toInt());
            }
        };
        sui.init();
    }

    private void setFocusAreaColorUI(View v, final AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.focus_area_camera_colors);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        SpinnerUI<NamedInt> sui = new SpinnerUI<NamedInt>(spinner, UiUtil.COLOR_LIST) {
            @Override
            public int calculatePos() {
                int c = augiement.getFocusAreaColor();
                for (int i = 0; i < UiUtil.COLOR_LIST.size(); i++) {
                    if (UiUtil.COLOR_LIST.get(i).toInt() == c) return i;
                }
                return 0;
            }
            @Override
            public void setMode(NamedInt m) {
                augiement.setFocusAreaColor(m.toInt());
            }
        };
        sui.init();
    }

    private void setAlwaysSetFaUI(View v, AugCamera camera) {

        CheckBox cbox = (CheckBox) v.findViewById(R.id.alwaysSetFocusArea);

        boolean isEnabled = augiement.isAlways_set_focus_area();
        cbox.setChecked(isEnabled);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                augiement.setAlways_set_focus_area(isChecked);
            }
        });
    }
  
    private void setDefaultFocusSzUI(View v, AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.defaultFocusAreaSz);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 50; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = augiement.getTouchFocusSz();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setTouchFocusSz(m);
            }
        };
        sui.init();
    }
}
