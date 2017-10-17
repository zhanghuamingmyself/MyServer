package com.zhanghuaming.myserver.Weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.zhanghuaming.myserver.MyApplication;
import com.zhanghuaming.myserver.util.SLog;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * Created by zhang on 2017/7/4.
 */

public class HTTPHelper {

    private Context context;


    public HTTPHelper() {
    }



    private static final String TAG = HTTPHelper.class.getSimpleName();

    public static String getWeather(String u) throws IOException {
        OkHttpClient okHttpClient = MyApplication.getInstance().getOkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(u).build();
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        String departJson = response.body().string();
        SLog.i(TAG, "Response   body  is " + departJson);
        SLog.i(TAG, "============================");
        return departJson;

    }

}
