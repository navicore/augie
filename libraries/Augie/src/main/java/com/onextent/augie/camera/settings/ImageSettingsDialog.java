package com.onextent.augie.camera.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.onextent.android.codeable.Size;
import com.onextent.android.ui.SeekBarUI;
import com.onextent.android.ui.SpinnerUI;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.R;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraParameters;
import com.onextent.augie.camera.NamedInt;

public class ImageSettingsDialog extends CamSettingsDialogBase {

    static private final int getPosition(int[] item, List<int[]> list) {

        if (item == null) return 0;

        for (int i = 0; i < list.size(); i++) {
            int[] sel = list.get(i);
            if (
                    (
                            item[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] ==
                            sel[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                            )
                            &&
                            (
                                    item[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] ==
                                    sel[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                                    )
                    ) return i;
        }
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Dialog d = getDialog();
        if (d != null) d.setTitle("Edit Image File Settings");
        View v = inflater.inflate(R.layout.camera_image_settings, container, false);
        try {
            AugieActivity activity = (AugieActivity) getActivity();
            ModeManager modeManager = activity.getModeManager();
            Mode mode = modeManager.getCurrentMode();
            AugCamera camera = mode.getCamera();

            setPictureFmtUI(v, camera);
            setPreviewFmtUI(v, camera);
            setShutterSndUI(v, camera);
            setXPictureFmtUI(v, camera);
            setPictureSizeUI(v, camera);
            setPreviewSizeUI(v, camera);
            setJpegQualityUI(v, camera);
            setJpegThumbnailQualityUI(v, camera);
            setPreviewFPSRangeUI(v, camera);
        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }

        return v;
    }

    private void setShutterSndUI(View v, final AugCamera camera) {
        CheckBox cb = (CheckBox) v.findViewById(R.id.camera_shutter_snd);
        cb.setChecked(camera.getParameters().getShutterSound());
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                camera.getParameters().setShutterSound(isChecked);
            }
        });
    }

    private void setPictureFmtUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_picture_formats);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<NamedInt> list = params.getSupportedPictureFmts();
        SpinnerUI<NamedInt> sui = new SpinnerUI<NamedInt>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getPictureFmt(), list);
            }
            @Override
            public void setMode(NamedInt m) {
                params.setPictureFmt(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setPreviewFmtUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_preview_formats);
        final List<NamedInt> list = params.getSupportedPreviewFmts();
        SpinnerUI<NamedInt> sui = new SpinnerUI<NamedInt>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getPreviewFmt(), list);
            }
            @Override
            public void setMode(NamedInt m) {
                params.setPreviewFmt(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setXPictureFmtUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_xpicture_formats);
        final List<String> list = getChoiceList(params.getXSupportedPictureFmts());
        SpinnerUI<String> sui = new SpinnerUI<String>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getXPictureFmt(), list);
            }
            @Override
            public void setMode(String m) {
                params.setXPictureFmt(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setPictureSizeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_picture_sizes);
        final List<Size> list = params.getSupportedPictureSizes();
        SpinnerUI<Size> sui = new SpinnerUI<Size>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getPictureSize(), list);
            }
            @Override
            public void setMode(Size m) {
                params.setPictureSize(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setPreviewSizeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_preview_sizes);
        final List<Size> list = params.getSupportedPreviewSizes();
        SpinnerUI<Size> sui = new SpinnerUI<Size>(spinner, list) {
            @Override
            public int calculatePos() {
                return getPosition(params.getPreviewSize(), list);
            }
            @Override
            public void setMode(Size m) {
                params.setPreviewSize(m);
                applyChanges(camera);
            }
        };
        sui.init();
    }

    private void setJpegQualityUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        int p = params.getJpegQuality();
        SeekBarUI ui = new SeekBarUI(v, R.id.camera_jpeg_quality_bar,
                R.id.camera_jpeg_quality, p, 100, " percent") {
            @Override
            public void setValue(int p) {
                params.setJpegQuality(p);
                applyChanges(camera);
            }
        };
        ui.init();
    }

    private void setJpegThumbnailQualityUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        int p = params.getJpegThumbnailQuality();
        SeekBarUI ui = new SeekBarUI(v, R.id.camera_jpeg_thumbnail_quality_bar,
                R.id.camera_jpeg_thumbnail_quality, p, 100, " percent") {
            @Override
            public void setValue(int p) {
                params.setJpegThumbnailQuality(p);
                applyChanges(camera);
            }
        };
        ui.init();
    }
    class Range {
        int min;
        int max;
        public String toString() {
            return min + " - " + max;
        }
    }
    private List<Range> getRangeList(List<int[]> l) {
        List<Range> rl = new ArrayList<Range>(); 
        for (int[] arr : l) {
            Range r = new Range();
            r.min = arr[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            r.max = arr[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            rl.add(r);
        }
        return rl;
    }

    private void setPreviewFPSRangeUI(View v, final AugCamera camera) {

        final AugCameraParameters params = camera.getParameters();
        Spinner spinner = (Spinner) v.findViewById(R.id.camera_preview_fps_range);
        if (spinner == null) throw new java.lang.NullPointerException("spnner is null");
        final List<int[]> arr = params.getSupportedPreviewFPSRanges();
        final List<Range> rl = getRangeList(arr);
        SpinnerUI<Range> sui = new SpinnerUI<Range>(spinner, rl) {
            @Override
            public int calculatePos() {
                return getPosition(params.getPreviewFPSRange(), arr);
            }
            @Override
            public void setMode(Range r) {
                params.setPreviewFPSRange(r.min, r.max);
                applyChanges(camera);
            }
        };
        sui.init();
    }
}
