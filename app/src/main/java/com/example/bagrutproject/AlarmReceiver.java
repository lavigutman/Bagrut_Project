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

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);
        String username = prefs.getString("logged_in_user", null);

        if (!notificationsEnabled || username == null) return;

        DatabaseReference budgetRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MBudget budget = snapshot.child("Budget").getValue(MBudget.class);
                Double spent = snapshot.child("spends").getValue(Double.class);

                if (budget != null && budget.hasMBudget() && spent != null) {
                    double total = budget.getBudget();
                    if (total <= 0) return;

                    double spentAmount = spent;
                    double percentLeft = ((total - spentAmount) / total) * 100;

                    if (percentLeft <= 10) {
                        NotificationUtils.sendBudgetNotification(context, "Budget Alert", "Good morning! You have less than 10% of your budget left.");
                    } else if (percentLeft <= 50) {
                        NotificationUtils.sendBudgetNotification(context, "Budget Notice", "Good morning! You have less than 50% of your budget left.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AlarmReceiver", "Failed to fetch budget data: " + error.getMessage());
            }
        });
    }
}

