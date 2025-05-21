package com.example.bagrutproject;

import static com.example.bagrutproject.NotificationUtils.sendBudgetNotification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SettingsFragment extends Fragment {

    private Switch switchNotifications, switchDarkMode;
    private SharedPreferences sharedPreferences;
    private String username;
    private DatabaseReference usersRef;
    private FirebaseDatabase database;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        if (getArguments() != null) {
            username = getArguments().getString("USER_KEY");
        }
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        // Get references to the switches
        // In your Fragment's onViewCreated or onCreateView after view is inflated

// Use the same shared preferences instance
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        switchNotifications = view.findViewById(R.id.switch_notifications);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

// Load saved states (false is default here, change if needed)
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", false));
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode_enabled", false));
// After setting switch state from SharedPreferences:
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false);
        switchNotifications.setChecked(notificationsEnabled);

        if (notificationsEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }
        }

        // Then set your listener for toggle changes:
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply();

            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                    } else {
                        scheduleDailyNotification(requireContext(), username);
                    }
                } else {
                    scheduleDailyNotification(requireContext(), username);
                }
            } else {
                cancelDailyNotification(requireContext());
            }
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save dark mode preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode_enabled", isChecked);
            editor.apply();

            // Apply dark mode immediately
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Restart activity to apply the theme properly
            requireActivity().recreate();
        });
        return view;
    }

    private void checkAndSendBudgetNotifications() {
        DatabaseReference budgetRef = usersRef.child(username);
        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                MBudget budget = snapshot.child("Budget").getValue(MBudget.class);
                Double spent = snapshot.child("spends").getValue(Double.class);

                if (budget != null && budget.hasMBudget() && spent != null) {
                    double total = budget.getBudget();

                    if (total <= 0) {
                        Log.w("BudgetNotification", "Total budget is zero or negative — skipping notification.");
                        return;
                    }

                    double spentAmount = spent;
                    double percentLeft = ((total - spentAmount) / total) * 100;

                    if (percentLeft <= 10) {
                        sendBudgetNotification(requireContext(), "Budget Alert", "Good morning! You have less than 10% of your budget left.");
                    } else if (percentLeft <= 50) {
                        sendBudgetNotification(requireContext(), "Budget Notice", "Good morning! You have less than 50% of your budget left.");
                    }
                } else {
                    Log.w("BudgetNotification", "Budget or spent value is null or invalid.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BudgetNotification", "Firebase error: " + error.getMessage());
                Toast.makeText(requireContext(), "Failed to check budget", Toast.LENGTH_SHORT).show();
            }
        });

}
    private void cancelDailyNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
    public static void scheduleDailyNotification(Context context, String username) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("username", username);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the time is before now, set it for tomorrow
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }



}
