package com.duubl.c196.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.duubl.c196.entities.Course;

import java.time.ZoneId;
import java.util.Calendar;

public class SchedulerNotificationManager {

    private static final String CHANNEL_ID = "MEOW";

    public static void scheduleNotification(Context context, Course course) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(java.util.Date.from(course.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Intent intent = new Intent(context, SchedulerNotificationReceiver.class);
        intent.putExtra("course_name", course.getCourseName());
        intent.putExtra("course_id", course.getCourseID());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                course.getCourseID(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule the notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    startDate.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    startDate.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    public static class SchedulerNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String courseName = intent.getStringExtra("course_name");
            if (courseName != null) {
                showNotification(context, courseName);
            }
        }

        private void showNotification(Context context, String courseName) {
            // Create the notification channel (required for Android 8.0 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Course Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifies when a course starts");

                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Course Starting Today")
                    .setContentText("Your course \"" + courseName + "\" starts today.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            // Show the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(courseName.hashCode(), builder.build());
            }
        }
    }
}
