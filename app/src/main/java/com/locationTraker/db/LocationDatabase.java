package com.locationTraker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDatabase extends SQLiteOpenHelper {

    public static final String VERIFCATION_LOG_tbl = "tblLocation";
    public static final String LONGITUDE = "longitude";
    public static final String ID_col = "ID_stamp";
    public static final String LATITUDE = "latitude";

    public LocationDatabase(Context context) {
        super(context, "locationDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VERIFCATION_LOG_tbl +
                "(" + ID_col + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LONGITUDE + " text, " +
                LATITUDE + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VERIFCATION_LOG_tbl);
    }


    public boolean insertLog(String longitude, String latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(LATITUDE, latitude);
        db.insert(VERIFCATION_LOG_tbl, null, contentValues);
        return true;
    }


    public Cursor getAllLog() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + VERIFCATION_LOG_tbl , null);
    }

}
