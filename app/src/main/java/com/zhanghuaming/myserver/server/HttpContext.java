package com.zhanghuaming.myserver.server;

import android.util.Log;

import java.net.Socket;
import java.util.HashMap;

/**
 *
 */
public class HttpContext {

	private static final String TAG="HttpContext";
	/**
	 * 头信息 注意value后面都有\r\n
	 */
	private final HashMap<String, String> requestHeaders;

	public HttpContext() {
		requestHeaders = new HashMap<String, String>();
	}

	/**
	 * 当前客户端socekt
	 */
	private Socket underlySocket;

	public void setUnderlySocket(Socket remotePeer) {
		this.underlySocket = remotePeer;
	}

	public Socket getUnderlySocket() {
		return underlySocket;
	}

	public void addRequestHeader(String headerName, String headerValue) {
		Log.e(TAG,headerName+"-------addRequestHeader is ---"+headerValue);
		requestHeaders.put(headerName, headerValue);
	}

	public String getRequestHeaderValue(String headerName) {
		Log.e(TAG,headerName +"---- getRequestHeaderValue is ---"+requestHeaders.get(headerName));
		return requestHeaders.get(headerName);
	}

}
