package com.onextent.augmatic;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class AugmaticPreferencesActivity extends SherlockPreferenceActivity {
    
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
