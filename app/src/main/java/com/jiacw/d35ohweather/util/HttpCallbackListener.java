package com.jiacw.d35ohweather.util;

/**
 * Created by Jiacw on 13:25 26/12/2015.
 * Email: 313133710@qq.com
 * Function:网络请求，成功、失败的接口
 */
//19.添加接口——》Utility
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
