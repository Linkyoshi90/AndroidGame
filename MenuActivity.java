package com.example.josha.anothergame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Josha on 10.08.2016.
 */

public class MenuActivity extends Activity implements View.OnClickListener{

    ImageButton bPlay, bExit;
    TextView tvHighscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        initialize();

    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bPlay:
                Intent newGameScreen= new Intent(getApplicationContext(), GameActivity.class);
                startActivity(newGameScreen);
                this.finish();
                break;

            case R.id.bExit2:
                this.finish();
                break;

        }

    }

    public void initialize() {

        bPlay = (ImageButton) findViewById(R.id.bPlay);
        bPlay.setOnClickListener(this);
        bExit = (ImageButton) findViewById(R.id.bExit2);
        bExit.setOnClickListener(this);
        tvHighscore = (TextView) findViewById(R.id.tvHighScore2);
        tvHighscore.setText(Integer.toString(readHighscore()));

    }

    public int readHighscore() {

        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);

    }

}
