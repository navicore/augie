package com.onextent.augie.ments.cvface;

import java.util.List;

import org.opencv.core.Rect;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsCvFaceFinder extends SimpleCvFaceFinder {

    protected void updateFocusAreas(List<Rect> faces) {
    }
}
