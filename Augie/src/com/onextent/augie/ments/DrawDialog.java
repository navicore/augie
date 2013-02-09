/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.R;

public class DrawDialog extends DialogFragment {

    Draw augiement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (Draw) 
        			activity.getModeManager().getCurrentMode()
        			.getAugiements()
        			.get(Draw.META.getCodeableName());

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.draw_settings, container, false);
        try {

            setEtchaUI(v);

        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private void setEtchaUI(View v) {

		CheckBox cbox = (CheckBox) v.findViewById(R.id.etchaEnabled);
		boolean isEnabled = augiement.isEtchaEnabled();
		cbox.setChecked(isEnabled);

		cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				augiement.setEtchaEnabled(isChecked);
			}
		});
	}
}
