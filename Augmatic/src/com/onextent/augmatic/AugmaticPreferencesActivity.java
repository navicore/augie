package com.onextent.augmatic;

import android.os.Bundle;
import android.preference.PreferenceActivity;

//todo: make this a fragment
public class AugmaticPreferencesActivity extends PreferenceActivity {
    
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
