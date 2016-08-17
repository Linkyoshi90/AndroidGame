package com.example.josha.anothergame;

/**
 * Created by Josha on 15.08.2016.
 */

import java.util.List;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TempSprite {

    private float x;
    private float y;
    private Bitmap bmp;
    private int life = 20;
    private List<TempSprite> temps;

    public TempSprite(List<TempSprite> temps, GameView theGameView, float x, float y, Bitmap bmp) {

        this.x = Math.min(Math.max(x - bmp.getWidth() / 2, 0),
                theGameView.getWidth() - bmp.getWidth());
        this.y = Math.min(Math.max(y - bmp.getHeight() / 2, 0),
                theGameView.getHeight() - bmp.getHeight());
        this.bmp = bmp;
        this.temps = temps;

    }

    public void draw(Canvas canvas) {

        update();
        canvas.drawBitmap(bmp, x, y, null);

    }

    private void update() {

        if (--life < 1) {

            temps.remove(this);

        }

    }

}