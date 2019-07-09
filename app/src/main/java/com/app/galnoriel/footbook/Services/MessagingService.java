package com.app.galnoriel.footbook.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.app.galnoriel.footbook.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    final int NOTIF_ID = 1 ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0){

            Intent intent = new Intent("message_received");
            intent.putExtra("message",remoteMessage.getData().get("message"));

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("id_1", "name1", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                builder.setChannelId("id_1");
            }

            builder.setContentTitle("new message from topic").setContentText(remoteMessage.getData().get("message"))
                    .setSmallIcon(R.drawable.announcment);

            manager.notify(NOTIF_ID,builder.build());
        }
    }
}
