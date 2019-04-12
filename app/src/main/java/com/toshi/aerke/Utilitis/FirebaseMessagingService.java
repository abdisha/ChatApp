package com.toshi.aerke.Utilitis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.toshi.aerke.pigeonfly.R;

import java.util.Map;

import static android.app.NotificationManager.*;

public class FirebaseMessagingService extends  com.google.firebase.messaging.FirebaseMessagingService {
    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (!remoteMessage.getData().isEmpty()){
           {
                sendNotification(remoteMessage);
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String,String> data = remoteMessage.getData();
        String _title =data.get("title");
        String _content = data.get("content");
        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String _notificationChannel = "PEGION";
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            @SuppressWarnings("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(_notificationChannel
                                                ,"Pegion Chat", NotificationManager.IMPORTANCE_MAX);

            notificationChannel.setName(_title);
            notificationChannel.setDescription(_content);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.getLightColor();
            notificationManager.createNotificationChannel(notificationChannel);
        }else{

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, _notificationChannel)
                    .setSmallIcon(R.drawable.ic_speaker_notes_black_24dp)
                    .setContentTitle(_title)
                    .setContentText(_content)
                    .setContentInfo("Message")
                    .setTicker("Hearty365")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
             builder.notify();
        }


    }


}
