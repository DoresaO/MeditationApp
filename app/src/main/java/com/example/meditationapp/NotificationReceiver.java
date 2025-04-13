package com.example.meditationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "🔔 Reminder triggered!", Toast.LENGTH_SHORT).show(); // for testing
        Log.d("ReminderTest", "Notification broadcast received!");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "mood_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // use a valid drawable!
                .setContentTitle("🌿 Mood Reminder")
                .setContentText("How are you feeling today?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(100, builder.build());
    }
}
