package com.zhanghuaming.myserver.plugin;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhanghuaming.myserver.R;
import com.zhanghuaming.myserver.util.Constant;
import com.zhanghuaming.myserver.util.HTTPHelper;
import com.zhanghuaming.myserver.util.OkBack;
import com.zhanghuaming.myserver.util.SLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MarketListFragment extends ListFragment {
    private static final String TAG = MarketListFragment.class.getSimpleName();
    private ArrayAdapter<PluginApp> adapter;
    final Handler handler = new Handler();
    private Context context;

    public MarketListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        adapter = new ArrayAdapter<PluginApp>(context, 0) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.market_item, null);
                }
                PluginApp item = getItem(position);
                SLog.i(TAG,"数据消息"+item.toString());
                SLog.i(TAG,"图片url"+Constant.MarketPath + "/file/findbypath.do?path=" +item.getAppName()+"\\"+ item.getIconName());
                ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
                Glide.with(context)
                        .load(Constant.MarketPath + "/file/findbypath.do?path=" +item.getAppName()+"\\"+ item.getIconName())
                        .placeholder(R.mipmap.ic_launcher)
                        .centerCrop()
                        .transform(new CircleTransformation(context))
                        .into(icon);
                TextView title = (TextView) convertView.findViewById(R.id.tv1);
                title.setText(item.getAppName());
                SLog.i(TAG,"应用名字为"+item.getAppName());

                TextView version = (TextView) convertView.findViewById(R.id.tv2);
                version.setText(item.getPluginVersion());

                TextView info = (TextView) convertView.findViewById(R.id.tv3);
                info.setText(item.getInfo());

                Button btn = (Button) convertView.findViewById(R.id.button2);
                btn.setText("下载咯");
                btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onListItemClick(getListView(), view, position, getItemId(position));
                    }
                });

                return convertView;
            }
        };

    }


    boolean isViewCreated = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        setEmptyText("没有在服务器找到apk");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(null);

        //获取数据
        startLoadInner();
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        super.onDestroyView();
    }

    @Override
    public void setListShown(boolean shown) {
        if (isViewCreated) {
            super.setListShown(shown);
        }
    }

    List<PluginApp> list;
    private void startLoadInner() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setListShown(true);
            }
        });
        if (!isViewCreated) {
            return;
        }

        HTTPHelper httpHelper = new HTTPHelper(HTTPHelper.renewIP(context)+"/pluginapp/findall.do");
        httpHelper.setBack(new OkBack() {
            @Override
            public void back(String str) {
                Gson gson = new Gson();
                Type lt = new TypeToken<List<PluginApp>>() {
                }.getType();

                try {
                     list = gson.fromJson(str, lt);
                }catch (Exception e){
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        SLog.i(TAG,"请求地址为-----"+HTTPHelper.renewIP(context)+"/pluginapp/findall.do");
        httpHelper.doGet();
    }


    public void onListItemClick(ListView l, View v, int position, long id) {
        final PluginApp item = adapter.getItem(position);
        if (v.getId() == R.id.button2) {
            Toast.makeText(context,"正在下载"+item.getAppName(),Toast.LENGTH_SHORT).show();
          //  SLog.i(TAG,"apk下载----"+Constant.MarketPath + "/file/findbypath.do?path=" +item.getAppName()+"\\"+ item.getAppName()+".apk"+"文件名-----"+item.getAppName()+".apk"+"文件夹-----"+Constant.pluginPath);
            HTTPHelper.downLoadFile(Constant.MarketPath + "/file/findbypath.do?path=" + item.getAppName() + "\\" + item.getAppName() + ".apk", item.getAppName() + ".apk", Constant.pluginPath);//下载apk部分
            HTTPHelper.downLoadFile(Constant.MarketPath + "/file/findbypath.do?path=" + item.getAppName() + "\\" + item.getPackageName() + ".zip", item.getPackageName() + ".zip", Constant.assetsPath);//zip部分

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
