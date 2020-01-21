package com.example.databaseexample.databas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.databaseexample.databas.ToolsHistory.*;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ToolsTable.db";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GROCERYLIST_TABLE = "CREATE TABLE " +
                ToolsItemEntery.TABLE_NAME + "(" +
                ToolsItemEntery._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ToolsItemEntery.COLUMN_INFO1 + " TEXT NOT NULL, " +
                ToolsItemEntery.COLUMN_INFO2 + " TEXT NOT NULL, " +
                ToolsItemEntery.COLUMN_INFO3 + " TEXT NOT NULL " +
                ")";
        db.execSQL(SQL_CREATE_GROCERYLIST_TABLE);
        Log.d("abc", "onCreate: vhjbgh");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ToolsItemEntery.TABLE_NAME);
        onCreate(db);
    }

    //src
    public void insertToolsHistoryTB(String time, String tool, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToolsItemEntery.COLUMN_INFO1, time);
        contentValues.put(ToolsItemEntery.COLUMN_INFO2, tool);
        contentValues.put(ToolsItemEntery.COLUMN_INFO3, status);
        long result = db.insert(ToolsItemEntery.TABLE_NAME, null, contentValues);
    }

    //src
    public Cursor getHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "SELECT ToolsHistory.ToolsItemEntery.COLUMN_INFO1,count(ToolsHistory.ToolsItemEntery.COLUMN_INFO1) FROM tools group by time" ;
        Cursor data = db.rawQuery("select " + ToolsItemEntery.COLUMN_INFO1 + ",count(" + ToolsItemEntery.COLUMN_INFO1 + ")" + " from " + ToolsItemEntery.TABLE_NAME + " where " + ToolsItemEntery.COLUMN_INFO3 + " = '" + 1 + "'" + " group by " + ToolsItemEntery.COLUMN_INFO1, null);
        return data;
    }

    //src
    public Cursor getDetail(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "SELECT keys,value FROM tools where time=="+time ;
        Cursor data = db.rawQuery("select " + ToolsItemEntery.COLUMN_INFO2 + "," + ToolsItemEntery.COLUMN_INFO3 + " from " + ToolsItemEntery.TABLE_NAME + " where " + ToolsItemEntery.COLUMN_INFO1 + " = '" + time + "'", null);
        return data;
    }

}
