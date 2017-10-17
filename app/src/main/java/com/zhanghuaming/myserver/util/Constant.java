package com.zhanghuaming.myserver.util;

import android.os.Environment;

/**
 * Created by zhang on 2017/8/14.
 */

public class Constant {
    private static final String appPath ="/Myserver";
    public static final String assetsPath = Environment.getExternalStorageDirectory().getPath() +appPath+ "/serverpath/";
    public static final String pluginPath = Environment.getExternalStorageDirectory().toString() +appPath+ "/plugin/";
    public static final String MarketPath ="http://192.168.1.110:8080/gmsystem";
    /**
     * 返回结果，开始说话
     */
    public static final int MSG_SPEECH_START = 0;
    /**
     * 开始识别
     */
    public static final int MSG_RECOGNIZE_RESULT = 1;
    /**
     * 开始识别
     */
    public static final int MSG_RECOGNIZE_START = 2;

    /**
     * 申请的turing的apikey（测试使用）
     **/
    public static final String TURING_APIKEY = "d975f8141aa550cea27b7f48dd50c48d";
    /**
     * 申请的secret（测试使用）
     **/
    public  static final String TURING_SECRET = "4145a1cb5f92901b";
    // 百度key（测试使用）
    public static final String BD_APIKEY = "uj2OWZj2sRz0SETE7xc5UUOl";
    // 百度screte（测试使用）
    public static final String BD_SECRET = "sui5Fg2oMGNvoDEeVXTonxlCysmK5PgK";
}
