package com.onextent.augmatic;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;

public class AugiementStatusFrag extends SherlockDialogFragment {
    
    private AugiementListHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, 
            ViewGroup container,
            Bundle savedInstanceState) {
       
        BaseAugmaticActivity activity = (BaseAugmaticActivity) getSherlockActivity();
        helper = new AugiementListHelper(activity);
        helper.init();

        Dialog d = getDialog();
        if (d != null) d.setTitle("Enable Augiement");
        View v = inflater.inflate(R.layout.module_status, container, false);
        CheckBox cbox = (CheckBox) v.findViewById(R.id.module_enabled);

        final CodeableName cn = helper.getCnList().get(activity.getCurrentAugiementIdx());

        boolean isEnabled = helper.getModeAugiements().containsKey(cn);
        cbox.setChecked(isEnabled);
        helper.updateButtonText(cbox, cn, isEnabled);
        helper.updateStatusText(v, cn);
        boolean isRequired = helper.updateDepText(v, cn);
        if (isEnabled && isRequired)
            cbox.setEnabled(false);

        cbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ModeManager modeManager = ((AugieActivity) getActivity()).getModeManager();
                AugiementFactory af = modeManager.getAugiementFactory();
                Mode mode = modeManager.getCurrentMode();
                if (isChecked) {
                    Augiement a = af.newInstance(cn);
                    mode.addAugiement(a);
                    helper.refreshModeAugiements(mode);
                    helper.updateButtonText(buttonView, cn, isChecked);
                } else {
                    Augiement a = helper.getModeAugiements().get(cn);
                    mode.removeAugiement(a);
                    helper.refreshModeAugiements(mode);
                }
                try {
                    modeManager.setCurrentMode(mode);
                } catch (AugieException e) {
                    AugLog.e( e.toString(), e);
                } //reset everything with new a
            }
        });

        return v;
    }
}
