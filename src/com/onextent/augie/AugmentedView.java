/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.augmatic.AugmaticActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class AugmentedView extends View implements OnTouchListener {

    static final String TAG = AugmentedViewFeature.TAG;

    Bitmap bitmap;
    Canvas canvas;
    final Paint paint;
    final SharedPreferences prefs;

    List<AugmentedViewFeature> features;

    public AugmentedView(Context context, SharedPreferences p) {
        super(context);
        prefs = p;

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE); 

        float stroke_w = Float.parseFloat(prefs.getString("DRAW_LINE_WIDTH", "18"));
        paint.setStrokeWidth( stroke_w );

        paint.setColor(Color.WHITE);
        paint.setAlpha(Color.WHITE);
        features = new ArrayList<AugmentedViewFeature>();
    }

    public Paint getPaint() {
        return paint;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void addFeature(AugmentedViewFeature f) {
        features.add(f);
    }

    public void reset() {
        boolean succ = initBmp(getWidth(), getHeight());
        if (succ) {
            for (AugmentedViewFeature f : features) {
                f.updateBmp();
            }
            invalidate();
        }
    }

    public void resume() {
        Log.d(TAG, "resuming " + AugmentedView.class.getName());
        for (AugmentedViewFeature f : features) {
            f.resume();
        }
    }

    public void stop() {
        Log.d(TAG, "stopping " + AugmentedView.class.getName());
        for (AugmentedViewFeature f : features) {
            f.stop();
        }
    }

    public boolean initBmp(int w, int h) {
        //int w = getWidth();
        //int h = getHeight();
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

    public boolean onTouch(View v, MotionEvent event) {

        boolean handled = false;
        try {
            for (AugmentedViewFeature f : features) {
                boolean b = f.onTouch(v, event);
                if (b) handled = true;
            }
            if (handled) invalidate();
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        //return handled;
        return true;
    }
}
