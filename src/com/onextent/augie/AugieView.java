package com.onextent.augie;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public interface AugieView {

    static final String TAG = Augiement.TAG;

    public int getWidth();
    
    public int getHeight();
    
    public abstract Paint getPaint();

    public abstract Bitmap getBitmap();

    public abstract Canvas getCanvas();

    public abstract void reset();

}