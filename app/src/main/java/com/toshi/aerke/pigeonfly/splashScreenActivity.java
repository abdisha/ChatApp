package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.icu.util.TimeUnit;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.toshi.aerke.Utilitis.PageSlider;
import com.toshi.aerke.Utilitis.UserState;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class splashScreenActivity extends AppCompatActivity {
        private FirebaseAuth firebaseAuth;
        private String userId = null;
        UserState userState;
        private PageSlider slider;
        private  Handler handler;
        ViewPager viewPager;
        LinearLayout linearLayout;
        private  int currentItem = 0;
        ViewPager.OnPageChangeListener onPageChangeListener;
          Button btnstart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_splash_screen);
        final TextView textView = (TextView)findViewById(R.id.LogoName);
        linearLayout =(LinearLayout)findViewById(R.id.postionIndector);
        viewPager =(ViewPager) findViewById(R.id.viewPager);
        btnstart =(Button)findViewById(R.id.Start);
         firebaseAuth = FirebaseAuth.getInstance();
         handler = new Handler();
         onPageChangeListener = new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

             }

             @Override
             public void onPageSelected(int position) {
                 currentItem = position;
                postionIndector(currentItem);
             }

             @Override
             public void onPageScrollStateChanged(int state) {

             }
         };
         final Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentItem <=4){
                            Log.i("currentCounter", "AutoSlider: " + currentItem);
                            viewPager.setCurrentItem(currentItem);
                            currentItem=currentItem+1;
                        }
                    }
                });


            }
        };

        timer.schedule(timerTask,2000,4000);
        if (currentItem == 4)
            timer.cancel();


             slider =new PageSlider(this);

             postionIndector(0);
             viewPager.setAdapter(slider);
             viewPager.addOnPageChangeListener(onPageChangeListener);

            // count.start();


          btnstart.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  timer.cancel();
                  startActivity(new Intent(splashScreenActivity.this,CreateAcount.class));
              }
          });


    }
  private void postionIndector(int postion){
      TextView[] mdot = new TextView[4];
      linearLayout.removeAllViews();
      for(int i = 0; i<mdot.length; i++){
          mdot[i]=new TextView(this);
          mdot[i].setText(Html.fromHtml("&#8226"));
          mdot[i].setTextSize(35f);
          mdot[i].setTextColor(Color.parseColor("#a9b4bb"));
          linearLayout.addView(mdot[i]);
      }
      if(mdot.length>0){
          mdot[postion].setTextColor(getResources().getColor(R.color.colorTextPrimary));
      }

  }




}
