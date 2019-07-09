package com.app.galnoriel.footbook.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    final int NOTIF_ID = 1 ;
    FirebaseAuth auth;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0){

            Intent intent = new Intent("message_received");
            intent.putExtra("message",remoteMessage.getData().get("message"));

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_CANCEL_CURRENT);

            Notification.Builder builder = new Notification.Builder(this);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("id_1", "name1", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.GREEN);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{2000});
                manager.createNotificationChannel(channel);
                builder.setChannelId("id_1");
            }

            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            builder.setContentTitle("new message from " + user.getDisplayName()).setContentText(remoteMessage.getData().get("message"))
                    .setSmallIcon(R.drawable.announcment)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[] {2000})
                    .setLights(Color.GREEN, 3000, 3000);



            manager.notify(NOTIF_ID,builder.build());
        }
    }
}
