package com.example.testappfornatlex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MAIN";
    public static final String TABLE_NAME = "My_table";
    public static final String KEY_ID = "_id";
    public static final String NAME = "name";
    public static final String TEMPE = "tempe";
    public static final String TIME = "time";
    public static final String IMAGE = "image";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + TIME + " text,"
                + NAME + " text,"
                + IMAGE + " text,"
                + TEMPE + " text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists mytable");
        onCreate(db);
    }
}
