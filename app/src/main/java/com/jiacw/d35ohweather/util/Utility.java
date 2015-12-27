package com.jiacw.d35ohweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.jiacw.d35ohweather.model.City;
import com.jiacw.d35ohweather.model.County;
import com.jiacw.d35ohweather.model.OhWeatherDB;
import com.jiacw.d35ohweather.model.Province;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jiacw on 14:13 26/12/2015.
 * Email: 313133710@qq.com
 * Function:工具类来解析和处理返回数据
 */

public class Utility {
    //20.解析省份数据
    public synchronized static boolean handleProvinceResponse(OhWeatherDB ohWeatherDB
            , String response) {
        //确认文本非空
        if (!TextUtils.isEmpty(response)) {
            //获取所有省份的数组
            String[] allProvinces = response.split(",");
            if (allProvinces.length > 0) {
                for (String p : allProvinces) {
                    //将|或运算符转义成字符“|”
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    ohWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    //21.解析和处理服务器返回的市级数据
    public static boolean handleCitiesResponse(OhWeatherDB ohWeatherDB, String response
            , int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    ohWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * created at 26/12/2015 14:47
     * function: 22解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(OhWeatherDB ohWeatherDB, String response
            , int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    ohWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    //23.res/layout:choose_area.xml

    /**
     * created at 27/12/2015 8:24
     * function:35.解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * created at 27/12/2015 8:34
     * function: 36.将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode
            , String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", dataFormat.format(new Date()));
        editor.apply();
    }
    //37.新建activity： WeatherActivity
}
