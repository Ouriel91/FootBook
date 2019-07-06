package com.app.galnoriel.footbook.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Chronometer;

import com.app.galnoriel.footbook.MainActivity;
import com.app.galnoriel.footbook.R;

public class TimerService extends Service {

    final int NOTIF_ID = 1;
    private Chronometer mChronometer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this, MainActivity.class); //check passing to fragment
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(TimerService.this);

        if (Build.VERSION.SDK_INT >= 26){

            NotificationChannel locationChannel = new NotificationChannel("channel_location_id","Location channel",NotificationManager.IMPORTANCE_HIGH);
            locationChannel.enableLights(true);
            locationChannel.setLightColor(Color.GREEN);
            locationChannel.enableVibration(true);
            locationChannel.setVibrationPattern(new long[]{2000});
            builder.setChannelId("channel_location_id");
            manager.createNotificationChannel(locationChannel);
        }

        builder.setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setContentTitle("Time is up!!! ")
                .setContentText("Go to game page and start new game") //check how to do - count up
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {2000})
                .setLights(Color.GREEN, 3000, 3000);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);


        manager.notify(NOTIF_ID, builder.build());

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        stopSelf();
    }
}
