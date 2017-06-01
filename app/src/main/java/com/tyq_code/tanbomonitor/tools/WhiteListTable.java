package com.tyq_code.tanbomonitor.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WhiteListTable extends SQLiteOpenHelper {
    private final static String DATABASE_NAME     = "tanbo.db";
    private final static int    DATABASE_VERSION  = 1;
    private       static String TABLE_NAME        = "white_list";
    public  final static String PACKAGE_ID        = "_id";
    public  final static String PACKAGE_NAME      = "package_name";

    public WhiteListTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " ( " + PACKAGE_ID + " INTEGER primary key autoincrement, "
                + PACKAGE_NAME      + " TEXT );";
//        Log.i("onCreate", sql);
        db.execSQL(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    //增加操作
    public long insert(String package_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PACKAGE_NAME, package_name);
        return db.insert(TABLE_NAME, null, cv);
    }

    //删除操作
    public void delete(String package_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = PACKAGE_NAME + " = ?";
        String[] whereValue = {package_name};
        db.delete(TABLE_NAME, where, whereValue);
    }

    public Cursor getPackageInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
