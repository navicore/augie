/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera;

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

public class HistogramDialog extends SherlockDialogFragment {

	private View myview;

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	HistogramFeature augiement;

	ViewGroup container;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.container = container;

		AugieActivity activity = (AugieActivity) getActivity();
		augiement = (HistogramFeature) 
				activity.getModeManager().getCurrentMode()
				.getAugiements()
				.get(HistogramFeature.AUGIE_NAME);

		Dialog d = getDialog();
		if (d != null) d.setTitle(augiement.getMeta().getUIName() + " GPS");
		myview = inflater.inflate(R.layout.histogram_settings, container, false);

		setHistoHeightUI();
		setRGBHUI();

		return myview;
	}
	
    private void setHistoHeightUI() {

        Spinner spinner = (Spinner) myview.findViewById(R.id.histogram_height);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> sizes = new ArrayList<Integer>();
        for (int i = 0; i < 150; i++  ) {
            sizes.add(i + 1);
        }
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, sizes) {
            @Override
            public int calculatePos() {
                int c = augiement.getHHeight();
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).equals(c)) return i;
                }
                return 0;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setHHeight(m);
            }
        };
        sui.init();
    }
    
    private void setRGBHUI() {

        CheckBox cbox = (CheckBox) myview.findViewById(R.id.rgbHistogram);

        boolean isGreyScale = augiement.isGreyscale();
        cbox.setChecked(!isGreyScale);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                augiement.setGreyscale(!isChecked);
            }
        });
    }
}
