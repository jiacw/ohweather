package com.jiacw.d35ohweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jiacw.d35ohweather.R;
import com.jiacw.d35ohweather.model.City;
import com.jiacw.d35ohweather.model.County;
import com.jiacw.d35ohweather.model.OhWeatherDB;
import com.jiacw.d35ohweather.model.Province;
import com.jiacw.d35ohweather.util.HttpCallbackListener;
import com.jiacw.d35ohweather.util.HttpUtil;
import com.jiacw.d35ohweather.util.Utility;

import net.youmi.android.AdManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiacw on 15:07 26/12/2015.
 * Email: 313133710@qq.com
 * Function:遍历省市县数据的活动
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;
    private TextView mTVTitle;
    private ListView mLV;
    private ArrayAdapter<String> mAdapter;
    private OhWeatherDB mOhWeatherDB;
    private List<String> dataList = new ArrayList<>();
    //省列表
    private List<Province> mProvinceList;
    //市列表
    private List<City> mCityList;
    //县列表
    private List<County> mCountyList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;
    //40.声明是否为WeatherActivity中过来的
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //51.有米广告初始化
        AdManager.getInstance(this).init("724ab49f7985539f","4e84a92c9158efda",false);
        //52.->weather_layout.xml
        //判断是否为天气界面过来的
       isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            //判断是否有选中的城市
            if (preferences.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
                finish();
                return;
            }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        //获取控件实例
        mLV = (ListView) findViewById(R.id.ca_lv);
        mTVTitle = (TextView) findViewById(R.id.ca_tvTitle);
        //初始化适配器
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        //设置适配器
        mLV.setAdapter(mAdapter);
        //获取数据库操作实例
         mOhWeatherDB = OhWeatherDB.getInstance(this);
        //设置点击事件
        mLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentLevel) {
                    case LEVEL_PROVINCE:
                        selectedProvince = mProvinceList.get(position);
                        //查询城市数据
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        selectedCity = mCityList.get(position);
                        queryCounties();
                        break;
                    case LEVEL_COUNTY:
                        //获取选中的县代号
                        String countyCode=mCountyList.get(position).getCountyCode();
                        Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                        intent.putExtra("county_code",countyCode);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });
        //加载省级数据
        queryProvinces();
    }

    //25.查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
    private void queryProvinces() {
        mProvinceList = mOhWeatherDB.loadProvinces();
        if (mProvinceList.size() > 0) {
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.getProvinceName());
            }
            //适配器刷新
            mAdapter.notifyDataSetChanged();
            //设置选择的位置为0
            mLV.setSelection(0);
            mTVTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    //26查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
    private void queryCities() {
        mCityList = mOhWeatherDB.loadCities(selectedProvince.getId());
        if (mCityList.size()>0){
            dataList.clear();
            for (City city:mCityList){
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mLV.setSelection(0);
            mTVTitle.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    //27查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
    private void queryCounties() {
        mCountyList = mOhWeatherDB.loadCounties(selectedCity.getId());
        if (mCountyList.size()>0){
            dataList.clear();
            for (County county:mCountyList){
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mLV.setSelection(0);
            mTVTitle.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
    //28根据传入的代号和类型从服务器上查询省市县数据。
    private void queryFromServer(final String  code,final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result  = Utility.handleProvinceResponse(mOhWeatherDB,response);
                }else if ("city".equals(type)){
                    result  =Utility.handleCitiesResponse(mOhWeatherDB,response
                            ,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountiesResponse(mOhWeatherDB,response
                            ,selectedCity.getId());
                }
                //如果保存数据成功
                if (result){
                    // 通过runOnUiThread() 方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                               queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread() 方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //29显示进度对话框
    private void showProgressDialog() {
        if (mProgressDialog==null){
            mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }
    //30关闭进度对话框
    private void closeProgressDialog() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
    //31. 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            //40,如果来自WeatherActivity，则退回——》Service： AutoUpdateService；
            if (isFromWeatherActivity){
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
    //32.Manifest
}
