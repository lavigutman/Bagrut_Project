package com.example.bagrutproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.bagrutproject.R;

import java.util.Calendar;

/**
 * Utility class for handling budget-related notifications in the application.
 * This class manages the creation and display of notifications for budget alerts,
 * including channel creation for Android 8.0 (API level 26) and above.
 */
public class NotificationUtils {

    /**
     * Sends a budget notification to the user.
     * Creates a notification channel if needed (for Android 8.0 and above)
     * and displays the notification with the specified title and message.
     * 
     * @param context The application context
     * @param title The title of the notification
     * @param message The message content of the notification
     */
    public static void sendBudgetNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "budget_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Budget Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications when budget is low");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_icon) // use your app's notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
