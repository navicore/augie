/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.impl;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableHandler;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.R;

public class GPSDialog extends SherlockDialogFragment {

	private View myview;

	@Override
	public void onPause() {
		super.onPause();
		AugieActivity activity = (AugieActivity) getActivity();
		activity.unlisten(GPS.GPS_UPDATE_AUGIE_NAME, gpsEventHandler);
	}

	@Override
	public void onResume() {
		super.onResume();
		AugieActivity activity = (AugieActivity) getActivity();
		activity.listen(GPS.GPS_UPDATE_AUGIE_NAME, gpsEventHandler);
	}

	GPS augiement;

	ViewGroup container;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.container = container;

		AugieActivity activity = (AugieActivity) getActivity();
		augiement = (GPS) 
				activity.getModeManager().getCurrentMode()
				.getAugiements()
				.get(GPS.AUGIE_NAME);

		Dialog d = getDialog();
		if (d != null) d.setTitle(augiement.getMeta().getUIName() + " GPS");
		myview = inflater.inflate(R.layout.gps_settings, container, false);
		try {

			setEnabledUI();
			
			TextView lat = (TextView) myview.findViewById(R.id.latitude);
			TextView longitude = (TextView) myview.findViewById(R.id.longitude);
			
			lat.setText(Double.toString(augiement.getLatitude()));
			longitude.setText(Double.toString(augiement.getLongitude()));

		} catch (Exception e) {
			AugLog.e( e.toString(), e);
		}

		return myview;
	}

	private CodeableHandler gpsEventHandler = new CodeableHandler() {

		@Override
		public void onCode(Code code) {
			AugLog.d( "ejs got dialog gps: " + code);
			TextView lat = (TextView) myview.findViewById(R.id.latitude);
			TextView longitude = (TextView) myview.findViewById(R.id.longitude);
			try {
				lat.setText(code.getString(GPS.LATITUDE_KEY));
				longitude.setText(code.getString(GPS.LONGITUDE_KEY));
			} catch (CodeableException e) {
				AugLog.e( e.toString(), e);
			}
		}
	};


	private void setEnabledUI() {

		CheckBox cbox = (CheckBox) myview.findViewById(R.id.gpsEnabled);
		boolean isEnabled = augiement.isEnabled();
		cbox.setChecked(isEnabled);
		cbox.setEnabled(false); //read only
		if (isEnabled)
			cbox.setText("GPS enabled via " + augiement.getProvider());
		else
			cbox.setText("You must enable GPS to use this Augiement.");

		cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//noop
			}
		});
	}
}
