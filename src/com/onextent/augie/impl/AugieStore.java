package com.onextent.augie.impl;

import com.onextent.augie.Augieable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class AugieStore {
    
    /**
     * 
     * list mode augienames
     * 
     * get mode by augiename
     * 
     * a mode:
     *   name
     *   description
     *   camera augiename
     *   camera state
     *   list of augiements enabled
     * 
     */
    
    public static final String TAG = Augieable.TAG;
    
    public static final String DATABASE_NAME = "augiestore_main";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "augies";
    public static final String KEY_ID = "_id";
    public static final String CONTENT_NAME = "content";
    
    private SQLiteDatabase db;
    private AugieDbHelper dbhelper;
    
    private final Context context;
    
    public AugieStore(Context c) {
        context = c;
    }
    
    public void close() {
        if (db != null)  {
            db.close();
            db = null;
        }
        if (dbhelper != null) {
            dbhelper.close();
            dbhelper = null;
        }
    }
    
    public void open() throws SQLiteException {
    
        if (dbhelper != null) throw new SQLiteException("dbhelper already init");
        dbhelper = new AugieDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        
        if (db != null) throw new SQLiteException("db already init");
        try {
            db = dbhelper.getWritableDatabase();
            
        } catch (SQLiteException e) {
            
            Log.e(TAG, "error getting writable db", e);
            
            db = dbhelper.getReadableDatabase();
        }
    }
    
    public long remove(String key) throws SQLiteException {
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, key);
    
        try {
            return db.delete(TABLE_NAME, KEY_ID + " = \"" + key + "\"", null);
        } catch (SQLiteException e) {
            Log.e(TAG, "can not insert", e);
            throw e;
        }
    }
    
    public long replaceContent(String key, String content) throws SQLiteException {
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, key);
        values.put(CONTENT_NAME, content);
    
        try {
            return db.replace(TABLE_NAME, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "can not insert", e);
            throw e;
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
    
    public Cursor getAllContent() throws SQLiteException {
        
        try {
            Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        
            return c;
        } catch (SQLiteException e) {
            Log.e(TAG, "can not get content", e);
            throw e;
        }
    }
    
    public String getContentString(String key) throws SQLiteException {
        String ret = null;
        Cursor c = getContent(key);
        if (c != null) {
            if (c != null && c.moveToFirst()) {
                int i = c.getColumnIndex(CONTENT_NAME);
                ret = c.getString(i);
            }
            c.close();
        }
        return ret;
    }

    public Cursor getContent(String key) throws SQLiteException {
        
        try {
            return db.query(TABLE_NAME,
                    new String[] {CONTENT_NAME},
                    KEY_ID + " LIKE \"" + key + "\"",
                    null,
                    null,
                    null,
                    null
                   );

        } catch (SQLiteException e) {
            Log.e(TAG, "can not get content", e);
            throw e;
        }
    }
}
