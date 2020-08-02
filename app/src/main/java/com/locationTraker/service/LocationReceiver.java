package com.locationTraker.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.locationTraker.R;
import com.locationTraker.ui.MainActivity;
import com.locationTraker.utils.Keys;

public class LocationReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.locationtracker.notify";


    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification("Location Service Status",intent.getStringExtra(Keys.BROADCAST_MSG),context);
    }


    public void sendNotification(String title, String message, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }


        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent, Intent.FILL_IN_ACTION);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        if (notificationManager != null) {
            notificationManager.notify(1, notification.build());
        }
    }







}
