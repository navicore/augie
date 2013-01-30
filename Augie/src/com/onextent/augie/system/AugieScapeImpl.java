/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.system;

import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;

public class AugieScapeImpl extends View implements AugieScape {

    Bitmap bitmap;
    Canvas canvas;
    final Paint paint;

    Set<Augiement> features;

    public AugieScapeImpl(Context context) {
        super(context);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE); 

        paint.setStrokeWidth( 18 ); //todo: setting

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
        AugLog.d( "resuming " + getClass().getName());
        for (Augiement f : features) {
            f.resume();
        }
        initBmp(getWidth(), getHeight());
    }

    public void stop() {
        AugLog.d( "stopping " + getClass().getName());
        for (Augiement f : features) {
            f.stop();
        }
        if (bitmap != null)  {
        	bitmap.recycle();
        	bitmap = null;
        }
    }

    public boolean initBmp(int w, int h) {
    	
        if (w <= 0 || h <= 0) return false;
        
        if (bitmap == null) {
        	canvas = new Canvas();
        	bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        	canvas.setBitmap(bitmap);
        } else {
        	bitmap.eraseColor(Color.TRANSPARENT);
        }
        
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
            	//todo: make an ordered list of these at startup
                if (f instanceof OnTouchListener) {
                    boolean b = ((OnTouchListener)f).onTouch(v, event);
                    if (b) handled = true;
                }
            }
            if (handled) invalidate();
        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }
        //return handled;
        return true;
    }

	@Override
	public boolean onLongClick(View v) {
		
		boolean handled = false;
        try {
            for (Augiement f : features) {
            	//todo: make an ordered list of these at startup
                if (f instanceof OnLongClickListener) {
                    boolean b = ((OnLongClickListener)f).onLongClick(v);
                    if (b) handled = true;
                }
            }
            if (handled) invalidate(); //probably unnecessary
        } catch (Exception e) {
            AugLog.e( e.toString(), e);
        }
        //return handled;
        return true;
	}
}
