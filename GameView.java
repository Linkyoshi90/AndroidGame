package com.example.josha.anothergame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {

    private Bitmap backgroundred, backgroundblue, backgroundyellow, backgroundgreen;
    private Bitmap bmp;
    private Bitmap bmpBlueTemp, bmpRedTemp, bmpGreenTemp, bmpYellowTemp;
    private Bitmap livesPicture;
    private boolean createSprites = true;
    private boolean spriteHit = false;
    private boolean timeIsUp=false;
    private boolean transparency=false;
    private float density;
    private GameActivity theGameActivity = new GameActivity();
    private GameLoopThread theGameLoopThread;
    private int currentColorNum;
    private final int heartChance = 25;
    private int score = 0;
    private int secondsLeft;
    //private int lives = 4;
    private List<Sprite> heartSprites = new ArrayList<Sprite>();
    private List<Sprite> spriteList = new ArrayList<Sprite>();
    private List<Integer> spriteListNum = new ArrayList<Integer>();
    private List<TempSprite> temps = new ArrayList<TempSprite>();
    private long lastClick;
    private long playTime = 60000;
    private long starttime = 0;
    private long timeleft;
    private Paint paintRed, paintBlue, paintGreen, paintYellow;
    private Paint currentColor;
    private String scoreString;
    private String livesString;
    private SurfaceHolder surfaceHolder;
    private int createHeart;
    Random rnd = new Random(System.currentTimeMillis());
    private final RectF rectF = new RectF();

    public GameView(Context context) {

        super(context);
        livesPicture = BitmapFactory.decodeResource(getResources(), R.drawable.lives1);
        Random rnd = new Random();
        bmpBlueTemp = BitmapFactory.decodeResource(getResources(), R.drawable.tmpblue);
        bmpGreenTemp = BitmapFactory.decodeResource(getResources(), R.drawable.tmpgreen);
        bmpRedTemp = BitmapFactory.decodeResource(getResources(), R.drawable.tmpred);
        bmpYellowTemp = BitmapFactory.decodeResource(getResources(), R.drawable.tmpyellow);
        theGameActivity = (GameActivity) context;
        backgroundblue = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundblue);
        backgroundgreen = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundgreen);
        backgroundred = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundred);
        backgroundyellow = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundyellow);
        setColors();
        currentColorNum = rnd.nextInt(4);
        theGameLoopThread = new GameLoopThread(this);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {

                theGameLoopThread.setRunning(false);
                boolean retry = true;

                while (retry) {

                    try {

                        theGameLoopThread.interrupt();
                        theGameLoopThread.join();
                        retry = false;

                    }

                    catch (InterruptedException e) {

                    }

                }

            }

            public void surfaceCreated(SurfaceHolder holder) {

                theGameLoopThread.setRunning(true);

                if (theGameLoopThread.getState() == theGameLoopThread.getState().NEW) {

                    theGameLoopThread.start();

                }

            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

        });

    }

    public void drawTime(Canvas canvas, Paint paint, int yTextPos){

        int theSeconds, theMinutes;
        String minuteString, secondsString;
        int x4 = (int) (30 * getDensity());
        long timegone = System.currentTimeMillis() - starttime;
        timeleft = playTime - timegone + theGameActivity.getPauseTime();
        secondsLeft = (int) (timeleft / 1000);
        theSeconds = (int) (secondsLeft % 60);
        theMinutes = (int) (secondsLeft / 60);
        minuteString = String.valueOf(theMinutes);
        secondsString = String.valueOf(theSeconds);
        final String theTime;

        if (theSeconds < 10) {

            theTime = minuteString + ":0" + secondsString;
            canvas.drawText(theTime, x4, yTextPos, paint);

        }

        else if (theSeconds >= 10) {

            theTime = minuteString + ":" + secondsString;
            canvas.drawText(theTime, x4, yTextPos, paint);

        }

        if (timeleft <= 0) {

            timeIsUp = true;

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawColor(Color.DKGRAY);

        drawBackground(canvas);

        if (createSprites == true) {

            initialSprites();
            starttime = System.currentTimeMillis();

        }

        for (int i = temps.size() - 1; i >= 0; i--) {

            temps.get(i).draw(canvas);

        }

        for (Sprite sprite : spriteList) {

            sprite.onDraw(canvas);

        }

        for (Sprite sprite : heartSprites) {

            sprite.onDraw(canvas);

        }

        /*

        if (currentColorNum == 0) {

            drawLines(paintBlue, canvas);

        }

        else if (currentColorNum == 1) {

            drawLines(paintRed, canvas);

        }

        else if (currentColorNum == 2) {

            drawLines(paintGreen, canvas);

        }

        else if (currentColorNum == 3) {

            drawLines(paintYellow, canvas);

        }

        */

        final int fontSize = (int) (25 * density);
        int yTextPos = (int) (25 * density);
        Typeface font = Typeface.create("Arial", Typeface.NORMAL);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(font);
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
        scoreString = String.valueOf(score);
        int x = (canvas.getWidth() * 5 / 7);
        final String text = "Score: " + scoreString;
        canvas.drawText(text, x, yTextPos, paint);
        //drawLives(canvas, paint);

        if(transparency){

            drawTransparency(canvas);

        }

        drawTime(canvas, paint, yTextPos);
        checkIfTimeOver();

    }

    private void createSprite(int index) {

        Bitmap bmp = null;

        switch (index) {

            case 0:
                bmp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.alienspriteblue);
                break;

            case 1:
                bmp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.alienspritered);
                break;

            case 2:
                bmp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.alienspritegreen);
                break;

            case 3:
                bmp = BitmapFactory.decodeResource(getResources(),
                        R.drawable.alienspriteyellow);
                break;

        }

        Sprite sprite = new Sprite(this, bmp);
        spriteList.add(sprite);
        spriteListNum.add(index);

    }

    private void initialSprites() {

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 3; j++)
                createSprite(i);

        }

        createSprites = false;

    }

    private void rndCreateSprite() {

        Random rnd = new Random(System.currentTimeMillis());
        int i = rnd.nextInt(4);
        createSprite(i);

    }

    private Sprite createHeartSprite(int resource) {

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resource);
        return new Sprite(this, bmp);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (System.currentTimeMillis() - lastClick > 300) {

            lastClick = System.currentTimeMillis();

            synchronized (getHolder()) {

                if (!spriteHit) {

                    for (int i = heartSprites.size() - 1; i >= 0; i--) {

                        Sprite sprite = heartSprites.get(i);

                        if (sprite.isTouched(event.getX(), event.getY())) {

                            score++;
                            playTime += 5000;

                            if (theGameActivity.getSoundToggleState()) {

                                theGameActivity.playPoint();

                            }

                            heartSprites.remove(sprite);
                            spriteHit = true;

                        }

                    }

                }

                if (!spriteHit) {

                    for (int i = spriteList.size() - 1; i >= 0; i--) {

                        createHeart = rnd.nextInt(heartChance);
                        Sprite sprite = spriteList.get(i);

                        if (sprite.isTouched(event.getX(), event.getY())) {

                            if (currentColorNum == spriteListNum.get(i)) {

                                score++;

                                if (theGameActivity.getSoundToggleState()) {

                                    theGameActivity.playPoint();

                                }

                                if ((score % 5 == 0) && (score != 0)) {

                                    playTime += 3000;

                                }

                            }

                            else {

                                // lives--;
                                playTime -= 5000;

                                if (theGameActivity.getVibrationToggleState()) {

                                    theGameActivity.vibrator();

                                }

                                if (theGameActivity.getSoundToggleState()) {

                                    theGameActivity.playMinusPoint();

                                }

                                // if (lives == 0) {
                                // 	theGameActivity.onGameOver();
                                // }

                            }

                            // if (score == 3)
                            // lives++;

                            if (!(createHeart == 1)) {

                                rndCreateSprite();

                            }

                            if (createHeart == 1) {

                                heartSprites.add(createHeartSprite(R.drawable.heartsprite));

                            }

                            checkColorTouched(i,event.getX(),event.getY());
                            removeSprite(i);
                            changeColor();
                            spriteHit = true;
                            break;

                        }

                    }

                }

            }

            spriteHit = false;

        }

        return true;

    }

    private void removeSprite(int index) {

        spriteList.remove(index);
        spriteListNum.remove(index);

    }

    public void setColors() {

        Paint paintRed = new Paint();
        paintRed.setARGB(255, 236, 27, 36);
        this.paintRed = paintRed;
        Paint paintBlue = new Paint();
        paintBlue.setARGB(255, 36, 72, 204);
        this.paintBlue = paintBlue;
        Paint paintGreen = new Paint();
        paintGreen.setARGB(255, 34, 177, 76);
        this.paintGreen = paintGreen;
        Paint paintYellow = new Paint();
        paintYellow.setARGB(255, 255, 242, 0);
        this.paintYellow = paintYellow;

    }

    public void drawBackground(Canvas canvas){

        rectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        if (currentColorNum == 0) {

            canvas.drawBitmap(backgroundblue, null, rectF, null);
            currentColor=paintBlue;

        }

        else if (currentColorNum == 1) {

            canvas.drawBitmap(backgroundred, null, rectF, null);
            currentColor=paintRed;

        }

        else if (currentColorNum == 2) {

            canvas.drawBitmap(backgroundgreen, null, rectF, null);
            currentColor=paintGreen;

        }

        else if (currentColorNum == 3) {

            canvas.drawBitmap(backgroundyellow, null, rectF, null);
            currentColor=paintYellow;

        }

    }

    /*

    public void drawLines(Paint lineColor, Canvas canvas) {

        int lineWidth = (int) (10 * density);
        int screenHeight = getHeight();
        int screenWidth = getWidth();
        canvas.drawRect(0, 0, lineWidth, getHeight(), lineColor);
        canvas.drawRect(0, getHeight() - lineWidth, screenWidth, screenHeight,
                lineColor);
        canvas.drawRect(screenWidth - lineWidth, 0, screenWidth, screenHeight,
                lineColor);
        currentColor = lineColor;

    }

    */

    public void changeColor() {

        Random rnd = new Random();
        int index = rnd.nextInt(spriteListNum.size());
        this.currentColorNum = spriteListNum.get(index);

        switch (index) {

            case 0:
                currentColor = paintBlue;
                break;

            case 1:
                currentColor = paintRed;
                break;

            case 2:
                currentColor = paintGreen;
                break;

            case 3:
                currentColor = paintYellow;
                break;

        }

    }

    public float getDensity() {

        density = getResources().getDisplayMetrics().density;
        return density;

    }

    /*

    private void drawLives(Canvas canvas, Paint paint){

        int xHeart= (int) (15*density);
        int yHeart= (int) (12*density);

        if (lives == 3) {

            canvas.drawBitmap(livesPicture, xHeart, yHeart, paint);
            canvas.drawBitmap(livesPicture, xHeart + livesPicture.getWidth() + 3*density, yHeart, paint);
            canvas.drawBitmap(livesPicture, xHeart + 2 * livesPicture.getWidth() + 6*density, yHeart, paint);
        }

        if (lives == 2) {

            canvas.drawBitmap(livesPicture, xHeart, yHeart, paint);
            canvas.drawBitmap(livesPicture, xHeart + livesPicture.getWidth() + 3, yHeart, paint);

        }

        if (lives == 1) {

            canvas.drawBitmap(livesPicture, xHeart, yHeart, paint);

        }

        if (lives > 3) {

            livesString = String.valueOf(lives);
            final String lives = livesString + "x";
            canvas.drawText(lives, 35 * getDensity(), 30 * getDensity(), paint);
            canvas.drawBitmap(livesPicture, 15 * getDensity() + 2 * livesPicture.getWidth() + 6, 12 * getDensity(), paint);

        }

    }

    */

    public void drawTransparency(Canvas canvas){

        final Paint paintRect = new Paint();
        paintRect.setARGB(188, 255, 255, 255);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintRect);
        pauseThread();

    }

    public int getScore() {

        return this.score;

    }

    public void pauseThread() {

        theGameLoopThread.setRunning(false);

    }

    public void resumeThread() {

        theGameLoopThread = new GameLoopThread(this);
        theGameLoopThread.setRunning(true);
        theGameLoopThread.start();

    }

    public void setTransparency(boolean theTransparency) {

        transparency = theTransparency;

    }

    public void checkIfTimeOver() {

        if (timeIsUp) {

            theGameActivity.onGameOver();

        }

    }

    public void checkColorTouched(int i, float x, float y){

        if (spriteListNum.get(i) == 0)
            temps.add(new TempSprite(temps, this,
                    x, y, bmpBlueTemp));

        else if (spriteListNum.get(i) == 1)
            temps.add(new TempSprite(temps, this,
                    x, y, bmpRedTemp));

        else if (spriteListNum.get(i) == 2)
            temps.add(new TempSprite(temps, this,
                    x, y, bmpGreenTemp));

        else if (spriteListNum.get(i) == 3)
            temps.add(new TempSprite(temps, this,
                    x, y, bmpYellowTemp));

    }

}