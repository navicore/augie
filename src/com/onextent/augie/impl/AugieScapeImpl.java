/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import java.util.Set;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AugieScapeImpl extends View implements AugieScape {

    Bitmap bitmap;
    Canvas canvas;
    final Paint paint;
    final SharedPreferences prefs;

    Set<Augiement> features;

    public AugieScapeImpl(Context context) {
        super(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context); //todo: stop doing this

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE); 

        float stroke_w = Float.parseFloat(prefs.getString("DRAW_LINE_WIDTH", "18"));
        paint.setStrokeWidth( stroke_w );

        paint.setColor(Color.WHITE);
        paint.setAlpha(Color.WHITE);
        features = new AugiementRegistryImpl(this);
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    public boolean removeFeature(Augiement f) throws AugiementException {
        
        if (f == null) {
            features.clear();
            return true;
        } else return features.remove(f);
    }
    
    public void addFeature(Augiement f) throws AugiementException {
        features.add(f);
    }

    @Override
    public void reset() {
        boolean succ = initBmp(getWidth(), getHeight());
        if (succ) {
            for (Augiement f : features) {
                f.updateCanvas();
            }
            invalidate();
        }
    }

    public void resume() {
        Log.d(TAG, "resuming " + getClass().getName());
        for (Augiement f : features) {
            f.resume();
        }
    }

    public void stop() {
        Log.d(TAG, "stopping " + getClass().getName());
        for (Augiement f : features) {
            f.stop();
        }
    }

    public boolean initBmp(int w, int h) {
        if (w <= 0 || h <= 0) return false;
        Bitmap img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas();
        c.setBitmap(img);
        if (bitmap != null) {
            c.drawBitmap(img, 0, 0, null);
        }
        bitmap = img;
        canvas = c;
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initBmp(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        boolean handled = false;
        try {
            for (Augiement f : features) {
                if (f instanceof OnTouchListener) {
                    boolean b = ((OnTouchListener)f).onTouch(v, event);
                    if (b) handled = true;
                }
            }
            if (handled) invalidate();
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return true;
    }
}