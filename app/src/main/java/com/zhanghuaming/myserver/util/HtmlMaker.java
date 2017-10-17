package com.zhanghuaming.myserver.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;

import com.morgoo.droidplugin.pm.PluginManager;
import com.zhanghuaming.myserver.plugin.ApkItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2017/8/14.
 */

public class HtmlMaker {
    private static final String TAG = HtmlMaker.class.getSimpleName();
    public static final String htmlHead = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "    <title>Android 管理中心</title>\n" +
            "    <meta name=\"Copyright\" content=\"Douco Design.\"/>\n" +
            "    <style>\n" +
            "\t\ta{\n" +
            "\t\t\ttext-align:center;\n" +
            "\t\t\tpadding:5px;\n" +
            "\t\t\tbackground:#064;\n" +
            "\t\t\tmargin:10px;\n" +
            "\t\t\tdisplay:block;\t\n" +
            "\t\t\tcolor:#000;\n" +
            "\t\t\tsize:20px;\n" +
            "\t\t}\n" +
            "\t</style>\n" +
            "\t<script src=\"js/jquery.js\"></script>\n" +
            "\t<script src=\"js/client.js\"></script>\n" +
            "</head>\n" +
            "<body >\n" +
            "\t<p style=\"text-align:center\"><strong>控制中心</strong></p>";

    public static final String htmlBottom = "\n</body>";


    /**
     *
     * @param context
     * @throws RemoteException
     * 子标签格式：
     * <a href="../包名/index.html">应用名</a>
     */
    public static void reflashFirstHTML(Context context) throws RemoteException {
        List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
        final PackageManager pm = context.getPackageManager();
        String HtmlItems = " ";
        for(PackageInfo info :infos) {
            ApkItem apkItem = new ApkItem(pm, info, info.applicationInfo.publicSourceDir);
            Log.i(TAG, "已安装的插件有" +apkItem.title+"      "+apkItem.packageInfo.packageName);

            HtmlItems+="<a href=\"../"+apkItem.packageInfo.packageName+"/index.html\">"+apkItem.title+"</a>\n";

        }
        String html=htmlHead+HtmlItems+htmlBottom;
        SLog.i(TAG,"引导html内容----"+html);
        FileManager.CleanAndWriteTxtFile(html,FileManager.FIRSTHTMLPATH);
    }
}
