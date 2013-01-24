/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.onextent.android.codeable.Codeable;
import com.onextent.android.store.CodeStoreSqliteImpl;
import com.onextent.augie.AugLog;

//todo: consider ics dberrorhandler

public class AugieDbHelper extends SQLiteOpenHelper {
    
    private static final String CREATE_TABLE = "CREATE TABLE " + CodeStoreSqliteImpl.TABLE_NAME + " (" + CodeStoreSqliteImpl.KEY_ID + " STRING primary key, " + CodeStoreSqliteImpl.CONTENT_NAME + " TEXT not null );";

    public AugieDbHelper(Context context, 
                         String name, 
                         CursorFactory factory,
                         int version) {
        
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        AugLog.d( "create db");
       
        try {
            
            db.execSQL(CREATE_TABLE);
        } catch (SQLiteException e) {
            AugLog.e( "can not create db", e);
        }
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        Log.w(Codeable.TAG, "re-create db for upgrade from version " + oldVersion + " to version " + newVersion);
        db.execSQL("drop table if exists " + CodeStoreSqliteImpl.TABLE_NAME);
        onCreate(db);
    }
}
