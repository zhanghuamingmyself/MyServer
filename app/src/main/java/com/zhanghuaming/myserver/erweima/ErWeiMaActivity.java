package com.zhanghuaming.myserver.erweima;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;
import com.zhanghuaming.myserver.R;


public class ErWeiMaActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_erweima);

    }
    public  void mysanner(View v)
    {
        Button button=(Button)findViewById(R.id.bt1);
        //开启摄像头权限
        int i= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(i!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},2);
            return;
        }
        Intent intent = new Intent(ErWeiMaActivity.this,
                CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         TextView textView=(TextView)findViewById(R.id.tv2);
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if ( resultCode == RESULT_OK) {
                Bundle bundle=data.getExtras();
                String content = bundle.getString("result");
               // Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
             textView.setText("解码结果： \n" + content);
               // qrCodeImage.setImageBitmap(bitmap);
            Intent i = new Intent(ErWeiMaActivity.this,BrowerActivty.class);
            i.putExtra("url",content);
            startActivity(i);
            finish();
            }
    }
    //当用户选择是否允许
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==2){
            //证明申请到权限
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                startActivityForResult(new Intent(ErWeiMaActivity.this, CaptureActivity.class),0);
            }
        }
    }
}
