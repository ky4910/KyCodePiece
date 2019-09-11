package com.example.kimberjin.viewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceViewHandWriting extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "SURFACEVIEW_HANDWRITING";

    LoopThread mThread;
    Path mPath;

    public SurfaceViewHandWriting(Context context) {
        super(context);
        initSurfaceView();
    }

    public SurfaceViewHandWriting(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurfaceView();
    }

    public SurfaceViewHandWriting(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurfaceView();
    }

    private void initSurfaceView() {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mPath = new Path();
        mPath.moveTo(0, 100);
        mThread = new LoopThread(surfaceHolder, getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class LoopThread extends Thread {

        final SurfaceHolder surfaceHolder;
        Context context;
        Canvas cans = null;
        boolean isRunning;
        Paint mPaint;

        private LoopThread(SurfaceHolder holder, Context context) {
            this.surfaceHolder = holder;
            this.context = context;
            isRunning = false;

            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(5);
            mPaint.setAntiAlias(true);
        }

        @Override
        public void run() {
            Canvas cans = null;

            while(isRunning){
                try{
                    // 基本书写格式：synchronized（自定义锁）{同步执行代码块}
                    synchronized (surfaceHolder) {
                        cans = surfaceHolder.lockCanvas(null);
                        long start = System.currentTimeMillis();
                        doDraw(cans);
                        long end = System.currentTimeMillis();
                        Thread.sleep(100 - (end - start));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    surfaceHolder.unlockCanvasAndPost(cans);
                }
            }
        }

        private void doDraw(Canvas cs) {
            try {
                cs.drawColor(Color.WHITE);
                cs.drawPath(mPath, mPaint);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
