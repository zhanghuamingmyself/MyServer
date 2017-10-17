package com.zhanghuaming.myserver.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.zhanghuaming.myserver.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    public HTTPHelper(String URL) {
        this.URL = URL;
    }

    public HTTPHelper() {
    }

    private OkBack back;

    public void setBack(OkBack back) {
        this.back = back;
    }

    private static final String TAG = HTTPHelper.class.getSimpleName();
    private String URL = "http://192.168.3.100:8080/gmsystem/nurser/findall.do";
    private static String baseURL = Constant.MarketPath;

    public void doGet() {
        OkHttpClient okHttpClient = MyApplication.getInstance().getOkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(URL).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SLog.e(TAG, "ok  get fail");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                String departJson = response.body().string();
                SLog.i(TAG, "Response   body  is " + departJson);
                SLog.i(TAG, "============================");
                SLog.i(TAG, " <><><><><:::  " + departJson);

                back.back(departJson);
            }
        });
    }


    public void doPostFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            SLog.e(TAG, "file is no exists");
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(URL).post(requestBody).build();
        Call call = MyApplication.getInstance().getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG, "ok  get fail");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SLog.i(TAG, "Response   body  is " + response.body().string());
            }
        });
    }

    public static String renewIP(Context context) {
        baseURL = Constant.MarketPath;
        return baseURL;
    }

    /**
     * 判断网络连接状态
     *
     * @param context
     * @return
     */

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 登录
     *
     * @param context
     * @param name
     */

    public void login(Context context, String name) {
        SLog.i(TAG, "正在请求登录");
        //创建okHttpClient对象
        OkHttpClient mOkhHttpClient = MyApplication.getInstance().getOkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .build();

        //创建一个Request
        final Request request = new Request.Builder()
                .url(HTTPHelper.renewIP(context) + "/user/findbyname.do")
                .post(requestBody)
                .build();

        //new call
        Call call = mOkhHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "提交登录请求失败！");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String htmlStr = response.body().string();
                    Log.d(TAG, htmlStr);
                    back.back(htmlStr);
                }
            }
        });
    }


    public void post_file(final String url, final Map<String, Object> map, File file) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("headImage", file.getName(), body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url("请求地址").post(requestBody.build()).tag(context).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("lfq", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Log.i("lfq", response.message() + " , body " + str);

                } else {
                    Log.i("lfq", response.message() + " error : body " + response.body().string());
                }
            }
        });

    }


    /**
     * 下载文件
     *
     * @param fileUrl     文件url
     * @param destFileDir 存储目标目录
     */
    public synchronized static void downLoadFile(String fileUrl, String f, final String destFileDir) {
        if (!new File(destFileDir).exists())
            new File(destFileDir).mkdirs();
        final File file = new File(destFileDir + "/" + f);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        SLog.i(TAG, "正在下载文件---" + file.getPath());
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = MyApplication.getInstance().getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SLog.i(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        SLog.i(TAG, "current------>" + current);
                    }
                    fos.flush();
                } catch (IOException e) {
                    SLog.i(TAG, e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        SLog.i(TAG, e.toString());
                    }
                }
            }
        });
    }

}
