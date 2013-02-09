/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;

public class HorizonDialog extends DialogFragment {

    Horizon augiement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (Horizon) 
        			activity.getModeManager().getCurrentMode()
        			.getAugiements()
        			.get(Horizon.AUGIE_NAME);

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.horizon_settings, container, false);
        try {
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setClosePixelDistUI(v, camera);
            setHorizLineWidthUI(v, camera);
            setVertLineWidthUI(v, camera);

        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private void setClosePixelDistUI(View v, AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.closePixelDist);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = (int) augiement.getClosePixelDist();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setClosePixelDist(m);
            }
        };
        sui.init();
    }
    
    private void setVertLineWidthUI(View v, AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.vertLineWidth);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = (int) augiement.getVertLineWidth();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setVertLineWidth(m);
            }
        };
        sui.init();
    }
    
    private void setHorizLineWidthUI(View v, AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.horizLineWidth);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = (int) augiement.getHorizLineWidth();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setHorizLineWidth(m);
            }
        };
        sui.init();
    }
}
