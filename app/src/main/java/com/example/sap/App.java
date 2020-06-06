package com.example.sap;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID = "SAP";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChanel();
    }

    private void createNotificationChanel() {
        NotificationChannel chanel = new NotificationChannel(CHANNEL_ID, "Task(s) has been mutated", NotificationManager.IMPORTANCE_DEFAULT);
        chanel.setDescription("A chanel to display tasks changed notification");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(chanel);

    }
}
