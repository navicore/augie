package com.onextent.augie.augmatic;

import com.onextent.augie.augmatic.R;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.os.Bundle;

public class AugmaticPreferences extends SherlockPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
