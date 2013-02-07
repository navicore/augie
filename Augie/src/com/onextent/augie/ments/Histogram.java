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
package com.onextent.augie.ments;

import java.util.HashSet;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.android.codeable.Size;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugPreviewCallback;

public class Histogram implements AugPreviewCallback, Augiement {
   
    @Override
	public void stop() {
		camera.removePreviewCallback(this);
	}
	@Override
	public void resume() {
		camera.addPreviewCallback(this);
	}

	public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/HISTOGRAM");
    public static final String UI_NAME = "Histogram";
    public static final String DESCRIPTION = "A live RGB Histogram drawn on the camera preview.";

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
    Paint mPaintWhite;

    private Draw augdraw;
    private AugCamera camera;

    final RectF greyRects[];
    final RectF redRects[];
    final RectF greenRects[];
    final RectF blueRects[];
	private boolean greyscale;
    private int hheight = 0;

	private final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(AugCamera.AUGIE_NAME);
        deps.add(Draw.AUGIE_NAME);
    }

    private RectF[] newRects() {

        RectF[] r= new RectF[256];
        for (int i = 0; i < 256; i++) {
            r[i] = new RectF();
        }
        return r;
    }
    public Histogram() {
        hasData = false;
        greyRects = newRects();
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
        
        mPaintWhite = new Paint();
        mPaintWhite.setStyle(Paint.Style.FILL);
        mPaintWhite.setColor(Color.WHITE);
        mPaintWhite.setTextSize(25);
    }

    private AugieScape augieScape;
    
    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        
        augieScape = av;

        for (Augiement a : helpers) {
            if (a instanceof Draw) {
                augdraw = (Draw) a;
            } else if (a instanceof AugCamera) {
                camera = (AugCamera) a;
            }
        }
        if (augdraw == null) throw new AugiementException("draw feature is null");
        if (camera == null) throw new AugiementException("camera is null");
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

    static private void decodeYUV420SPGrayscale(int[] rgb, byte[] yuv420sp, 
    		int width, int height) {
    	
        final int frameSize = width * height;

        for (int pix = 0; pix < frameSize; pix++)
        {
            int pixVal = (0xff & ((int) yuv420sp[pix])) - 16;
            if (pixVal < 0) pixVal = 0;
            if (pixVal > 255) pixVal = 255;
            rgb[pix] = 0xff000000 | (pixVal << 16) | (pixVal << 8) | pixVal;
        } // pix
    }

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
            yyuvdata = null;
        }

        if (greyscale) {
        	updateCanvas(greyRects, mPaintWhite);
        } else {
        	updateCanvas(redRects, mPaintRed);
        	updateCanvas(greenRects, mPaintGreen);
        	updateCanvas(blueRects, mPaintBlue);
        }
    }

    private void updateHistograms() {

        Canvas canvas = augieScape.getCanvas();
        int canvasHeight = canvas.getHeight();
        Size prevSize = camera.getParameters().getPreviewSize();
        int w = prevSize.getWidth();
        int h = prevSize.getHeight();
        int[] rgbdata = new int[w * h]; 

        // Convert from YUV to RGB
        if (greyscale)
        	decodeYUV420SPGrayscale(rgbdata, yyuvdata, w, h);
        else
        	decodeYUV420SP(rgbdata, yyuvdata, w, h);

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

        if (greyscale) {
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	//todo: ejs warning this greyscale stuff is a bad bad hack.  broken.
        	updateHistogramRects(canvasHeight, redHistogramSum, mRedHistogram, greyRects);
        } else {
        	updateHistogramRects(canvasHeight - (2 * getHHeight()), redHistogramSum, mRedHistogram, redRects);
        	updateHistogramRects(canvasHeight - (getHHeight()), greenHistogramSum, mGreenHistogram, greenRects);
        	updateHistogramRects(canvasHeight, blueHistogramSum, mBlueHistogram, blueRects);
        }
    }
    
    private void updateCanvas(RectF[] rects, Paint paint) {
        Canvas canvas = augieScape.getCanvas();
        for (RectF r : rects) {
            canvas.drawRect(r, paint);
        }
    }

    private void updateHistogramRects(int bottom, double sum, int[] histogram, RectF[] rects) {

        Canvas canvas = augieScape.getCanvas();
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
    public Code getCode() throws CodeableException {

        Code code = JSONCoder.newCode();
        code.put(AUGIE_NAME);
        code.put("greyscale", greyscale);
        code.put("hheight", hheight);
        
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
    	if (code == null) return;
    	if (code.has("hheight")) {
    		hheight = code.getInt("hheight");
    	}
    	if (code.has("greyscale")) {
    		greyscale = code.getBoolean("greyscale");
    	}
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
    public Meta getMeta() {
        return META;
    }

    public static final Meta META =
            new Augiement.Meta() {

        @Override
        public Class<? extends Augiement> getAugiementClass() {

            return Histogram.class;
        }

        @Override
        public CodeableName getCodeableName() {

            return AUGIE_NAME;
        }

        @Override
        public String getUIName() {

            return UI_NAME;
        }

            @Override
            public int getMinSdkVer() {
                return 0;
            }
        @Override
        public String getDescription() {

            return DESCRIPTION;
        }

        @Override
        public Set<CodeableName> getDependencyNames() {

            return deps;
        }
    };

    @Override
    public DialogFragment getUI() {
        return new HistogramDialog();
    }
    
    public boolean isGreyscale() {
		return greyscale;
	}
	public void setGreyscale(boolean greyscale) {
		this.greyscale = greyscale;
	}
	
    public void setHHeight(int h) {
    	hheight = h;
    }
    
    public int getHHeight() {
    	if (hheight != 0) return hheight;
        if (augieScape != null && augieScape.getHeight() > 600) return 100;
        return 50;
    }
}
