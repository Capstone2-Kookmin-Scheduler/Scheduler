package edu.capstone.scheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class Notification extends AppCompatActivity {
    Intent intent = new Intent();

    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, String name) {
//        super.onCreate(savedInstanceState);
//
//        builder = null;
//        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.createNotificationChannel( new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT) );
//        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//
//        builder.setContentTitle("알림");
//        builder.setContentText(name);
//
//    }

//    public void show_notification(String name) {
//        onCreate(null, name);
//
//
//    } // end of show_notification



} // end of class
