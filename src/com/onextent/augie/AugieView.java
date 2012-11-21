package com.onextent.augie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.OnTouchListener;

public interface AugieView extends OnTouchListener {

    static final String TAG = Augiement.TAG;

    public int      getWidth();
    
    public int      getHeight();
    
    public Paint    getPaint();

    public Canvas   getCanvas();
    
    public Context  getContext();

    public void     reset();
    
    public void     stop();
    
    public void     resume();
    
    public void     addFeature( Augiement f ) throws AugiementException;
    
    public boolean  removeFeature( Augiement f ) throws AugiementException;
}
