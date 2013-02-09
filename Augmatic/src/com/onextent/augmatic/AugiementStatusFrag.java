package com.onextent.augmatic;

import java.util.List;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;

public class AugiementStatusFrag extends DialogFragment {
    
    private AugiementListHelper helper;
    private DialogFragment augiementUI;
    private View statusView;
    
    private final OnClickListener showDetailsListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
           
            if (augiementUI != null) {
                augiementUI.show(getActivity().getFragmentManager(), "Module Status");
            }
        }
    };
    
    private void configEditButton() {
        
        if (helper.isDualPane() || augiementUI == null) {
            View bl = (View) statusView.findViewById(R.id.editAugiementDetailsBtnHolder);
            bl.setVisibility(View.GONE);
        } else {
            Button b = (Button) statusView.findViewById(R.id.editAugiementDetailsBtn);
            b.setOnClickListener(showDetailsListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, 
            ViewGroup container,
            Bundle savedInstanceState) {
       
        ControlActivity activity = (ControlActivity) getActivity();
        helper = activity.getHelper();

        Dialog d = getDialog();
        if (d != null) d.setTitle("Enable Augiement");
        statusView = inflater.inflate(R.layout.module_status, container, false);
        CheckBox cbox = (CheckBox) statusView.findViewById(R.id.module_enabled);

        int idx = activity.getCurrentAugiementIdx();
        List<CodeableName> cnlist = helper.getCnList();
        final CodeableName cn = cnlist.get(idx);

        boolean isEnabled = helper.getModeAugiements().containsKey(cn);
        cbox.setChecked(isEnabled);
        if (isEnabled) {
            Augiement augiement = helper.getModeAugiements().get(cn);
            augiementUI = augiement.getUI();
        }
        configEditButton();
        
        helper.updateButtonText(cbox, cn, isEnabled);
        helper.updateStatusText(statusView, cn);
        boolean isRequired = helper.updateDepText(statusView, cn);
        if (isEnabled && isRequired)
            cbox.setEnabled(false);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                AugieActivity aa = (AugieActivity) getActivity();
                ModeManager modeManager = aa.getModeManager();
                AugiementFactory af = aa.getAugiementFactory();
                Mode mode = modeManager.getCurrentMode();
                if (isChecked) {
                    Augiement a = af.newInstance(cn);
                    mode.addAugiement(a);
                    helper.refreshModeAugiements(mode);
                    helper.updateButtonText(buttonView, cn, isChecked);
                    augiementUI = a.getUI();
                } else {
                    Augiement a = helper.getModeAugiements().get(cn);
                    mode.removeAugiement(a);
                    helper.refreshModeAugiements(mode);
                    augiementUI = null;
                }
                configEditButton();
                try {
                    modeManager.setCurrentMode(mode);
                } catch (AugieException e) {
                    AugAppLog.e( e.toString(), e);
                } //reset everything with new a
            }
        });

        return statusView;
    }
}
