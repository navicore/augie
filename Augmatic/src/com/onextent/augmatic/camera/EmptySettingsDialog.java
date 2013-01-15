package com.onextent.augmatic.camera;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.augmatic.R;

public class EmptySettingsDialog extends SherlockDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Dialog d = getDialog();
        if (d != null) d.setTitle("No Settings Available");
        View v = inflater.inflate(R.layout.empty_settings, container, false);
        return v;
    }
}
