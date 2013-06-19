package com.onextent.augmatic;

import com.onextent.android.log.ALog;
import com.onextent.android.log.ALog.ATag;

public class AugAppLog {
    
    protected final static ATag TAG = new ATag("AUGIEAPP");

    public static void i(String msg) {
        ALog.i(TAG, msg);
    }
    
    public static void d(String msg) {
        ALog.d(TAG, msg);
    }
    
    public static void e(String msg) {
        ALog.e(TAG, msg);
    }
    public static void e(String msg, Throwable err) {
        ALog.e(TAG, msg, err);
    }
    public static void e(Throwable err) {
        if (err == null) return;
        ALog.e(TAG, err.toString(), err);
    }
    
    public static void w(String msg) {
        ALog.w(TAG, msg);
    }
    public static void w(String msg, Throwable err) {
        ALog.w(TAG, msg, err);
    }
    public static void w(Throwable err) {
        if (err == null) return;
        ALog.w(TAG, err.toString(), err);
    }
}
