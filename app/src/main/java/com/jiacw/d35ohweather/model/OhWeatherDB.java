package com.jiacw.d35ohweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jiacw.d35ohweather.database.OW_OpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 20:14 24/12/2015.
 * Email: 313133710@qq.com
 * Function:把一些常用的数据库操作封装起来，以方便后面使用
 */
public class OhWeatherDB {
    //7.数据库名
    public final static String DB_NAME = "oh_weather";
    //8.数据库版本
    public final static int VERSION = 1;
    //9.声明，当前类和操作类
    private static OhWeatherDB mOhWeather;
    private SQLiteDatabase mSQLDb;

    //10.私有构造方法
    private OhWeatherDB(Context context) {
        OW_OpenHelper openHelper = new OW_OpenHelper(context, DB_NAME, null, VERSION);
        mSQLDb = openHelper.getWritableDatabase();//获取可写数据库实例
    }


    /**
     * created at 24/12/2015 20:32
     * function: 11.获取OhWeatherDB的实例
     */
    //只准同时一个获取
    public synchronized static OhWeatherDB getInstance(Context context) {
        if (mOhWeather == null) {
            mOhWeather = new OhWeatherDB(context);
        }
        return mOhWeather;
    }

    /**
     * created at 24/12/2015 20:31
     * function: 12.将省份信息插入到数据库。
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put(OW_OpenHelper.PROVINCE_NAME, province.getProvinceName());
            values.put(OW_OpenHelper.PROVINCE_CODE, province.getProvinceCode());
            mSQLDb.insert(OW_OpenHelper.PROVINCE, null, values);
        }
    }

    /**
     * created at 24/12/2015 20:37
     * function: 13.从数据库中读取全国省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        //查询数据库
        Cursor cursor = mSQLDb.query(OW_OpenHelper.PROVINCE,null,null,null,null,null,null);
        //当移动到第一行
        if (cursor.moveToFirst()){
            //循环为列表添加数据
            do{
                //实例化对象
                Province province = new Province();
                //为对象赋值
                province.setId(cursor.getInt(cursor.getColumnIndex(OW_OpenHelper.ID)));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex(OW_OpenHelper.PROVINCE_NAME)));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex(OW_OpenHelper.PROVINCE_CODE)));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
    * created at 24/12/2015 20:48
    * function: 14.将城市信息添加到数据库中
    */
    public void saveCity(City city){
        if (city!=null){
            ContentValues values =new ContentValues();
            values.put(OW_OpenHelper.CITY_NAME,city.getCityName());
            values.put(OW_OpenHelper.CITY_CODE,city.getCityCode());
            values.put(OW_OpenHelper.PROVINCE_ID,city.getProvinceId());
            mSQLDb.insert(OW_OpenHelper.CITY, null, values);
        }
    }

    /**
    * created at 24/12/2015 20:51
    * function: 15.从数据库中读取城市信息
    */
    public List<City>  loadCities(int provinceId){
        List<City> list = new ArrayList<>();
        //查询数据库
        Cursor cursor = mSQLDb.query(OW_OpenHelper.CITY,null, OW_OpenHelper.PROVINCE_ID+"=?"
                ,new String[]{String.valueOf(provinceId)},null,null,null);
        //当移动到第一行
        if (cursor.moveToFirst()){
            //循环为列表添加数据
            do{
                //实例化对象
                City city = new City();
                //为对象赋值
                city.setId(cursor.getInt(cursor.getColumnIndex(OW_OpenHelper.ID)));
                city.setCityName(cursor.getString(cursor.getColumnIndex(OW_OpenHelper.CITY_NAME)));
                city.setCityCode(cursor.getString(cursor.getColumnIndex(OW_OpenHelper.CITY_CODE)));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex(OW_OpenHelper.PROVINCE_ID)));
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
    * created at 24/12/2015 21:21
    * function: 16.将乡镇信息存储到数据库
    */
    public void saveCounty(County county){
        if (county!=null){
            ContentValues values = new ContentValues();
            values.put(OW_OpenHelper.COUNTY_NAME,county.getCountyName());
            values.put(OW_OpenHelper.COUNTY_CODE,county.getCountyCode());
            values.put(OW_OpenHelper.CITY_ID,county.getCityId());
            mSQLDb.insert(OW_OpenHelper.COUNTY,null,values);
        }
    }

    /**
    * created at 24/12/2015 21:47
    * function: 17.从数据库中读取乡镇信息
    */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<>();
        Cursor cursor = mSQLDb.query(OW_OpenHelper.COUNTY,null,OW_OpenHelper.CITY_ID+"=?"
                ,new String[]{String.valueOf(cityId)},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex(OW_OpenHelper.ID)));
                county.setCountyName(cursor.getString(cursor.getColumnIndex(OW_OpenHelper.COUNTY_NAME)));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex(OW_OpenHelper.COUNTY_CODE)));
                county.setCityId(cursor.getInt(cursor.getColumnIndex(OW_OpenHelper.CITY_ID)));
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }
    //18->util:HttpUtil
}
