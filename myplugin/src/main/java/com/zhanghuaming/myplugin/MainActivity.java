package com.zhanghuaming.myplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements MessFuntion{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Boolean isPlay = true;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Receiver.addHander(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoView.isPlaying())
                videoView.pause();
                else videoView.start();
            }
        });
        initVideo();
    }
    void initVideo() {
        //本地的视频  需要在手机SD卡根目录添加一个 fl1234.mp4 视频
        String videoUrl1 = Environment.getExternalStorageDirectory().getPath() + "/1.mp4";

        Uri uri = Uri.parse(videoUrl1);
        videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setVisibility(View.VISIBLE);
        //videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();
    }

    @Override
    public String doHandler(MessBeen been) {
        Log.i(TAG,"收到的json"+been.toString());
        if(been.getMess().equals("back")){
            finish();
        }else {
            if (videoView.isPlaying())
                videoView.pause();
            else videoView.start();
        }
        return null;
    }
}
