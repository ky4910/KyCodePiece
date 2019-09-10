package com.example.kimberjin.viewtest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static final String TAG = "KIMBER";

    // 是否绘制
    private volatile boolean mIsDrawing;
    // SurfaceView 控制器
    private SurfaceHolder mSurfaceHolder;
    // 画笔
    private Paint mPaint;
    // 画布
    private Canvas mCanvas;
    // 独立的线程
    private Thread mThread;
    // 绘制的图像
    private Bitmap mBitmap;

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMySurface();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initMySurface();
    }

    public void initMySurface(){
        mSurfaceHolder = getHolder();
        // Register the callback event
        mSurfaceHolder.addCallback(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "on surfaceCreated!");
        mThread = new Thread(this, "MySurfaceView");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG, "format: " + i + " width: " + i1 + " height: " + i2);
        try {
            mBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()
                    + File.separator + "Pictures" + File.separator + "Androidbg.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsDrawing = true;
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "on surfaceDestroyed!");
        mIsDrawing = false;
        mThread.interrupt();
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            Log.e(TAG, "draw canvas");
            // 锁定画布，获得画布对象
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {
                try {
                    draw();
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 解锁画布，提交绘制，显示内容
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }

    private void draw() {
        mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }
}
