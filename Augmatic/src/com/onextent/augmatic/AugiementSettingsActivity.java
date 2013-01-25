/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */

package com.onextent.augmatic;

import com.onextent.augmatic.R;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;

public class AugiementSettingsActivity extends BaseAugmaticActivity {
    
    private int currentAugiementIdx = 0;

    public AugiementSettingsActivity() {
    }
   
    @Override
    protected Button getMenuButton() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.settings;
    }

    @Override
    protected int getPreviewId() {
        return R.id.camera_settings_preview;
    }
    
    @Override
    protected void configMenuButton() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        FragmentTransaction txn = getSupportFragmentManager().beginTransaction();
        
        ModeSelectionFrag modeList = new ModeSelectionFrag();
        txn.replace(R.id.main_list, modeList);
        
        txn.commit();
    }

    public int getCurrentAugiementIdx() {
        return currentAugiementIdx;
    }

    public void setCurrentAugiementIdx(int currentAugiementIdx) {
        this.currentAugiementIdx = currentAugiementIdx;
    }
}
