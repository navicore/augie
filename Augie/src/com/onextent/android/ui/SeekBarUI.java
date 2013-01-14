/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.android.ui;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.onextent.android.codeable.Codeable;

public abstract class SeekBarUI {
    private final String progressSuffix;
    private final SeekBar seekBar;
    private final TextView textView;
    private int progress;
    boolean track = false;
    
    public SeekBarUI(
            View v, 
            int seekBarId, 
            int textViewId, 
            int progress, 
            int max, 
            String progressSuffix) {
        
        this.progress = progress;
        this.progressSuffix = progressSuffix;
        seekBar = (SeekBar) v.findViewById(seekBarId);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        textView = (TextView) v.findViewById(textViewId);
        textView.setText(calcProgressValue(progress) + progressSuffix);
    }
    
    public void track(boolean b) {
        track = b;
    }
    
    public void enable(boolean b, String msg) {
        textView.setText(msg);
        seekBar.setEnabled(b);
    }
    
    public void init() {

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
               
                    textView.setText(calcProgressValue(progress) + progressSuffix);
                    if (track)
                        onStopTrackingTouch(seekBar);
                } catch (Throwable err) {
                    Log.e(Codeable.TAG, err.toString(), err);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int p = seekBar.getProgress();
                if (p != progress) {
                    setValue(p);
                    progress = p;
                }
            }
        });
    }
    //override this if not a 0-to-n scale
    public String calcProgressValue(int p) {
        return Integer.toString(p);
    }
    public abstract void setValue(int p);
}
