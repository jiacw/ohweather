package com.jiacw.d35ohweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiacw.d35ohweather.service.AutoUpdateService;

/**
 * Created by Jiacw on 20:41 27/12/2015.
 * Email: 313133710@qq.com
 * Function:.接收广播
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService=new Intent(context,AutoUpdateService.class);
        context.startService(intentService);
    }
    //44.->WeatherActivity
}
