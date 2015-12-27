package com.jiacw.d35ohweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.jiacw.d35ohweather.util.HttpCallbackListener;
import com.jiacw.d35ohweather.util.HttpUtil;
import com.jiacw.d35ohweather.util.Utility;

/**
 * Created by Jiacw on 20:10 27/12/2015.
 * Email: 313133710@qq.com
 * Function:41后台自动更新天气
 */
public class AutoUpdateService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        //8小时
        int eightHour=8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+eightHour;
        Intent intentBroadCast=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intentBroadCast,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
    * created at 27/12/2015 20:14
    * function: 更新天气信息
    */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=preferences.getString("weather_code", "");
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //在后台，将信息更新到本地
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
    //42->AutoUpdateReceiver
}
