package com.jiacw.d35ohweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jiacw on 10:40 24/12/2015.
 * Email: 313133710@qq.com
 * Function:数据库和表
 */
public class OW_OpenHelper extends SQLiteOpenHelper {

    public final static String ID = "id";
    public final static String PROVINCE = "Province";
    public final static String PROVINCE_NAME = "province_name";
    public final static String PROVINCE_CODE = "province_code";

    public final static String CITY = "City";
    public final static String CITY_NAME = "city_name";
    public final static String CITY_CODE = "city_code";
    public final static String PROVINCE_ID = "province_id";

    public final static String COUNTY = "County";
    public final static String COUNTY_NAME = "county_name";
    public final static String COUNTY_CODE = "county_code";
    public final static String CITY_ID = "city_id";

    //1.建表语句
    private final static String CREATE_PROVINCE = "create table " +
            PROVINCE + "(" +
            ID + " integer primary key autoincrement," +
            PROVINCE_NAME + " text," +
            PROVINCE_CODE + " text)";
    private final static String CREATE_CITY = "create table "+
            CITY+"(" +
            ID+" integer primary key autoincrement," +
            CITY_NAME+" text," +
            CITY_CODE+" text," +
            PROVINCE_ID+" integer)";
    private final static String CREATE_COUNTY = "create table "+
            COUNTY+"(" +
            ID+" integer primary key autoincrement," +
            COUNTY_NAME+" text," +
            COUNTY_CODE+" text," +
            CITY_ID+" integer)";

    public OW_OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory
            , int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //2.创建3个表-》model：Province
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
