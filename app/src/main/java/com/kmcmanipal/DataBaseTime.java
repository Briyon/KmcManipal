package com.kmcmanipal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NAGS on 6/11/2016.
 */
public class DataBaseTime extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "logindata.db";
    private static final String TABLE_NAME = "logs";

    private static final String COL_1 = "name";
    private static final String COL_2 = "actime";
    SQLiteDatabase db;


    public DataBaseTime(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + " (name TEXT,actime BIGINT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXIST"+TABLE_NAME );
        onCreate(db);

    }
    public boolean inserttime(String name,String actime)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues = new  ContentValues();
        contentValues.put(COL_1,name);
        contentValues.put(COL_2, actime);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }
    public Cursor getAllData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
}
