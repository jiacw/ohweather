package com.jiacw.d35ohweather.util;

import android.text.TextUtils;

import com.jiacw.d35ohweather.model.City;
import com.jiacw.d35ohweather.model.County;
import com.jiacw.d35ohweather.model.OhWeatherDB;
import com.jiacw.d35ohweather.model.Province;

/**
 * Created by Jiacw on 14:13 26/12/2015.
 * Email: 313133710@qq.com
 * Function:工具类来解析和处理返回数据
 */

public class Utility {
    //20.解析省份数据
    public synchronized static boolean handleProvinceResponse(OhWeatherDB ohWeatherDB
            ,String response){
        //确认文本非空
        if (!TextUtils.isEmpty(response)){
            //获取所有省份的数组
            String[] allProvinces = response.split(",");
            if (allProvinces.length > 0){
                for (String p:allProvinces){
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
    public static boolean handleCitiesResponse(OhWeatherDB ohWeatherDB,String response
            ,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities.length>0){
                for (String c:allCities){
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
    public static boolean handleCountiesResponse(OhWeatherDB ohWeatherDB,String response
            ,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties.length>0)
            {
                for (String c:allCounties){
                    String[] array  =c.split("\\|");
                    County county=new County();
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
}
