package com.zhanghuaming.myserver.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读流工具类
 */
public class StreamToolkit {

	private static final String TAG = "StreamToolkit";
	/**
	 * 从输入流过滤CRLF读取一行
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static final String readLine(InputStream inputStream)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		int c1 = 0;
		int c2 = 0;
		while (c2 != -1 && !(c1 == '\r' && c2 == '\n')) {
			c1 = c2;
			c2 = inputStream.read();
			sb.append((char) c2);
			System.out.print((char)c2);
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}
	public static final String readBody(InputStream inputStream)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		int c1 = 0;
		while (c1 != -1) {
			c1 = inputStream.read();
			sb.append((char) c1);
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}
	public static byte[] readRawFromStream(InputStream fis) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		int nReaded;
		try {
			while ((nReaded = fis.read(buffer)) > 0) {
				bos.write(buffer, 0, nReaded);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	public static List<Map<String, Object>> ParseFruitList(String jsondata) {
		//初始化一个链表Map
		List<Map<String, Object>> fruit_list = new ArrayList<Map<String,Object>>();
		try {
			//构造一个JSONObject对象，参数是网络请求返回的数据
			JSONObject jo = new JSONObject(jsondata);
			//获得其中的list项，返回一个JSONArray数组
			JSONArray jo_fruit_array = jo.getJSONArray("list");

			//获得其中的sum
			int sum = jo.getInt("sum");
			Log.d(TAG, "fruit count: " + sum);

			//获得其中的result
			int result = jo.getInt("result");
			if(result != 0){
				//获得其中的message
				String message = jo.getString("message");
				Log.e(TAG, "Request failed: " + message);
				return null;
			}

			//遍历数组
			for(int i = 0; i < sum; i++){
				//获得数组中的每一个JSONObject
				JSONObject jo_fruite = jo_fruit_array.getJSONObject(i);
				//获得其中的index
				int index = jo_fruite.getInt("index");
				//获得其中的image
				String image = jo_fruite.getString("image");
				//获得其中的name
				String name = jo_fruite.getString("name");
				Log.d(TAG, "index: " + index);
				Log.d(TAG, "image: " + image);
				Log.d(TAG, "name: " + name);

				//初始化Map
				Map<String, Object> item = new HashMap<String, Object>();
				//Map中放入一个Key-value
				item.put("image", image);
				item.put("title", name);
				item.put("index", Integer.valueOf(index));
				//Map数据增加到链表
				fruit_list.add(item);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fruit_list;
	}
}
