package com.zhanghuaming.myserver.erweima;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.zhanghuaming.myserver.R;
import com.zhanghuaming.myserver.timer.BaseActivity;

/**
 * Created by zhang on 2017/10/13.
 */

public class BrowerActivty extends BaseActivity {
    WebView myWebView;
    @Override
    protected int layoutResId() {
        return R.layout.activity_brower;
    }

    @Override
    protected int menuResId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brower);

        Intent i = getIntent();
        WebView myWebView= (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(i.getStringExtra("url"));
    }

}
