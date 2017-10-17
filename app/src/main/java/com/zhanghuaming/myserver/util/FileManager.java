package com.zhanghuaming.myserver.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by zhang on 2017/5/10.
 */

public class FileManager {

    public static final String FIRSTHTMLPATH= Constant.assetsPath+"server/index.html";

    public static InputStream getFile(String filePath)
    {
        if(filePath == null)
        {
            return  null;
        }
        try {
            FileInputStream file = new FileInputStream(filePath);
            byte[] bytes = new byte[10240];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream inputStream = null;
            int n= -1;
            while((n = file.read(bytes)) != -1)
            {
                baos.write(bytes,0,n);
            }
            byte[] aArray =baos.toByteArray();
            inputStream = new ByteArrayInputStream(aArray);
            baos.close();
            file.close();
            return inputStream;
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }




    private static final String TAG = FileManager.class.getSimpleName();


    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            SLog.d(TAG, "The File is Directory");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                        SLog.i(TAG, "读到一行：" + line);
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                SLog.d(TAG, "The File doesn't not exist.");
            } catch (IOException e) {
                SLog.d(TAG, e.getMessage());
            }
        }
        return content;
    }

    public static void WriteTxtFile(String strcontent, String strFilePath) {
        //每次写入时，都换行写
        String strContent = strcontent + "\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                SLog.d(TAG, "Create the file:" + strFilePath);
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            SLog.e(TAG, "Error on write File.");
        }
    }

    public static void CleanAndWriteTxtFile(String strcontent, String strFilePath) {
        String strContent = strcontent ;
        FileOutputStream out = null;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                SLog.d(TAG, "Create the file:" + strFilePath);
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            out.write(strContent.getBytes());
            out.flush();
        } catch (Exception e) {
            SLog.e(TAG, "Error on write File.");
        }
    }
}
