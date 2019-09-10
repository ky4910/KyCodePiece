package com.example.kimberjin.viewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceViewCircle extends SurfaceView implements SurfaceHolder.Callback{

    LoopThread thread;

    public SurfaceViewCircle(Context context) {
        super(context);
        initSurface();
    }

    public SurfaceViewCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurface();
    }

    public SurfaceViewCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurface();
    }

    public void initSurface() {
        SurfaceHolder mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        thread = new LoopThread(mSurfaceHolder, getContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.isRunning = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class LoopThread extends Thread {

        final SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;
        float radius = 10f;
        Paint paint;

        private LoopThread(SurfaceHolder surfaceHolder, Context context) {
            this.surfaceHolder = surfaceHolder;
            this.context = context;
            isRunning = false;

            paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void run() {

            Canvas cans = null;

            while(isRunning){
                try{
                    // 基本书写格式：synchronized（自定义锁）{同步执行代码块}
                    synchronized (surfaceHolder) {
                        cans = surfaceHolder.lockCanvas(null);
                        doDraw(cans);
                        //通过它来控制帧数执行一次绘制后休息50ms
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    surfaceHolder.unlockCanvasAndPost(cans);
                }
            }
        }

        private void doDraw(Canvas cs) {
            // Clear Screen
            cs.drawColor(Color.WHITE);

            cs.translate(200, 200);
            cs.drawCircle(0, 0, radius++, paint);

            if (radius > 100) {
                radius = 10f;
            }
        }
    }
}
