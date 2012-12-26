package com.onextent.util.store;

import com.onextent.augie.impl.AugieDbHelper;
import com.onextent.util.codeable.Code;
import com.onextent.util.codeable.CodeableException;
import com.onextent.util.codeable.CodeableName;
import com.onextent.util.codeable.JSONCoder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class CodeStoreSqliteImpl implements CodeStore {
    
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
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "STRINGS";
    public static final String KEY_ID = "_id";
    public static final String CONTENT_NAME = "CONTENT";
    
    private SQLiteDatabase db;
    private AugieDbHelper dbhelper;
    private final String dbname;
    
    private final Context context;
    
    public CodeStoreSqliteImpl(Context c, String dbname) {
        context = c;
        this.dbname = dbname;
    }
    
    @Override
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
    
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.StringStore#open()
     */
    @Override
    public void open() throws SQLiteException {
    
        if (dbhelper != null) throw new SQLiteException("dbhelper already init");
        dbhelper = new AugieDbHelper(context, dbname, null, DATABASE_VERSION);
        
        if (db != null) throw new SQLiteException("db already init");
        try {
            db = dbhelper.getWritableDatabase();
            
        } catch (SQLiteException e) {
            
            e.printStackTrace();
            
            db = dbhelper.getReadableDatabase();
        }
    }
    
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.StringStore#remove(java.lang.String)
     */
    @Override
    public long remove(String key) throws SQLiteException {
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, key);
    
        return db.delete(TABLE_NAME, KEY_ID + " = \"" + key + "\"", null);
    }
    
    /* (non-Javadoc)
     * @see com.onextent.augie.impl.StringStore#replaceContent(java.lang.String, java.lang.String)
     */
    @Override
    public long replaceContent(String key, String content) throws SQLiteException {
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, key);
        values.put(CONTENT_NAME, content);
    
        return db.replace(TABLE_NAME, null, values);
    }
    
    @Override
    public long insertContent(String key, String content) throws SQLiteException {
        
        ContentValues values = new ContentValues();
        values.put(KEY_ID, key);
        values.put(CONTENT_NAME, content);
    
        return db.insert(TABLE_NAME, null, values);
    }
    
    private Cursor getAllContentCursor() throws SQLiteException {
        
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        
        return c;
    }
    
    @Override
    public String getContentString(String key) throws SQLiteException {
        String ret = null;
        Cursor c = getContentCursor(key);
        if (c != null) {
            if (c != null && c.moveToFirst()) {
                int i = c.getColumnIndex(CONTENT_NAME);
                ret = c.getString(i);
            }
            c.close();
        }
        return ret;
    }

    @Override
    public Cursor getContentCursor(String key) throws SQLiteException {
        
        return db.query(TABLE_NAME,
                new String[] {CONTENT_NAME},
                KEY_ID + " LIKE \"" + key + "\"",
                null,
                null,
                null,
                null
                );
    }

    @Override
    public long replaceContent(CodeableName key, Code content) throws SQLiteException {
        return replaceContent(key.toString(), content.toString());
    }

    @Override
    public long insertContent(CodeableName key, Code content) throws SQLiteException {
        return insertContent(key.toString(), content.toString());
    }

    @Override
    public Code getContentCode(CodeableName key) throws SQLiteException, CodeableException {
        return JSONCoder.newCode(getContentString(key.toString()));
    }

    @Override
    public Code dump() throws CodeableException {
        Code code = JSONCoder.newCode();
        Cursor c = getAllContentCursor();
        if (c != null) {
            while (c != null && c.moveToNext()) {
                int ki = c.getColumnIndex(KEY_ID);
                String key = c.getString(ki);
                int ci = c.getColumnIndex(CONTENT_NAME);
                String value = c.getString(ci);
                if (value != null) 
                    if (value.charAt(0) == '{') {
                        code.put(key, JSONCoder.newCode(value));
                    } else {
                        code.put(key, value);
                    }
            }
            c.close();
        }
        return code;
    }
}
