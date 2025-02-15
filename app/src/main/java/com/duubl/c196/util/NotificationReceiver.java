package com.duubl.c196.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.duubl.c196.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private void showNotification(Context context) {
        String channelId = "scheduled_notification_channel";
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Scheduled Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder")
                .setContentText("This is your scheduled notification!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());
    }

    public static void scheduleNotification(Context context, LocalDate date) {
        LocalDateTime localDateTime = date.atStartOfDay();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        long triggerTimeMillis = zonedDateTime.toInstant().toEpochMilli();

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent permissionIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(permissionIntent);
                    Toast.makeText(context, "Please grant exact alarm permission.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
                Log.d("NotificationReceiver", "Alarm scheduled for " + date.getMonth() + date.getDayOfMonth());
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(context, "Exact alarm permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
