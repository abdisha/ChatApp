package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.icu.util.TimeUnit;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TimeUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.toshi.aerke.Utilitis.UserState;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class splashScreenActivity extends AppCompatActivity {
        private FirebaseAuth firebaseAuth;
        private String userId = null;
        UserState userState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        CountDownTimer time = new CountDownTimer(3000,1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity( new Intent(splashScreenActivity.this,CreateAcount.class));
            }
        };

         time.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        userId =firebaseAuth.getCurrentUser().getUid();

             if(!userId.isEmpty()){
                userState = UserState.getInstance(userId);
                 userState.setUserState(true);
                 startActivity(new Intent(this,Home.class));
             }else {
                startActivity(new Intent(this,CreateAcount.class));
              }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userState!=null){

            userState.setUserState(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userState!=null){
            userState.setUserState(false);
        }
    }
}
