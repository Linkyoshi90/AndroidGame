package com.example.josha.anothergame;


import android.annotation.SuppressLint;
import android.graphics.Canvas;

/**
 * Created by Josha on 05.08.2016.
 */

public class GameLoopThread extends Thread{

    static final long FPS = 20;
    private GameView theView;
    private boolean isRunning = false;

    public GameLoopThread(GameView theView){
        this.theView = theView;
    }

    public void setRunning(boolean run){
        isRunning = run;
    }

    @Override
    public void run() {

        long TPS = 1000 / FPS;
        long startTime, sleepTime;
        while (isRunning) {

            Canvas theCanvas = null;
            startTime = System.currentTimeMillis();

            try {

                theCanvas = theView.getHolder().lockCanvas();
                synchronized (theView.getHolder()) {

                    theView.onDraw(theCanvas);

                }

            }
            finally {

                if (theCanvas != null) {

                    theView.getHolder().unlockCanvasAndPost(theCanvas);

                }

            }

            sleepTime = TPS - (System.currentTimeMillis() - startTime);

            try {

                if (sleepTime>0)
                    sleep(sleepTime);
                else
                    sleep(10);
            }

            catch (Exception e){

            }

        }

    }

}
