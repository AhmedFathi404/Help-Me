package com.example.helpme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.helpme.EmergencyActivity;
import com.example.helpme.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotifications extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String tittle = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String click_action=remoteMessage.getNotification().getClickAction();
        Intent in = new Intent(click_action);
        final String channel_ID ="HEADS_UP_NOTIFICATION";
        NotificationChannel channel = new NotificationChannel(
                channel_ID,
                "Heads_Up_Notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this,channel_ID)
                .setContentTitle(tittle)
                .setContentText(text)
                .setSmallIcon(R.mipmap.appicon)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1,notification.build());
        super.onMessageReceived(remoteMessage);
        }



}
