package com.onextent.augie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.OnTouchListener;

/**
 * The View layered on top of the RealityScape that holds
 * the Augiements
 */
public interface AugieScape extends OnTouchListener {

    static final String TAG = Augiement.TAG;

    int      getWidth();
    
    int      getHeight();
    
    Paint    getPaint();

    Canvas   getCanvas();
    
    Context  getContext();
    
    void     reset();
    
    void     stop();
    
    void     resume();
    
    void     addFeature( Augiement f ) throws AugiementException;
    
    boolean  removeFeature( Augiement f ) throws AugiementException;
}
