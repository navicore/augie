package com.onextent.augie.camera;

import android.graphics.ImageFormat;

public class ImageFmt {

    final int format;
    public ImageFmt(int f) {
        format = f;
    }
    public int toInt() {
        return format;
    }
    @Override
    public String toString() {
        switch (format) {
        case ImageFormat.JPEG:
            return "JPEG";
        case ImageFormat.NV16:
            return "NV16";
        case ImageFormat.NV21:
            return "NV21";
        case ImageFormat.RGB_565:
            return "RGB 565";
        case ImageFormat.YUY2:
            return "YUY2";
        case ImageFormat.YV12:
            return "YV12";
        case ImageFormat.UNKNOWN:
        default:
            return "unknown";
        }
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ImageFmt)) {
            return false;
        }
        ImageFmt that = (ImageFmt) o;
        return toInt() == that.toInt();
    }
}
