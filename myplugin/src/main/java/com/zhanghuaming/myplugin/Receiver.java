package com.zhanghuaming.myplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2017/8/14.
 */

public class Receiver extends BroadcastReceiver {

    private static final String TAG = Receiver.class.getSimpleName();
    private static List<MessFuntion> messFuntion;

    public static void addHander(MessFuntion m) {
        if (messFuntion == null) {
            messFuntion = new ArrayList<MessFuntion>();
            messFuntion.add(m);
        } else {
            messFuntion.add(m);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String mess = intent.getExtras().getString("massger");
        Log.i(TAG, "接收到:" + mess);

        Log.i(TAG,"我的包名是======"+Receiver.class.getPackage().getName());
        MessBeen messBeen = new MessBeen();
        Gson g = new Gson();
        messBeen = g.fromJson(mess, MessBeen.class);
        if (messBeen.getPkg().equals(Receiver.class.getPackage().getName())) {
            if (messFuntion != null) {
                for (MessFuntion m : messFuntion) {
                    m.doHandler(messBeen);
                }
            } else {
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }

    }

}

