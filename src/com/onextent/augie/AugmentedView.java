/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.ArrayList;
import java.util.List;

import com.onextent.augie.testcamera.TestCameraActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class AugmentedView extends View implements OnTouchListener {
	
	static final String TAG = TestCameraActivity.TAG;
	
	Bitmap bitmap;
	Canvas canvas;
	final Paint paint;

	List<AugmentedViewFeature> features;
	
	public AugmentedView(Context context) {
	    super(context);
	    paint = new Paint();
	    paint.setStyle(Paint.Style.STROKE); 
	    paint.setStrokeWidth(18);
	    paint.setColor(Color.WHITE);
	    //paint.setColor(Color.BLACK);
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
	    initBmp();
	    for (AugmentedViewFeature f : features) {
	    	f.redraw();
	    }
	    invalidate();
    }

    public void resume() {
	    for (AugmentedViewFeature f : features) {
	    	f.resume();
	    }
    }

    public void stop() {
	    for (AugmentedViewFeature f : features) {
	    	f.stop();
	    }
    }

    public void initBmp() {
	    Bitmap img = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
	    Canvas c = new Canvas();
	    c.setBitmap(img);
	    if (bitmap != null) {
	    	c.drawBitmap(img, 0, 0, null);
	    }
	    bitmap = img;
	    canvas = c;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	initBmp();
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
			switch(event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				break;
			}
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
