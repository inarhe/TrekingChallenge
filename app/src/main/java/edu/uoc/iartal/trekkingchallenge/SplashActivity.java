package edu.uoc.iartal.trekkingchallenge;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private final int TIME_SPLASH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // First app screen with aplication name and logo
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               Intent intent = new Intent(SplashActivity.this, MainActivity.class);
               startActivity(intent);
               finish();
            }
        }, TIME_SPLASH);
    }
}
