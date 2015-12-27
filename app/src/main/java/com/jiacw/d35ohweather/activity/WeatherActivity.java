package com.jiacw.d35ohweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiacw.d35ohweather.R;
import com.jiacw.d35ohweather.service.AutoUpdateService;
import com.jiacw.d35ohweather.util.HttpCallbackListener;
import com.jiacw.d35ohweather.util.HttpUtil;
import com.jiacw.d35ohweather.util.Utility;

/**
 * Created by Jiacw on 08:44 27/12/2015.
 * Email: 313133710@qq.com
 * Function:38.天气界面
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout mLLWeatherInfo;
    //城市名
    private TextView mTVCityName;
    //显示发布时间
    private TextView mTVPublishText;
    //显示天气描述信息
    private TextView mTVWeatherDesp;
    //显示气温1
    private TextView mTVTemp1;
    //显示气温2
    private TextView mTVTemp2;
    //显示当前日期
    private TextView mTVCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        mLLWeatherInfo= (LinearLayout) findViewById(R.id.wl_llWeatherInfo);
        mTVCityName= (TextView) findViewById(R.id.wl_tvCityName);
        mTVPublishText= (TextView) findViewById(R.id.wl_tvPublish_text);
        mTVWeatherDesp= (TextView) findViewById(R.id.wl_tvWeatherDesp);
        mTVTemp1= (TextView) findViewById(R.id.wl_tvTemp1);
        mTVTemp2= (TextView) findViewById(R.id.wl_tvTemp2);
        mTVCurrentDate= (TextView) findViewById(R.id.wl_tvCurrentDate);
        Button btnSwitchCity = (Button) findViewById(R.id.wl_btnSwitch_City);
        Button btnRefreshWeather = (Button) findViewById(R.id.wl_btnRefresh_weather);
        //获取县代号
        String countyCode=getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            mTVPublishText.setText("同步中...");
            //隐藏天气/城市信息
            mLLWeatherInfo.setVisibility(View.INVISIBLE);
            mTVCityName.setVisibility(View.INVISIBLE);
            // 有县级代号时就去查询天气
            queryWeatherCode(countyCode);
        }else{
            // 没有县级代号时就直接显示本地天气
            showWeather();
        }
        btnRefreshWeather.setOnClickListener(this);
        btnSwitchCity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wl_btnSwitch_City:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.wl_btnRefresh_weather:
                mTVPublishText.setText("同步中...");
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=preferences.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    /**
     * created at 27/12/2015 10:14
     * function: 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address, "countyCode");
    }
    /**
    * created at 27/12/2015 10:30
    * function:查询天气代号所对应的天气。
    */
    private void queryWeatherInfo(String weatherCode) {
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }
    /**
    * created at 27/12/2015 10:34
    * function:根据传入的地址和类型去向服务器查询天气代号或者天气信息。
    */
    private void queryFromServer(final String address,final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //判断请求类型
                if ("countyCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
                    //从服务器返回的数据中解析出天气代号
                        String[] array=response.split("\\|");
                        if (array.length == 2){
                          String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)){
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示天气
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTVPublishText.setText("同步失败");
                    }
                });
            }
        });
    }
    /**
    * created at 27/12/2015 10:42
    * function: 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
    */
    private void showWeather() {
        SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(this);
        mTVCityName.setText(preference.getString("city_name",""));
        mTVTemp1.setText(preference.getString("temp1",""));
        mTVTemp2.setText(preference.getString("temp2",""));
        mTVWeatherDesp.setText(preference.getString("weather_desp",""));
        mTVPublishText.setText("今天"+preference.getString("publish_time","")+"发布");
        mTVCurrentDate.setText(preference.getString("current_date",""));
        mLLWeatherInfo.setVisibility(View.VISIBLE);
        mTVCityName.setVisibility(View.VISIBLE);
        //45.启动后台服务
        Intent intent =new Intent(this, AutoUpdateService.class);
        startService(intent);
        //46->Manifest
    }
//39.修改 ChooseAreaActivity 中的代码

}
