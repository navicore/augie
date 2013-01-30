package com.onextent.augmatic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.augmatic.R;

public class EmptySettingsDialog extends SherlockDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.empty_settings, container, false);
        return v;
    }
}
