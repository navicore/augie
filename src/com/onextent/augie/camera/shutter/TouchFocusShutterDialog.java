/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera.shutter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.NamedInt;

public class TouchFocusShutterDialog extends SherlockDialogFragment {


    //todo: make util class
    final static List<NamedInt> COLOR_LIST;
    static {
        COLOR_LIST = new ArrayList<NamedInt>();
        COLOR_LIST.add(new ColorItem(Color.GREEN, "Green"));
        COLOR_LIST.add(new ColorItem(Color.GRAY, "Gray"));
        COLOR_LIST.add(new ColorItem(Color.RED, "Red"));
        COLOR_LIST.add(new ColorItem(Color.BLACK, "Black"));
        COLOR_LIST.add(new ColorItem(Color.WHITE, "White"));
        COLOR_LIST.add(new ColorItem(Color.BLUE, "Blue"));
        COLOR_LIST.add(new ColorItem(Color.CYAN, "Cyan"));
        COLOR_LIST.add(new ColorItem(Color.MAGENTA, "Magenta"));
        COLOR_LIST.add(new ColorItem(Color.YELLOW, "Yellow"));
    }
    
    TouchShutter augiement;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        AugieActivity activity = (AugieActivity) getActivity();
        augiement = (TouchShutter) 
                activity.getModeManager().getCurrentMode()
                .getAugiements()
                .get(TouchShutter.AUGIE_NAME);

        Dialog d = getDialog();
        if (d != null) d.setTitle("Touch Shutter Settings");
        View v = inflater.inflate(R.layout.shutter_settings, container, false);
        try {
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setFocusAreaColorUI(v, camera);
        } catch (Exception e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }

        return v;
    }

    private void setFocusAreaColorUI(View v, AugCamera camera) {

        Spinner spinner = (Spinner) v.findViewById(R.id.focus_area_camera_colors);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        SpinnerUI<NamedInt> sui = new SpinnerUI<NamedInt>(spinner, COLOR_LIST, camera) {
            @Override
            public int calculatePos() {
                int c = augiement.getFocusAreaColor();
                for (int i = 0; i < COLOR_LIST.size(); i++) {
                    if (COLOR_LIST.get(i).toInt() == c) return i;
                }
                return 0;
            }
            @Override
            public void setMode(NamedInt m) {
                augiement.setFocusAreaColor(m.toInt());
            }
        };
        sui.init();
    }

    static class ColorItem implements NamedInt {

        private final int color;
        private final String name;
        ColorItem(int c, String n) {
            color = c;
            name = n;
        }
        @Override
        public String toString() {
            return name;
        }
        @Override
        public int toInt() {
            return color;
        }
    }
}