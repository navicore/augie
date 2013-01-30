package com.onextent.augmatic.camera;

import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augmatic.R;

public class ProcessingSettingsDialog extends CamSettingsDialogBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Dialog d = getDialog();
        if (d != null) d.setTitle("Edit Processing Settings");
        View v = inflater.inflate(R.layout.camera_processing_settings, container, false);

        try {

            AugieActivity activity = (AugieActivity) getActivity();
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setColorModeUI(v, camera);
            setWhiteBalUI(v, camera);
            setSceneModeUI(v, camera);
            setAntibandingUI(v, camera);
        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    //
    // begin per-setting code
    //
    private void setColorModeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_color_modes);
        final List<String> list = getChoiceList(params.getSupportedColorModes());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getColorMode(), list);
            }
            @Override
            public void setMode(String m) {
                params.setColorMode(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setWhiteBalUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_whitebal_modes);
        final List<String> list = getChoiceList(params.getSupportedWhiteBalances());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getWhiteBalance(), list);
            }
            @Override
            public void setMode(String m) {
                params.setWhiteBalance(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setSceneModeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_scene_modes);
        final List<String> list = getChoiceList(params.getSupportedSceneModes());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getSceneMode(), list);
            }
            @Override
            public void setMode(String m) {
                params.setSceneMode(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setAntibandingUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_antibanding_values);
        final List<String> list = getChoiceList(params.getSupportedAntibanding());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getAntibanding(), list);
            }
            @Override
            public void setMode(String m) {
                params.setAntibanding(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }
}
