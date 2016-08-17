package com.example.josha.anothergame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameOverActivity extends Activity implements OnClickListener{

    private ImageButton bReplay;
    private ImageButton bExit;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameoverscreen);
        bReplay = (ImageButton) findViewById(R.id.bReplay);
        bExit = (ImageButton) findViewById(R.id.bExit);
        bReplay.setOnClickListener(this);
        bExit.setOnClickListener(this);

    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bReplay:
                Intent newGameScreen= new Intent(getApplicationContext(), GameActivity.class);
                startActivity(newGameScreen);
                this.finish();
                break;

            case R.id.bExit:
                Intent menuIntent= new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(menuIntent);
                this.finish();
                break;

        }

    }

    public void initialize(){

        int score = this.getIntent().getExtras().getInt("score");
        TextView tvScore = (TextView) findViewById(R.id.tvScore);
        tvScore.setText("Your score is: " + Integer.toString(score));

        /*

        TextView tvHighscore = (TextView) findViewById(R.id.tvHighScore);
        tvHighscore.setText("Endless Game Highscore: " + Integer.toString(readHighscore()));

         */

    }

    public int readHighscore() {

        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);

    }

}