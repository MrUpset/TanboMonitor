package com.tyq_code.tanbomonitor.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class UsageInfoTable extends SQLiteOpenHelper {
    private final static String DATABASE_NAME     = "PackageDB.db";
    private final static int    DATABASE_VERSION  = 1;
    private       static String TABLE_NAME;
    public  final static String PACKAGE_ID        = "_id";
    public  final static String PACKAGE_NAME      = "package_name";
    public  final static String PACKAGE_TIME      = "package_time";
    public  final static String PACKAGE_FREQUENCY = "package_frequency";

    public UsageInfoTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("UsageInfoTable", "start");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH) + 1, day = calendar.get(Calendar.DAY_OF_MONTH);
        TABLE_NAME = "table_" + year + "_" + (month > 9 ? month : ("0" + month)) + "_" + (day > 9 ? day : ("0" + day));
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " ( " + PACKAGE_ID + " INTEGER primary key autoincrement, "
                + PACKAGE_NAME      + " TEXT, "
                + PACKAGE_TIME      + " INTEGER, "
                + PACKAGE_FREQUENCY + " INTEGER );";
        Log.e("onCreate", sql);
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
    public long insert(String package_name, float package_time, int package_frequency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PACKAGE_NAME, package_name);
        cv.put(PACKAGE_TIME, package_time);
        cv.put(PACKAGE_FREQUENCY, package_frequency);
        return db.insert(TABLE_NAME, null, cv);
    }

    //删除操作
    public void delete(String package_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = PACKAGE_NAME + " = ?";
        String[] whereValue = {package_name};
        db.delete(TABLE_NAME, where, whereValue);
    }

    //修改操作
    public void update(String package_name, float package_time, int package_frequency) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = PACKAGE_NAME + " = ?";
        String[] whereValue = {package_name};

        ContentValues cv = new ContentValues();
        cv.put(PACKAGE_TIME, package_time);
        cv.put(PACKAGE_FREQUENCY, package_frequency);
        db.update(TABLE_NAME, cv, where, whereValue);
    }

    //查询操作
    public Cursor select(String package_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE package_name='" + package_name + "'", null);
    }

    public Cursor getPackageInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
