package com.example.admin.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.TimeUnit;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }
    private DrawThread drawThread;
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder());
        drawThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        drawThread.setTowardPoint((int)event.getX());
        return false;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // изменение размеров SurfaceView
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }









    class DrawThread extends Thread {


        private SurfaceHolder surfaceHolder;

        private volatile boolean running = true;
        private Paint backgroundPaint = new Paint();
        private int smileX;
        private int c1 = Color.RED;
        private int c2 = Color.BLUE;


        public void setTowardPoint(int x) {
            smileX = x;
        }
        public DrawThread( SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void requestStop() {
            running = false;
        }

        @Override
        public void run() {
            Canvas canvas1 = surfaceHolder.lockCanvas();
            smileX = canvas1.getWidth() / 2;
            surfaceHolder.unlockCanvasAndPost(canvas1);
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();

                if (canvas != null) {
                    try {
                        backgroundPaint.setColor(c1);
                        canvas.drawRect(0, 0, smileX, canvas.getHeight(), backgroundPaint);
                        backgroundPaint.setColor(c2);
                        canvas.drawRect(smileX, 0, canvas.getWidth() , canvas.getHeight(), backgroundPaint);
                        c1=c1+c2-(c2=c1);
                        Thread.sleep(1000);
                        //TimeUnit.SECONDS.sleep(1);

                    }
                    catch (InterruptedException e) {}

                    finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}