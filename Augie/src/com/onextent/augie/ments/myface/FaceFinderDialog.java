/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.myface;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;

public class FaceFinderDialog extends DialogFragment {

    FaceFinder augiement;
    AugCamera camera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (FaceFinder) 
        			activity.getModeManager().getCurrentMode()
        			.getAugiements()
        			.get(FaceFinder.META.getCodeableName());
        camera = activity.getModeManager().getCurrentMode().getCamera();

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.facefinder_settings, container, false);
        try {

            setMaxFacesUI(v);

        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private void setMaxFacesUI(View v) {

			TextView mf = (TextView) v.findViewById(R.id.maxfFaces);
			int max = camera.getParameters().getMaxNumDetectedFaces();
			AugLog.d("Face Finder supports " + max + " faces");
			mf.setText(Integer.toString(max));
    }
}
