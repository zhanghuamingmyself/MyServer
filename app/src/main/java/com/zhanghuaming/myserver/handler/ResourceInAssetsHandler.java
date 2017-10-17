package com.zhanghuaming.myserver.handler;

import android.content.Context;



import com.zhanghuaming.myserver.handler.itf.IResourceUriHandler;
import com.zhanghuaming.myserver.server.HttpContext;
import com.zhanghuaming.myserver.util.Constant;
import com.zhanghuaming.myserver.util.FileManager;
import com.zhanghuaming.myserver.util.SLog;
import com.zhanghuaming.myserver.util.StreamToolkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 静态的资源
 * http://192.168.1.104:8088/static/server/index.html
 */
public class ResourceInAssetsHandler implements IResourceUriHandler {

	private static final String TAG = "ResourceInAssetsHandler";
	private String acceptPrefix = "/static/";
	private Context context;
	
	public ResourceInAssetsHandler(Context context) {
		this.context=context;
	}

	@Override
	public boolean accept(String uri) {
		return uri.startsWith(acceptPrefix);
	}

	@Override
	public void handler(String uri, HttpContext httpContext) {
		StringBuffer path = new StringBuffer(Constant.assetsPath);
		int startIndex=acceptPrefix.length();
		String assetsPath=uri.substring(startIndex);
		path.append(assetsPath);

		if(new File(path.toString()).exists()) {
			SLog.i(TAG, "assetsPath:" + assetsPath + "Loca is -----" + path.toString());
		}else {
			SLog.i(TAG, "assetsPath:" + assetsPath + "file is not exists -----" + path.toString());
		}
		try {
			InputStream fis =FileManager.getFile(path.toString());
		    byte[] raw= StreamToolkit.readRawFromStream(fis);
		    fis.close();
		    OutputStream nos=httpContext.getUnderlySocket().getOutputStream();
		    PrintStream printer = new PrintStream(nos);
		    printer.write(raw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
