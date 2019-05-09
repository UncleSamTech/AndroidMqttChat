package com.handcarryapp.ustech.seamfixchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
//the variable for holding the progress of the synchronous status is declared
    int stat_progress = 0;
Context c = SplashActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        executeProgress();

    }

    //this method is used for running the splash screem asynchronously

    public void executeProgress(){
        new Thread(new Runnable() {
            public void run() {
                while (stat_progress < 100) {
                    stat_progress += 5;

                    try {

                        Thread.sleep(200);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(c, MainActivity.class);
               startActivity(intent);

            }
        }).start();

    }//closing brace method
}//closing brace class


