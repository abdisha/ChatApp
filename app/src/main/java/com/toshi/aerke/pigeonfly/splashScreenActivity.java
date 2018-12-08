package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.icu.util.TimeUnit;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        ViewPager viewPager;
        LinearLayout linearLayout;
    int counter = 0;
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
         onPageChangeListener = new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

             }

             @Override
             public void onPageSelected(int position) {
                postionIndector(position);
             }

             @Override
             public void onPageScrollStateChanged(int state) {

             }
         };

         CountDownTimer count = new CountDownTimer(7000,1) {
             @Override
             public void onTick(long millisUntilFinished) {

                 new Handler().post(new Runnable() {
                     @Override
                     public void run() {
                         if(viewPager.getCurrentItem()<4){
                             counter=counter++;
                             viewPager.setCurrentItem(counter);
                         }if(viewPager.getCurrentItem()==3){
                             counter=0;
                         }
                     }
                 });
             }

             @Override
             public void onFinish() {

             }
         };

         if(firebaseAuth.getCurrentUser()!=null){
             slider =new PageSlider(this);

             postionIndector(0);
             viewPager.setAdapter(slider);
             count.start();
             viewPager.addOnPageChangeListener(onPageChangeListener);
         }else {
             startActivity(new Intent(splashScreenActivity.this,Home.class));
         }

          btnstart.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
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
