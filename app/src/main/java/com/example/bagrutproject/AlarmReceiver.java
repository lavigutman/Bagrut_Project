package com.example.bagrutproject;

import static com.example.bagrutproject.NotificationUtils.sendBudgetNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received");
        
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);
        String username = prefs.getString("logged_in_user", null);

        if (!notificationsEnabled || username == null) {
            Log.d(TAG, "Notifications disabled or no user logged in");
            return;
        }

        // Check if notification was already sent today
        long lastNotificationTime = prefs.getLong("last_notification_time", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStart = calendar.getTimeInMillis();

        if (lastNotificationTime >= todayStart) {
            Log.d(TAG, "Notification already sent today");
            return;
        }

        Log.d(TAG, "Checking budget for user: " + username);
        DatabaseReference budgetRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d(TAG, "No budget data found");
                    return;
                }

                MBudget budget = snapshot.child("Budget").getValue(MBudget.class);
                Double spent = snapshot.child("spends").getValue(Double.class);

                if (budget != null && budget.hasMBudget() && spent != null) {
                    double total = budget.getBudget();
                    if (total <= 0) {
                        Log.d(TAG, "Budget is 0 or negative");
                        return;
                    }

                    double spentAmount = spent;
                    double percentLeft = ((total - spentAmount) / total) * 100;

                    Log.d(TAG, "Budget check - Total: " + total + ", Spent: " + spentAmount + ", Percent left: " + percentLeft);

                    if (percentLeft <= 10) {
                        Log.d(TAG, "Sending 10% budget alert");
                        NotificationUtils.sendBudgetNotification(context, 
                            "Budget Alert", 
                            "Good morning! You have less than 10% of your budget left.");
                        prefs.edit().putLong("last_notification_time", System.currentTimeMillis()).apply();
                    } else if (percentLeft <= 50) {
                        Log.d(TAG, "Sending 50% budget notice");
                        NotificationUtils.sendBudgetNotification(context, 
                            "Budget Notice", 
                            "Good morning! You have less than 50% of your budget left.");
                        prefs.edit().putLong("last_notification_time", System.currentTimeMillis()).apply();
                    } else {
                        Log.d(TAG, "Budget is above 50%, no notification needed");
                    }
                } else {
                    Log.d(TAG, "Invalid budget data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch budget data: " + error.getMessage());
            }
        });
    }
}

