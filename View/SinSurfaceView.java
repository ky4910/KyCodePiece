package com.example.kimberjin.viewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SinSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    LoopThread mThread;

    public SinSurfaceView(Context context) {
        super(context);
        initSurfaceView();
    }

    public SinSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurfaceView();
    }

    public SinSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurfaceView();
    }

    private void initSurfaceView() {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        mThread = new LoopThread(surfaceHolder, getContext());
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mThread.isRunning = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mThread.isRunning = false;
        try {
            mThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoopThread extends Thread {

        final SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;
        Paint mPaint;
        Path mPath;
        private int x, y;

        private LoopThread(SurfaceHolder holder, Context context) {
            this.surfaceHolder = holder;
            this.context = context;
            isRunning = false;

            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(5);
            mPath = new Path();
            //路径起始点(0, 100)
            mPath.moveTo(0, 100);
        }

        @Override
        public void run() {
            Canvas cans = null;

            while (isRunning) {
                try {
                    synchronized (surfaceHolder) {
                        drawSomething(cans);
                        x += 1;
                        y = (int)(100 * Math.sin(2 * x * Math.PI / 180) + 400);
                        //加入新的坐标点
                        mPath.lineTo(x, y);
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void drawSomething(Canvas cs) {
            try {
                //获得canvas对象
                cs = surfaceHolder.lockCanvas();
                //绘制背景
                cs.drawColor(Color.WHITE);
                //绘制路径
                cs.drawPath(mPath, mPaint);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (cs != null){
                    //释放canvas对象并提交画布
                    surfaceHolder.unlockCanvasAndPost(cs);
                }
            }
        }
    }
}
