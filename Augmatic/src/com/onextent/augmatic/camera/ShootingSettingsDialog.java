package com.onextent.augmatic.camera;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.onextent.android.ui.SeekBarUI;
import com.onextent.android.ui.SpinnerUI;
import com.onextent.augmatic.R;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.android.codeable.Codeable;

public class ShootingSettingsDialog extends SherlockDialogFragment {

    static private List<String> getChoiceList(List<String> list) {
        if (list == null) return null;
        List<String> r = new ArrayList<String>();
        r.add("<unset>");
        r.addAll(list);
        return r;
    }

    static private final int getPosition(Object item, List<?> list) {

        if (item == null) return 0;

        for (int i = 0; i < list.size(); i++) {
            if (item.equals(list.get(i))) return i;
        }
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Dialog d = getDialog();
        if (d != null) d.setTitle("Edit Shooting Settings");
        View v = inflater.inflate(R.layout.camera_shooting_settings, container, false);
        try {
            AugieActivity activity = (AugieActivity) getActivity();
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setFlashModeUI(v, camera);
            setFocusModeUI(v, camera);
            setExposureCompUI(v, camera);
            setZoomUI(v, camera);
            setISOUI(v, camera);
        } catch (Exception e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }

        return v;
    }

    private void setFocusModeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_focus_modes);
        final List<String> list = getChoiceList(params.getSupportedFocusModes());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getFocusMode(), list);
            }
            @Override
            public void setMode(String m) {
                params.setFocusMode(m);
                try {
                    camera.applyParameters();
                } catch (AugCameraException e) {
                    Log.e(Codeable.TAG, e.toString());
                }
            }
        };
        sui.init();
    }

    private void setFlashModeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();

        Spinner spinner = (Spinner) v.findViewById(R.id.camera_flash_modes);
        final List<String> list = getChoiceList(params.getSupportedFlashModes());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getFlashMode(), list);
            }
            @Override
            public void setMode(String m) {
                params.setFlashMode(m);
                try {
                    camera.applyParameters();
                } catch (AugCameraException e) {
                    Log.e(Codeable.TAG, e.toString());
                }
            }
        };
        sui.init();
    }

    private void setExposureCompUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        final float step = params.getExposureCompensationStep();
        final int min = params.getMinExposureCompensation();
        final int max = params.getMaxExposureCompensation();
        int p = params.getExposureCompensation();
        SeekBarUI ui = new SeekBarUI(v, R.id.camera_exposure_comp_bar,
                R.id.camera_exposure_comp, p + max, max * 2, " ev") {
            @Override
            public String calcProgressValue(int p) {
                int ec = p - max;
                float ev = step * ec;
                ev = (float)Math.round(ev * 100) / 100;
                return Float.toString(ev);
            }

            @Override
            public void setValue(int p) {
                int ec = p - max;
                if (ec < min) ec = min;
                if (ec > max) ec = max;
                params.setExposureCompensation(ec);
                try {
                    camera.applyParameters();
                } catch (AugCameraException e) {
                    Log.e(Codeable.TAG, e.toString());
                }
            }
        };
        ui.track(true);
        ui.init();
    }

    private void setZoomUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();

        final int max = params.getMaxZoom();
        final int zoom = params.getZoom();
        SeekBarUI ui = new SeekBarUI(v, R.id.camera_zoom_bar,
                R.id.camera_zoom, zoom, max, " zoom") {

            @Override
            public String calcProgressValue(int p) {
                return Integer.toString(p);
            }

            @Override
            public void setValue(int p) {
                params.setZoom(p);
                try {
                    camera.applyParameters();
                } catch (AugCameraException e) {
                    Log.e(Codeable.TAG, e.toString());
                }
            }
        };
        ui.track(true);
        ui.init();
        if (!params.isZoomSupported()) {
            ui.enable(false, "camera does not zoom");
        }
    }
    
    private void setISOUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_iso);
        final List<String> list = getChoiceList(params.getXSupportedISOs());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getXISO(), list);
            }
            @Override
            public void setMode(String m) {
                params.setXISO(m);
                try {
                    camera.applyParameters();
                } catch (AugCameraException e) {
                    Log.e(Codeable.TAG, e.toString());
                }
            }
        };
        sui.init();
    }

}
