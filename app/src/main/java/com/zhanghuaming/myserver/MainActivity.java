package com.zhanghuaming.myserver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.RecognizeListener;
import com.turing.androidsdk.RecognizeManager;
import com.turing.androidsdk.TTSListener;
import com.turing.androidsdk.TTSManager;
import com.turing.androidsdk.TuringManager;
import com.xys.libzxing.zxing.encoding.EncodingUtils;
import com.zhanghuaming.myserver.Weather.HTTPHelper;
import com.zhanghuaming.myserver.Weather.TimeUtil;
import com.zhanghuaming.myserver.Weather.WeatherInfo;
import com.zhanghuaming.myserver.Weather.XmlPullParseUtil;
import com.zhanghuaming.myserver.config.WebConfiguration;
import com.zhanghuaming.myserver.erweima.BrowerActivty;
import com.zhanghuaming.myserver.erweima.ErWeiMaActivity;
import com.zhanghuaming.myserver.handler.PostDataHandler;
import com.zhanghuaming.myserver.handler.ResourceInAssetsHandler;
import com.zhanghuaming.myserver.handler.UploadImageHandler;
import com.zhanghuaming.myserver.plugin.PluginActivity;
import com.zhanghuaming.myserver.server.SimpleHttpServer;

import com.zhanghuaming.myserver.timer.BaseActivity;
import com.zhanghuaming.myserver.timer.TimerActivity;
import com.zhanghuaming.myserver.timer.settings.SettingsActivity;
import com.zhanghuaming.myserver.util.Constant;
import com.zhanghuaming.myserver.util.GifView;
import com.zhanghuaming.myserver.util.HtmlMaker;
import com.zhanghuaming.myserver.util.IPUtils;
import com.zhanghuaming.myserver.util.MySharedPreferences;
import com.zhanghuaming.myserver.util.OkBack;
import com.zhanghuaming.myserver.util.SLog;
import com.zhanghuaming.myserver.util.VoiceEnableTimer;

import org.json.JSONException;
import org.json.JSONObject;


import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.btn_operator)
    ImageView btnOperator;
    @Bind(R.id.btn_plugin)
    ImageView btnPlugin;
    @Bind(R.id.btn_timer)
    ImageView btnTimer;
    @Bind(R.id.btn_erweima)
    ImageView btnErWeiMa;
    @Bind(R.id.btn_set)
    ImageView btnSet;
    @Bind(R.id.username)
    TextView tvUsername;
    @Bind(R.id.message)
    TextView tvMessage;
    @Bind(R.id.ipv4)
    TextView tvIpv4;
    @Bind(R.id.iv_main)
    ImageView mImageView;
    @Bind(R.id.btn_gif)
    GifView gifView;
    @Bind(R.id.week_today)
    TextView weekTv;
    @Bind(R.id.temperature)
    TextView temperatureTv;
    @Bind(R.id.climate)
    TextView climateTv;
    @Bind(R.id.wind)
    TextView windTv;//天气的显示模块
    @Bind(R.id.funtion_weater)
    View untion_weater;
    @Bind(R.id.weather_img)
    ImageView weatherImg;//显示天气图片


    private SimpleHttpServer shs;
    private boolean isServerRun;


    private int[] gifRaw = {R.raw.mya, R.raw.myb, R.raw.myc, R.raw.myheader, R.raw.myheart, R.raw.brok};
    int gifNum = 0;

    private TTSManager mTtsManager;
    private RecognizeManager mRecognizerManager;
    private TuringManager mTuringManager;


    private VoiceEnableTimer timer;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constant.MSG_SPEECH_START:
                    String say = (String) msg.obj;
                    tvMessage.setText("图灵机器人开始讲话：" + say);
                    mTtsManager.startTTS(say);
                    break;
                case Constant.MSG_RECOGNIZE_RESULT:
                    String sayResult = (String) msg.obj;
                    tvMessage.setText("识别结果：" + sayResult);
                    if (sayResult.startsWith("打开")) {
                        try {
                            PackageManager pm = getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(MySharedPreferences.get(MainActivity.this, sayResult.split("打开")[1]));
                            SLog.i(TAG, "打开包名为" + MySharedPreferences.get(MainActivity.this, sayResult.split("打开")[1]) + "的插件");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            mHandler.sendEmptyMessage(Constant.MSG_RECOGNIZE_START);
                        }
                    } else if(sayResult.startsWith("你好")){
                        tvMessage.setText("暂停识别");
                        mRecognizerManager.stopRecognize();
                        timer.start();
                    }else {
                        mTuringManager.requestTuring(sayResult);
                    }

                    break;
                case Constant.MSG_RECOGNIZE_START:
                    tvMessage.setText("开始识别");
                    mRecognizerManager.startRecognize();
                    break;
            }
        }
    };


    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected int menuResId() {
        return 0;
    }

    private static final int REQUEST_UI = 1;

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        verifyStoragePermissions(this);
        ButterKnife.bind(this);
        gifView.setMovieResource(gifRaw[gifNum]);
        btnOperator.setScaleType(ImageView.ScaleType.CENTER);
        btnPlugin.setScaleType(ImageView.ScaleType.CENTER);
        btnTimer.setScaleType(ImageView.ScaleType.CENTER);

        WebConfiguration webConfiguration = new WebConfiguration();
        webConfiguration.setPort(8088);
        webConfiguration.setMaxParallels(50);
        shs = new SimpleHttpServer(webConfiguration);
        shs.registerResourceHandler(new ResourceInAssetsHandler(this));
        shs.registerResourceHandler(new UploadImageHandler() {

            @Override
            protected void onImageLoaded(String path) {
                showImage(path);
            }
        });
        shs.registerResourceHandler(new PostDataHandler() {

            @Override
            public String showRequestDatas(JSONObject json) {
                return showDatas(json);
            }
        });

        new GetWeatherTask("中山").execute();// 启动更新天气进程
        timer=new VoiceEnableTimer();
        timer.setEnable(true);
        timer.setOkBack(new OkBack() {
            @Override
            public void back(String str) {
                mHandler.sendEmptyMessage(Constant.MSG_RECOGNIZE_START);
            }
        });
        init();
    }

    @OnClick({R.id.funtion_weater,R.id.btn_gif,R.id.btn_timer,R.id.btn_operator,
            R.id.btn_plugin,R.id.btn_erweima,R.id.btn_set})
   public void myClick(View v){
        switch (v.getId()){
            case R.id.btn_timer:
                Intent iTimer = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(iTimer);
                break;
            case R.id.btn_plugin:
                Intent iPlugin = new Intent(MainActivity.this, PluginActivity.class);
                startActivity(iPlugin);
                break;
            case R.id.funtion_weater:
                new GetWeatherTask("中山").execute();// 启动更新天气进程
                break;
            case R.id.btn_gif:
                if (gifNum < gifRaw.length) {
                    gifView.setMovieResource(gifRaw[gifNum]);
                    gifNum++;
                } else {
                    gifNum = 0;
                }
                break;
            case R.id.btn_erweima:
                Intent iTuLin = new Intent(MainActivity.this, ErWeiMaActivity.class);
                startActivity(iTuLin);
                break;
            case R.id.btn_operator:
                if (isServerRun) {
                    isServerRun = false;
                    btnOperator.setImageResource(R.drawable.play);
                    shs.stopAsync();
                    mImageView.setImageResource(R.drawable.erweima);
                } else {
                    isServerRun = true;
                    btnOperator.setImageResource(R.drawable.stop);
                    showIpv4();
                    shs.startAsync();
                    try {
                        HtmlMaker.reflashFirstHTML(MainActivity.this);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_set:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            default:
                break;

        }
    }


    private void showIpv4() {
        IPUtils ip = new IPUtils();
        tvIpv4.setText("http://" + ip.getIpAddress(MainActivity.this) + ":8088/static/server/index.html");
        mydraw();
    }

    protected void showImage(final String path) {
        SLog.d(TAG, "showImage:" + path);
        // UI线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = BitmapFactory.decodeFile(path);
                mImageView.setImageBitmap(bm);
                Toast.makeText(MainActivity.this, "upload success!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String result = "faile";
    protected String showDatas(final JSONObject jsonStr) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "json get name ---" + jsonStr.optString("pkg", "pkg"));
                String pkg = jsonStr.optString("pkg", "pkg");
                String mess = jsonStr.optString("mess", "mess");
                tvUsername.setText(pkg);
                tvMessage.setText(mess);
                result = "success";

                if (pkg.equals("com.zhanghuaming.myserver")) {
                    PackageManager pm = MainActivity.this.getPackageManager();
                    Intent i = pm.getLaunchIntentForPackage(mess);
                    SLog.i(TAG, "打开包名为" + mess + "的插件");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {

                    Intent intent = new Intent();
                    intent.setAction("com.zhanghuaming");
                    intent.putExtra("massger", jsonStr.toString());
                    MainActivity.this.sendBroadcast(intent);
                }
            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        SLog.i(TAG, "服务器已经关闭");
        shs.stopAsync();
        super.onDestroy();
    }

    public void mydraw() {
        IPUtils ip = new IPUtils();
        String url = "http://" + ip.getIpAddress(MainActivity.this) + ":8088/static/server/index.html";
        if (url.equals("")) {
            Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show();
        } else {
            Bitmap bitmap = EncodingUtils.createQRCode(url, 500, 500,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            mImageView.setImageBitmap(bitmap);
        }
    }

    public class GetWeatherTask extends AsyncTask<Void, Void, Integer> {
        private static final String BASE_URL = "http://sixweather.3gpk.net/SixWeather.aspx?city=%s";
        private MyApplication mApplication;
        private String mCity;
        private static final int SCUESS = 0;
        private static final int FAIL = -1;


        public GetWeatherTask(String city) {
            this.mCity = city;
            mApplication = MyApplication.getInstance();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                Log.i(TAG, "正在更新天气");
                String url = String.format(BASE_URL,
                        URLEncoder.encode(mCity, "utf-8"));
                // 最后才执行网络请求
                String netResult = HTTPHelper.getWeather(url);
                if (!TextUtils.isEmpty(netResult)) {
                    SLog.i(TAG, "xml netResult is -----" + netResult);
                    WeatherInfo allWeather = XmlPullParseUtil
                            .parseWeatherInfo(netResult);
                    if (allWeather != null) {
                        mApplication.SetAllWeather(allWeather);

                    }
                    return SCUESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return FAIL;
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case SCUESS:
                    weekTv.setText("今天 " + TimeUtil.getWeek(0, TimeUtil.XING_QI));
                    String climate = mApplication.GetAllWeather().getWeather0();
                    climateTv.setText(climate);
                    temperatureTv.setText(mApplication.GetAllWeather().getTemp0());
                    int weatherIcon = R.drawable.biz_plugin_weather_qing;
                    if (climate.contains("转")) {// 天气带转字，取前面那部分
                        String[] strs = climate.split("转");
                        climate = strs[0];
                        if (climate.contains("到")) {// 如果转字前面那部分带到字，则取它的后部分
                            strs = climate.split("到");
                            climate = strs[1];
                        }
                    }
                    if (mApplication.getWeatherIconMap().containsKey(climate)) {
                        weatherIcon = mApplication.getWeatherIconMap().get(climate);
                    }
                    weatherImg.setImageResource(weatherIcon);
                    windTv.setText(mApplication.GetAllWeather().getWind0());
                    break;
                case FAIL:

                    break;
            }

        }
    }

    private void init() {
        /** 支持百度，需自行去相关平台申请appid，并导入相应的jar和so文件 */
        mRecognizerManager = new RecognizeManager(this, Constant.BD_APIKEY, Constant.BD_SECRET);
        mTtsManager = new TTSManager(this, Constant.BD_APIKEY, Constant.BD_SECRET);
        mRecognizerManager.setVoiceRecognizeListener(myVoiceRecognizeListener);
        mTtsManager.setTTSListener(myTTSListener);
        mTuringManager = new TuringManager(this, Constant.TURING_APIKEY,
                Constant.TURING_SECRET);
        mTuringManager.setHttpRequestListener(myHttpConnectionListener);
        mTtsManager.startTTS("你好啊");
    }

    /**
     * 网络请求回调
     */
    HttpRequestListener myHttpConnectionListener = new HttpRequestListener() {

        @Override
        public void onSuccess(String result) {
            if (result != null) {
                try {
                    SLog.d(TAG, "result" + result);
                    JSONObject result_obj = new JSONObject(result);
                    if (result_obj.has("text")) {
                        SLog.d(TAG, result_obj.get("text").toString());
                        mHandler.obtainMessage(Constant.MSG_SPEECH_START,
                                result_obj.get("text")).sendToTarget();
                    }
                } catch (JSONException e) {
                    SLog.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onFail(int code, String error) {
            Log.d(TAG, "onFail code:" + code + "|error:" + error);
            mHandler.obtainMessage(Constant.MSG_SPEECH_START, "网络慢脑袋不灵了").sendToTarget();
        }
    };

    /**
     * 语音识别回调
     */
    RecognizeListener myVoiceRecognizeListener = new RecognizeListener() {

        @Override
        public void onVolumeChange(int volume) {
            // 仅讯飞回调
        }

        @Override
        public void onStartRecognize() {
            // 仅针对百度回调
        }

        @Override
        public void onRecordStart() {

        }

        @Override
        public void onRecordEnd() {

        }

        @Override
        public void onRecognizeResult(String result) {
            Log.d(TAG, "识别结果：" + result);
            if (result == null) {
                mHandler.sendEmptyMessage(Constant.MSG_RECOGNIZE_START);
                return;
            }
            mHandler.obtainMessage(Constant.MSG_RECOGNIZE_RESULT, result).sendToTarget();
        }

        @Override
        public void onRecognizeError(String error) {
            Log.e(TAG, "识别错误：" + error);
            mHandler.sendEmptyMessage(Constant.MSG_RECOGNIZE_START);
        }
    };

    /**
     * TTS回调
     */
    TTSListener myTTSListener = new TTSListener() {

        @Override
        public void onSpeechStart() {
            Log.d(TAG, "onSpeechStart");
        }

        @Override
        public void onSpeechProgressChanged() {

        }

        @Override
        public void onSpeechPause() {
            Log.d(TAG, "onSpeechPause");
        }

        @Override
        public void onSpeechFinish() {
            Log.d(TAG, "onSpeechFinish");
            mHandler.sendEmptyMessage(Constant.MSG_RECOGNIZE_START);
        }

        @Override
        public void onSpeechError(int errorCode) {
            Log.d(TAG, "onSpeechError：" + errorCode);
            mHandler.sendEmptyMessage(Constant.MSG_RECOGNIZE_START);
        }

        @Override
        public void onSpeechCancel() {
            Log.d(TAG, "TTS Cancle!");
        }
    };
}
