package com.jiacw.d35ohweather.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jiacw on 14:39 25/12/2015.
 * Email: 313133710@qq.com
 * Function:遍历全国省市县
 */
//18.处理网络请求——》创建接口
public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection mUrlConnection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(address);
                    mUrlConnection = (HttpURLConnection) url.openConnection();
                    mUrlConnection.setRequestMethod("GET");
                    mUrlConnection.setReadTimeout(8000);
                    mUrlConnection.setConnectTimeout(8000);
                    InputStream inputStream = mUrlConnection.getInputStream();
                     reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line="";
                    StringBuilder response = new StringBuilder();
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (IOException e) {
                   if (listener!=null){
                        listener.onError(e);
                   }
                }finally {
                    //关闭连接
                    if (mUrlConnection!=null){
                        mUrlConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}

