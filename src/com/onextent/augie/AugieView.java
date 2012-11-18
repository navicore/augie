package com.onextent.augie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public interface AugieView {

    static final String TAG = Augiement.TAG;

    public int getWidth();
    
    public int getHeight();
    
    public Paint getPaint();

    public Bitmap getBitmap();

    public Canvas getCanvas();
    
    public Context getContext();

    public void reset();

}