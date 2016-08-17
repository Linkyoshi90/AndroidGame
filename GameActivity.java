package com.example.josha.anothergame;

/**
 * Created by Josha on 08.08.2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;

public class GameActivity extends Activity implements OnClickListener, OnDismissListener {

    private Button resume, menu, exit, newTry, pause;
    private boolean dialogIsActive=false;
    private boolean firstStart=true;
    private boolean gameOver = false;
    private boolean onPauseSecondTime = false;
    private boolean soundToggleState;
    private boolean vibrationToggleState;
    private Dialog dialog;
    private GameView theGameView;
    private long pauseTime, starttime;
    private MediaPlayer mpPoint, mpMinusPoint, mpBackground;
    private ToggleButton vibrationToggle, soundToggle;

    @Override
    protected void onPause() {

        super.onPause();
        theGameView.pauseThread();
        firstStart = false;

        if (mpBackground != null) {

            mpBackground.release();

        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        theGameView.pauseThread();
        dialog.dismiss();
        mpPoint.release();
        mpMinusPoint.release();

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!firstStart) {

            dialog.show();
            dialogIsActive = true;
            createBackgroundSound();

            if (soundToggleState) {

                mpBackground.start();

            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        theGameView = new GameView(this);
        setContentView(new GameView(this));
        dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.pausescreen);
        dialog.hide();
        dialog.setOnDismissListener(this);
        mpPoint = MediaPlayer.create(this, R.raw.sm64_1up);
        mpMinusPoint = MediaPlayer.create(this, R.raw.sm64_impact);
        mpBackground = MediaPlayer.create(this, R.raw.bg2);
        mpBackground.setLooping(true);
        initialize();

        if (soundToggleState) {
            playBackgroundSound();
        }

    }

    public boolean getSoundToggleState() {

        return soundToggleState;

    }

    public void playPoint() {

        mpPoint.start();

    }

    public void playMinusPoint() {

        mpMinusPoint.start();

    }

    public void playBackgroundSound() {

        if (mpBackground != null && !mpBackground.isPlaying()) {

            try {

                mpBackground.start();

            }

            catch (IllegalStateException e) {

                e.printStackTrace();

            }

        }
        
    }

    public void onGameOver() {

        compareScore();
        Intent theNextIntent = new Intent(getApplicationContext(), GameOverActivity.class);
        theNextIntent.putExtra("score", theGameView.getScore());
        startActivity(theNextIntent);
        gameOver = true;
        this.finish();

    }

    public int readHighscore() {

        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);

    }

    public void writeHighscore(int highscore) {

        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("HIGHSCORE", highscore);
        editor.commit();

    }
    public void compareScore() {

        if (theGameView.getScore() > readHighscore()) {

            writeHighscore(theGameView.getScore());

        }

    }

    private void initialize() {

        resume = (Button) dialog.findViewById(R.id.bContinue);
        menu = (Button) dialog.findViewById(R.id.bMainMenu);
        newTry = (Button) dialog.findViewById(R.id.bNewTry);
        resume.setOnClickListener(this);
        menu.setOnClickListener(this);
        newTry.setOnClickListener(this);
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        vibrationToggleState = pref.getBoolean("vibrationToggleState", false);
        vibrationToggle = (ToggleButton) dialog.findViewById(R.id.tbVibration);
        vibrationToggle.setChecked(vibrationToggleState);

        vibrationToggle.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                saveVibrationToggleState();

            }

        });

        soundToggleState = pref.getBoolean("soundToggleState", false);
        soundToggle = (ToggleButton) dialog.findViewById(R.id.tbSound);
        soundToggle.setChecked(soundToggleState);

        soundToggle.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                saveSoundToggleState();

                if (soundToggle.isChecked()) {

                    createBackgroundSound();

                    if (mpBackground != null && !mpBackground.isPlaying()) {

                        try {
                            //mpBackground.prepare();

                            mpBackground.start();

                        }

                        catch (IllegalStateException e) {

                            e.printStackTrace();

                        }

                        /*

                        catch (IOException e) {

                            e.printStackTrace();

                        }

                        */

                    }

                }

                else {

                    if (mpBackground != null && mpBackground.isPlaying()) {

                        mpBackground.stop();
                        mpBackground.release();

                    }

                }

            }

        });

    }

    public void createBackgroundSound() {

        mpBackground=null;
        mpBackground = MediaPlayer.create(this, R.raw.bg);
        mpBackground.setLooping(true);

    }

    public void saveVibrationToggleState(){

        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("vibrationToggleState", vibrationToggle.isChecked());
        editor.commit();

    }

    public boolean getVibrationToggleState(){

        return vibrationToggleState;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bContinue:
                dialogState();
                break;

            case R.id.bMainMenu:
                compareScore();
                dialog.dismiss();
                Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(menuIntent);
                finish();
                break;

            case R.id.bNewTry:
                compareScore();
                Intent newGameScreen= new Intent(this, GameActivity.class);
                startActivity(newGameScreen);
                dialog.dismiss();
                finish();

        }

    }

    @Override
    public void onBackPressed() {

        dialogState();

    }

    public void dialogState() {

        if (dialogIsActive) {

            dialog.hide();
            dialogIsActive = false;
            theGameView.setTransparency(false);
            pauseTime = System.currentTimeMillis() - starttime + pauseTime;
            theGameView.resumeThread();
            onPauseSecondTime = false;

        }

        else if (!dialogIsActive) {

            theGameView.setTransparency(true);

            if(!gameOver)
                dialog.show();

            dialogIsActive = true;
            starttime = System.currentTimeMillis();
            onPauseSecondTime = true;

        }
    }

    public void onDismiss(DialogInterface dialog) {

        dialogState();

    }

    public void vibrator() {

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(200);

    }

    public void saveSoundToggleState(){

        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("soundToggleState", soundToggle.isChecked());
        editor.commit();

    }

    public long getPauseTime() {

        return pauseTime;

    }

}