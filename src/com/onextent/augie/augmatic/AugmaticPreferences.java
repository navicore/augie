package com.onextent.augie.augmatic;

import com.onextent.augie.augmatic.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AugmaticPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
