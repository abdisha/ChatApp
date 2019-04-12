package com.toshi.aerke.Utilitis;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class OfflineStorage  extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso Builder = builder.build();
        Builder.setIndicatorsEnabled(true);
        Builder.setLoggingEnabled(true);
        Picasso.setSingletonInstance(Builder);
        UserState.getInstance().setUserState(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        UserState.getInstance().setUserState(false);
    }
}
