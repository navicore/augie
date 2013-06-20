package com.onextent.android.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class AbstractTwoFingerListener implements OnTouchListener {

    private boolean activeGestureStrted = false;
    
    private final OnTouchListener delegate;
    protected AbstractTwoFingerListener(OnTouchListener delegate) {
        this.delegate = delegate;
    }
    protected AbstractTwoFingerListener() {
        this(null);
    }

    private void reset() {
        activeGestureStrted = false;
    }
    protected abstract void doit();
    private void _doit() {

        doit();
        reset();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        boolean ret = false;
        if (delegate != null) 
            ret = delegate.onTouch(v, event);
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            reset();
            break;
        case MotionEvent.ACTION_UP:
            if (activeGestureStrted) _doit();
            else activeGestureStrted = true;
            break;
        case MotionEvent.ACTION_POINTER_UP:
            if (activeGestureStrted) _doit();
            else activeGestureStrted = true;
            break;
        }
        return ret;
    }
}
