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
import com.onextent.augie.AugieableException;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.impl.AugDrawBase;
import com.onextent.augie.impl.AugDrawFeature;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.Size;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HistogramFeature extends AugDrawBase implements AugPreviewCallback {

    //todo: fix performance hit!

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/HISTOGRAM");

    double redHistogramSum, greenHistogramSum, blueHistogramSum;

    int[] mRedHistogram;
    int[] mGreenHistogram;
    int[] mBlueHistogram;
    double[] mBinSquared;

    Paint mPaintBlack;
    Paint mPaintYellow;
    Paint mPaintRed;
    Paint mPaintGreen;
    Paint mPaintBlue;

    byte[] yyuvdata;
    private AugDrawFeature augdraw;
    private AugCamera camera;

    private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIENAME);
        deps.add(AugDrawFeature.AUGIE_NAME);
    }

    public HistogramFeature() {
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

        camera.setPreviewCallback(this);
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

    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
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

    static public void decodeYUV420SPGrayscale(int[] rgb, byte[] yuv420sp, int width, int height)
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

    static public void calculateIntensityHistogram(int[] rgb, int[] histogram, int width, int height, int component)
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
        
        Canvas canvas = augview.getCanvas();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int newImageWidth = canvasWidth;
        //int newImageHeight = canvasHeight;
        int marginWidth = (canvasWidth - newImageWidth)/2;

        Size prevSize = camera.getParameters().getPreviewSize();
        int[] rgbdata = new int[prevSize.getWidth() * prevSize.getHeight()]; 

        if (yyuvdata != null) {

            // Convert from YUV to RGB
            decodeYUV420SP(rgbdata, yyuvdata, prevSize.getWidth(), prevSize.getHeight());

            // Calculate histogram
            calculateIntensityHistogram(rgbdata, mRedHistogram, 
                    prevSize.getWidth(), prevSize.getHeight(), 0);
            calculateIntensityHistogram(rgbdata, mGreenHistogram, 
                    prevSize.getWidth(), prevSize.getHeight(), 1);
            calculateIntensityHistogram(rgbdata, mBlueHistogram, 
                    prevSize.getWidth(), prevSize.getHeight(), 2);

            // Calculate mean
            redHistogramSum = 0; greenHistogramSum = 0; blueHistogramSum = 0;
            for (int bin = 0; bin < 256; bin++)
            {
                redHistogramSum += mRedHistogram[bin];
                greenHistogramSum += mGreenHistogram[bin];
                blueHistogramSum += mBlueHistogram[bin];
            } // bin
        }

        // Draw red intensity histogram
        float barMaxHeight = 3000;
        float barWidth = ((float)newImageWidth) / 256;
        float barMarginHeight = 2;
        RectF barRect = new RectF();
        barRect.bottom = canvasHeight - 200;
        barRect.left = marginWidth;
        barRect.right = barRect.left + barWidth;
        for (int bin = 0; bin < 256; bin++)
        {
            float prob = (float)mRedHistogram[bin] / (float)redHistogramSum;
            barRect.top = barRect.bottom - 
                    Math.min(80,prob*barMaxHeight) - barMarginHeight;
            canvas.drawRect(barRect, mPaintBlack);
            barRect.top += barMarginHeight;
            canvas.drawRect(barRect, mPaintRed);
            barRect.left += barWidth;
            barRect.right += barWidth;
        } // bin

        // Draw green intensity histogram
        barRect.bottom = canvasHeight - 100;
        barRect.left = marginWidth;
        barRect.right = barRect.left + barWidth;
        for (int bin = 0; bin < 256; bin++)
        {
            barRect.top = barRect.bottom - 
                    Math.min(80, ((float)mGreenHistogram[bin])/((float)greenHistogramSum) * 
                            barMaxHeight) - barMarginHeight;
            canvas.drawRect(barRect, mPaintBlack);
            barRect.top += barMarginHeight;
            canvas.drawRect(barRect, mPaintGreen);
            barRect.left += barWidth;
            barRect.right += barWidth;
        } // bin

        // Draw blue intensity histogram
        barRect.bottom = canvasHeight;
        barRect.left = marginWidth;
        barRect.right = barRect.left + barWidth;
        for (int bin = 0; bin < 256; bin++)
        {
            barRect.top = barRect.bottom - 
                    Math.min(80, ((float)mBlueHistogram[bin])/((float)blueHistogramSum) * 
                            barMaxHeight) - barMarginHeight;
            canvas.drawRect(barRect, mPaintBlack);
            barRect.top += barMarginHeight;
            canvas.drawRect(barRect, mPaintBlue);
            barRect.left += barWidth;
            barRect.right += barWidth;
        }
        yyuvdata = null;
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
    public void edit(Context context, EditCallback cb) throws AugieableException {

    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public Meta getMeta() {
        return null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null || data.length == 0) return;
        yyuvdata = new byte[data.length]; 

        System.arraycopy(data, 0, yyuvdata, 0, data.length);
        augview.reset();
    }
}
