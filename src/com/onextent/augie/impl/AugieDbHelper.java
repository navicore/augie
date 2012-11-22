package com.onextent.augie.impl;

import com.onextent.augie.Augieable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//todo: consider ics dberrorhandler

public class AugieDbHelper extends SQLiteOpenHelper {
    
    private static final String TAG = Augieable.TAG;
    
    private static final String CREATE_TABLE = "CREATE TABLE " + AugieStore.TABLE_NAME + " (" + AugieStore.KEY_ID + " STRING primary key, " + AugieStore.CONTENT_NAME + " TEXT not null );";

    public AugieDbHelper(Context context, 
                         String name, 
                         CursorFactory factory,
                         int version) {
        
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "create db");
       
        try {
            
            db.execSQL(CREATE_TABLE);
        } catch (SQLiteException e) {
            Log.e(TAG, "can not create db", e);
        }
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        Log.w(TAG, "re-create db for upgrade from version " + oldVersion + " to version " + newVersion);
        db.execSQL("drop table if exists " + AugieStore.TABLE_NAME);
        onCreate(db);
    }
}
