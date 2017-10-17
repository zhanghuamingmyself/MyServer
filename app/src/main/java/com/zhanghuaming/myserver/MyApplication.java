package com.zhanghuaming.myserver;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.morgoo.droidplugin.PluginApplication;
import com.zhanghuaming.myserver.Weather.WeatherInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;


/**
 * Created by zhang on 2017/7/3.
 */

public class MyApplication extends PluginApplication {

    //单例模式
    private static MyApplication mApplication;
    //okhttp
    private OkHttpClient okHttpClient;
    private WeatherInfo allweather;
    private HashMap<String, Integer> mWeatherIcon;// 天气图标
    private static final long cacheSize = 1024 * 1024 * 20;//緩存20M
    private static Cache cache = new Cache(new File(Environment.getDownloadCacheDirectory().toString()), cacheSize);

    public static synchronized MyApplication getInstance() {
        if (mApplication != null)
            return mApplication;
        else {
            mApplication = new MyApplication();
            return mApplication;
        }
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient != null) {
            return okHttpClient;
        } else {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(8, TimeUnit.SECONDS);
            builder.writeTimeout(8, TimeUnit.SECONDS);
            builder.readTimeout(8, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(false);
            builder.cache(cache);
            okHttpClient = builder.build();
            //okHttpClient = new OkHttpClient();
            return okHttpClient;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 内存泄漏检查
       // LeakCanary.install(this);
        //腾讯自动更新
        // if (MySharedPreferences.get(this, MySharedPreferences.AUTONEW).equals("true"))
        //     Bugly.init(getApplicationContext(), "1893fad90d", true);


    }

    public WeatherInfo GetAllWeather() {
        return allweather;
    }

    public void SetAllWeather(WeatherInfo allweather) {
        this.allweather = allweather;
    }

    private HashMap<String, Integer> initWeatherIconMap() {
        if (mWeatherIcon != null && !mWeatherIcon.isEmpty())
            return mWeatherIcon;
        mWeatherIcon = new HashMap<String, Integer>();
        mWeatherIcon.put("暴雪", R.drawable.biz_plugin_weather_baoxue);
        mWeatherIcon.put("暴雨", R.drawable.biz_plugin_weather_baoyu);
        mWeatherIcon.put("大暴雨", R.drawable.biz_plugin_weather_dabaoyu);
        mWeatherIcon.put("大雪", R.drawable.biz_plugin_weather_daxue);
        mWeatherIcon.put("大雨", R.drawable.biz_plugin_weather_dayu);

        mWeatherIcon.put("多云", R.drawable.biz_plugin_weather_duoyun);
        mWeatherIcon.put("雷阵雨", R.drawable.biz_plugin_weather_leizhenyu);
        mWeatherIcon.put("雷阵雨冰雹",
                R.drawable.biz_plugin_weather_leizhenyubingbao);
        mWeatherIcon.put("晴", R.drawable.biz_plugin_weather_qing);
        mWeatherIcon.put("沙尘暴", R.drawable.biz_plugin_weather_shachenbao);

        mWeatherIcon.put("特大暴雨", R.drawable.biz_plugin_weather_tedabaoyu);
        mWeatherIcon.put("雾", R.drawable.biz_plugin_weather_wu);
        mWeatherIcon.put("小雪", R.drawable.biz_plugin_weather_xiaoxue);
        mWeatherIcon.put("小雨", R.drawable.biz_plugin_weather_xiaoyu);
        mWeatherIcon.put("阴", R.drawable.biz_plugin_weather_yin);

        mWeatherIcon.put("雨夹雪", R.drawable.biz_plugin_weather_yujiaxue);
        mWeatherIcon.put("阵雪", R.drawable.biz_plugin_weather_zhenxue);
        mWeatherIcon.put("阵雨", R.drawable.biz_plugin_weather_zhenyu);
        mWeatherIcon.put("中雪", R.drawable.biz_plugin_weather_zhongxue);
        mWeatherIcon.put("中雨", R.drawable.biz_plugin_weather_zhongyu);
        return mWeatherIcon;
    }

    public Map<String, Integer> getWeatherIconMap() {
        if (mWeatherIcon == null || mWeatherIcon.isEmpty())
            mWeatherIcon = initWeatherIconMap();
        return mWeatherIcon;
    }

    public int getWeatherIcon(String climate) {
        int weatherRes = R.drawable.biz_plugin_weather_qing;
        if (TextUtils.isEmpty(climate))
            return weatherRes;
        String[] strs = {"晴", "晴"};
        if (climate.contains("转")) {// 天气带转字，取前面那部分
            strs = climate.split("转");
            climate = strs[0];
            if (climate.contains("到")) {// 如果转字前面那部分带到字，则取它的后部分
                strs = climate.split("到");
                climate = strs[1];
            }
        }
        if (mWeatherIcon == null || mWeatherIcon.isEmpty())
            mWeatherIcon = initWeatherIconMap();
        if (mWeatherIcon.containsKey(climate)) {
            weatherRes = mWeatherIcon.get(climate);
        }
        return weatherRes;
    }

}
