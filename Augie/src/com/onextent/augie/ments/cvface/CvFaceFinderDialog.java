/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.cvface;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.ments.cvface.CvFaceFinder.DETECTION_TYPE;

public class CvFaceFinderDialog extends SherlockDialogFragment {

    CvFaceFinder augiement;
    AugCamera camera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (CvFaceFinder) 
                activity.getModeManager().getCurrentMode()
                .getAugiements()
                .get(CvFaceFinder.META.getCodeableName());
        camera = activity.getModeManager().getCurrentMode().getCamera();

        Dialog d = getDialog();
        if (d != null) d.setTitle(augiement.getMeta().getUIName() + " Settings");
        View v = inflater.inflate(R.layout.cvfacefinder_settings, container, false);
        try {

            setDetectionTypeUI(v);
            setFaceSizeUI(v);

        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private static final String JAVA_DTYPE = "Cascade Classifier";
    private static final String NATIVE_DTYPE = "Detection Based Tracker";
    
    private void setDetectionTypeUI(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.cv_myface_detection_type);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<String> tList = new ArrayList<String>();
        
        tList.add(JAVA_DTYPE);
        tList.add(NATIVE_DTYPE);
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, tList) {
            @Override
            public int calculatePos() {
                DETECTION_TYPE ct = augiement.getDtype();
                if (ct == DETECTION_TYPE.JAVA)  return 0;
                return 1;
            }
            @Override
            public void setMode(String m) {
                if (m != null && m.equals(JAVA_DTYPE)) augiement.setDtype(DETECTION_TYPE.JAVA);
                else augiement.setDtype(DETECTION_TYPE.NATIVE);
            }
        };
        sui.init();
    }
    
    private void setFaceSizeUI(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.cv_myface_size_pct);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<Integer> tList = new ArrayList<Integer>();
        
        tList.add(50);
        tList.add(40);
        tList.add(30);
        tList.add(20);
        SpinnerUI<Integer> sui = new SpinnerUI<Integer>(spinner, tList) {
            @Override
            public int calculatePos() {
                int sz = augiement.getFaceSizePct();
                if (sz == 50)  return 0;
                if (sz == 40)  return 1;
                if (sz == 30)  return 2;
                if (sz == 20)  return 3;
                return 1;
            }
            @Override
            public void setMode(Integer m) {
                augiement.setFaceSizePct(m);
            }
        };
        sui.init();
    }
}
