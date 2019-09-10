package com.example.kimberjin.viewtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class AnimateViewActivity  extends Activity{
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(new AnimateView(this));

		/*
        ImageView imgView = (ImageView)findViewById(R.id.imgView);

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tmpPath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Pictures" + File.separator + "AndroidBG3.jpg";
        Log.e("KIMBER", tmpPath);

        Bitmap btp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Pictures" + File.separator + "AndroidBG3.jpg");
        imgView.setImageBitmap(btp);
		*/
    }

    class AnimateView extends View {
        float radius = 10;
        Paint paint;

        public AnimateView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            invalidate();

            canvas.translate(200, 200);
            Log.e("CANVAS LOG", "onDraw called!");
            canvas.drawCircle(0, 0, radius++, paint);

            if (radius > 200) {
                radius = 10;
            }
        }
    }
}
