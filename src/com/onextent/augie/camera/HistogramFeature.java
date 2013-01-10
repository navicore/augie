/**
 * based on ViewfinderEE368.java_orig (see original file in distributed inside this apk)
 * licensed as:
 *
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onextent.augie.camera;

import java.util.HashSet;
import java.util.Set;

import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.AugiementName;
import com.onextent.augie.impl.AugDrawBase;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.Size;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HistogramFeature extends AugDrawBase implements AugPreviewCallback {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/HISTOGRAM");
    public static final String UI_NAME = "Histogram";
    
    boolean hasData;
    byte[] yyuvdata;

    double redHistogramSum, greenHistogramSum, blueHistogramSum;

    final int[] mRedHistogram;
    final int[] mGreenHistogram;
    final int[] mBlueHistogram;
    final double[] mBinSquared;

    Paint mPaintBlack;
    Paint mPaintYellow;
    Paint mPaintRed;
    Paint mPaintGreen;
    Paint mPaintBlue;

    private AugDrawFeature augdraw;
    private AugCamera camera;

    final RectF redRects[];
    final RectF greenRects[];
    final RectF blueRects[];

    private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIENAME);
        deps.add(AugDrawFeature.AUGIE_NAME);
    }

    private RectF[] newRects() {

        RectF[] r= new RectF[256];
        for (int i = 0; i < 256; i++) {
            r[i] = new RectF();
        }
        return r;
    }
    public HistogramFeature() {
        hasData = false;
        redRects = newRects();
        greenRects = newRects();
        blueRects = newRects();
        mRedHistogram = new int[256];
        mGreenHistogram = new int[256];
        mBlueHistogram = new int[256];
        mBinSquared = new double[256];
        for (int bin = 0; bin < 256; bin++)
        {
            mBinSquared[bin] = ((double)bin) * bin;
        } // bin
        mPaintBlack = new Paint();
        mPaintBlack.setStyle(Paint.Style.FILL);
        mPaintBlack.setColor(Color.BLACK);
        mPaintBlack.setTextSize(25);

        mPaintYellow = new Paint();
        mPaintYellow.setStyle(Paint.Style.FILL);
        mPaintYellow.setColor(Color.YELLOW);
        mPaintYellow.setTextSize(25);

        mPaintRed = new Paint();
        mPaintRed.setStyle(Paint.Style.FILL);
        mPaintRed.setColor(Color.RED);
        mPaintRed.setTextSize(25);

        mPaintGreen = new Paint();
        mPaintGreen.setStyle(Paint.Style.FILL);
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(25);

        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setTextSize(25);
    }

    @Override
    public Set<CodeableName> getDependencyNames() {
        return deps;
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        
        super.onCreate(av, helpers);

        for (Augiement a : helpers) {
            if (a instanceof AugDrawFeature) {
                augdraw = (AugDrawFeature) a;
            } else if (a instanceof AugCamera) {
                camera = (AugCamera) a;
            }
        }
        if (augdraw == null) throw new AugiementException("draw feature is null");
        if (camera == null) throw new AugiementException("camera is null");

        //camera.setPreviewCallback(this);
        camera.setPreviewCallbackWithBuffer(this);
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {

            switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:

            default:
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }

    static private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    /*
    static private void decodeYUV420SPGrayscale(int[] rgb, byte[] yuv420sp, int width, int height)
    {
        final int frameSize = width * height;

        for (int pix = 0; pix < frameSize; pix++)
        {
            int pixVal = (0xff & ((int) yuv420sp[pix])) - 16;
            if (pixVal < 0) pixVal = 0;
            if (pixVal > 255) pixVal = 255;
            rgb[pix] = 0xff000000 | (pixVal << 16) | (pixVal << 8) | pixVal;
        } // pix
    }
     */

    static private void calculateIntensityHistogram(int[] rgb, int[] histogram, int width, int height, int component)
    {
        for (int bin = 0; bin < 256; bin++)
        {
            histogram[bin] = 0;
        } // bin
        if (component == 0) // red
        {
            for (int pix = 0; pix < width*height; pix += 3)
            {
                int pixVal = (rgb[pix] >> 16) & 0xff;
                histogram[ pixVal ]++;
            } // pix
        }
        else if (component == 1) // green
        {
            for (int pix = 0; pix < width*height; pix += 3)
            {
                int pixVal = (rgb[pix] >> 8) & 0xff;
                histogram[ pixVal ]++;
            } // pix
        }
        else // blue
        {
            for (int pix = 0; pix < width*height; pix += 3)
            {
                int pixVal = rgb[pix] & 0xff;
                histogram[ pixVal ]++;
            } // pix
        }
    }

    @Override
    public void updateCanvas() {

        if (!camera.isOpen()) return;

        if (yyuvdata != null && hasData) {
            
            updateHistograms();

            //add buffer back in
            hasData = false;
            camera.addCallbackBuffer(yyuvdata);
            yyuvdata = null;
        }
        
        updateCanvas(redRects, mPaintRed);
        updateCanvas(greenRects, mPaintGreen);
        updateCanvas(blueRects, mPaintBlue);
    }

    private void updateHistograms() {
        
        Canvas canvas = augview.getCanvas();
        int canvasHeight = canvas.getHeight();
        Size prevSize = camera.getParameters().getPreviewSize();
        int w = prevSize.getWidth();
        int h = prevSize.getHeight();
        int[] rgbdata = new int[w * h]; 

        // Convert from YUV to RGB
        decodeYUV420SP(rgbdata, yyuvdata, w, h);
        //decodeYUV420SPGrayscale(rgbdata, yyuvdata, w, h);

        calculateIntensityHistogram(rgbdata, mRedHistogram, w, h, 0);
        calculateIntensityHistogram(rgbdata, mGreenHistogram, w, h, 1);
        calculateIntensityHistogram(rgbdata, mBlueHistogram, w, h, 2);

        redHistogramSum = 0; greenHistogramSum = 0; blueHistogramSum = 0;
        for (int bin = 0; bin < 256; bin++)
        {
            redHistogramSum += mRedHistogram[bin];
            greenHistogramSum += mGreenHistogram[bin];
            blueHistogramSum += mBlueHistogram[bin];
        }

        updateHistogramRects(canvasHeight - 200, redHistogramSum, mRedHistogram, redRects);
        updateHistogramRects(canvasHeight - 100, greenHistogramSum, mGreenHistogram, greenRects);
        updateHistogramRects(canvasHeight, blueHistogramSum, mBlueHistogram, blueRects);
    }

    private void updateCanvas(RectF[] rects, Paint paint) {
        Canvas canvas = augview.getCanvas();
        for (RectF r : rects) {
            canvas.drawRect(r, paint);
        }
    }

    private void updateHistogramRects(int bottom, double sum, int[] histogram, RectF[] rects) {

        Canvas canvas = augview.getCanvas();
        int canvasWidth = canvas.getWidth();
        float barMaxHeight = 3000;
        float barWidth = ((float)canvasWidth) / 256;
        float barMarginHeight = 2;

        float top;
        float left = 0;
        float right = left + barWidth;

        for (int bin = 0; bin < 256; bin++) {

            RectF barRect = rects[bin];
            float prob = (float)histogram[bin] / (float)sum;
            top = bottom - 
                    Math.min(80, prob * barMaxHeight) - barMarginHeight;
            top += barMarginHeight;
            barRect.left = left;
            barRect.top = top;
            barRect.bottom = bottom;
            barRect.right = right;
            left += barWidth;
            right += barWidth;
        }
    }

    @Override
    public void clear() {
    }

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() {

        return null;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null || data.length == 0) return;
        yyuvdata = data;
        hasData = true;
        //todo: make setting to reset for every frame (perf hit) or only
        // when some other augiement was a re-paint.
        //augview.reset();
    }
    @Override
    public String getUIName() {
        
        return UI_NAME;
    }
    
    public static final AugiementFactory.Meta getMeta() {
        return new AugiementFactory.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return PinchZoom.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }
        };
    }
}
