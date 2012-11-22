package com.onextent.augie.impl;

import com.onextent.augie.Augieable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class AugieStore {
    
    public static final String TAG = Augieable.TAG;
    
    public static final String DATABASE_NAME = "augiestore_main";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "augies";
    public static final String KEY_ID = "_id";
    public static final String CONTENT_NAME = "content";
    
    private SQLiteDatabase db;
    private final AugieDbHelper dbhelper;
    
    public AugieStore(Context c) {
        dbhelper = new AugieDbHelper(c, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void close() {
        db.close();
    }
    
    public void open() throws SQLiteException {
        
        try {
            
            db = dbhelper.getWritableDatabase();
            
        } catch (SQLiteException e) {
            
            Log.e(TAG, "error getting writable db", e);
            
            db = dbhelper.getReadableDatabase();
        }
    }
    
    public long insertContent(String key, String content) throws SQLiteException {
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, key);
        values.put(CONTENT_NAME, content);
    
        try {
            return db.insert(TABLE_NAME, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "can not insert", e);
            throw e;
        }
    }
    
    public Cursor getContent() throws SQLiteException {
        
        try {
            Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        
            return c;
        } catch (SQLiteException e) {
            Log.e(TAG, "can not get content", e);
            throw e;
        }
    }
}
