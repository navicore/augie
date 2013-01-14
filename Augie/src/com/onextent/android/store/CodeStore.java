package com.onextent.android.store;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

public interface CodeStore {

    void close();

    void open() throws SQLiteException;

    long remove(String key) throws SQLiteException;

    long replaceContent(String key, String content) throws SQLiteException;
    long replaceContent(CodeableName key, Code content) throws SQLiteException;

    long insertContent(String key, String content) throws SQLiteException;
    long insertContent(CodeableName key, Code content) throws SQLiteException;

    String getContentString(String key) throws SQLiteException;
    Code getContentCode(CodeableName key) throws SQLiteException, CodeableException;

    Cursor getContentCursor(String key) throws SQLiteException;
    
    Code dump() throws CodeableException;
}
