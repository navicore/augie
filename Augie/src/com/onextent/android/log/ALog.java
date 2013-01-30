package com.onextent.android.log;

import android.util.Log;

import com.onextent.android.codeable.CodeableName;

public class ALog {
    
    public static class ATag extends CodeableName {

        public ATag(String name) {
            super(name);
        }
    }
    
    public static void i(ATag tag, String msg) {
        if (tag == null || msg == null) return;
        Log.i(tag.toString(), msg);
    }
    
    public static void d(ATag tag, String msg) {
        if (tag == null || msg == null) return;
        Log.d(tag.toString(), msg);
    }
    
    public static void e(ATag tag, String msg) {
        if (tag == null || msg == null) return;
        Log.e(tag.toString(), msg);
    }
    public static void e(ATag tag, String msg, Throwable err) {
        if (tag == null || err == null || msg == null) return;
        Log.e(tag.toString(), msg, err);
    }
    public static void e(ATag tag, Throwable err) {
        if (tag == null || err == null) return;
        Log.e(tag.toString(), err.toString(), err);
    }
    
    public static void w(ATag tag, String msg) {
        if (tag == null || msg == null) return;
        Log.w(tag.toString(), msg);
    }
    public static void w(ATag tag, String msg, Throwable err) {
        if (tag == null || err == null || msg == null) return;
        Log.w(tag.toString(), msg, err);
    }
    public static void w(ATag tag, Throwable err) {
        if (tag == null || err == null) return;
        Log.w(tag.toString(), err.toString(), err);
    }
}
