package com.example.josha.anothergame;

/**
 * Created by Josha on 10.08.2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread logoTimer = new Thread(){

            public void run(){

                try{

                    sleep(2000);
                    Intent menuIntent = new Intent(SplashActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                    finish();

                }

                catch (InterruptedException e) {

                    e.printStackTrace();

                }

                finally{



                }

            }

        };

        logoTimer.start();

    }

}