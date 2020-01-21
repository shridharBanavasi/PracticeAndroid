package com.example.toreading.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Canvas;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import static android.content.ContentValues.TAG;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "readings.db";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //create tables here.
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GROCERYLIST_TABLE = "CREATE TABLE " +
                ReadingTable.ReadingTableEntry.TABLE_NAME + "(" +
                ReadingTable.ReadingTableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReadingTable.ReadingTableEntry.COLUMN_INFO1+ " TEXT NOT NULL, " +
                ReadingTable.ReadingTableEntry.COLUMN_INFO2+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO3+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO4+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO5+ " INTEGER NOT NULL, " +
                ReadingTable.ReadingTableEntry.COLUMN_INFO6+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO7+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO8+ " INTEGER NOT NULL, " +
                ReadingTable.ReadingTableEntry.COLUMN_INFO9+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO10+ " INTEGER NOT NULL, " +
                ReadingTable.ReadingTableEntry.COLUMN_INFO11+ " INTEGER NOT NULL, "+
                ReadingTable.ReadingTableEntry.COLUMN_INFO12+ " INTEGER NOT NULL "+
                ")";
        db.execSQL(SQL_CREATE_GROCERYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReadingTable.ReadingTableEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insert(List<JSONArray> reading) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0;i<reading.size();i++){
           ContentValues contentValues = new ContentValues();
           JSONArray z=reading.get(i);
            contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO1, "T"+i);
            try {
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO2, z.getInt(0));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO3, z.getInt(1));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO4, z.getInt(2));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO5, z.getInt(3));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO6, z.getInt(4));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO7, z.getInt(5));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO8, z.getInt(6));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO9, z.getInt(7));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO10, z.getInt(8));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO11, z.getInt(9));
                contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO12, z.getInt(10));
                int result =(int) db.insert(ReadingTable.ReadingTableEntry.TABLE_NAME, null, contentValues);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO1, "End one reading");
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO2, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO3, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO4, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO5, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO6, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO7, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO8, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO9, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO10, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO11, 0);
        contentValues.put(ReadingTable.ReadingTableEntry.COLUMN_INFO12, 0);
        int result =(int) db.insert(ReadingTable.ReadingTableEntry.TABLE_NAME, null, contentValues);
        Log.d(TAG, "insert: ");
    }

    //clear all data stored in table.
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ReadingTable.ReadingTableEntry.TABLE_NAME, null, null);
    }
}
