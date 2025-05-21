package com.example.bagrutproject;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    TextView nav_name;
    private String userKey;
    Toolbar toolbar;
    TextView textBalance, textIncome, textExpense, textBudgetTotal, textBudgetLeft, textBudgetSpent;
    DatabaseReference usersRef;
    String username;
    Button btnAddIncome, btnAddExpense, btnAddBudget;
    EditText editIncome, editExpense;
    Calendar calendar;
    DonutProgress donutProgress;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        checkInternetConnection();
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode_enabled", false);

        // Set dark mode theme based on stored preference
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("USER_KEY");
        resetValuesIfNeeded();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Toggle Button (Hamburger icon)
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            btnAddIncome.setOnClickListener(view -> {
                checkInternetConnection();
                if (editIncome.getVisibility() == View.GONE) {
                    editIncome.setVisibility(View.VISIBLE);
                } else {
                    String val = editIncome.getText().toString().trim();
                    if (!val.isEmpty()) {
                        double added = Double.parseDouble(val);
                        updateValue(username, "incomes", added);
                        updateValue(username, "balance", added);

                        // Save new Incomes object
                        Incomes incomeEntry = new Incomes(added);  // casting double to int for Incomes constructor
                        DatabaseReference incomeRef = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(username)
                                .child("incomesList");
                        incomeRef.push().setValue(incomeEntry);  // push creates a unique ID

                        editIncome.setVisibility(View.GONE);
                        editIncome.setText("");
                    }
                }
            });
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            return insets;

        });

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (isDarkModeEnabled) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));

        }
        textBudgetTotal = findViewById(R.id.text_budget_total);
        textBudgetLeft = findViewById(R.id.text_budget_left);
        textBudgetSpent = findViewById(R.id.text_budget_spent);
        btnAddBudget = findViewById(R.id.btn_add_budget);
        donutProgress = findViewById(R.id.donut_progress);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_name = headerView.findViewById(R.id.nav_name);
        UserName();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        textBalance = findViewById(R.id.text_balance);
        textIncome = findViewById(R.id.text_income);
        textExpense = findViewById(R.id.text_expense);
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        btnAddIncome = findViewById(R.id.btn_add_income);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        editIncome = findViewById(R.id.edit_income);
        editExpense = findViewById(R.id.edit_expense);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);
        String username = prefs.getString("logged_in_user", null);

        if (notificationsEnabled && username != null) {
            scheduleDailyBudgetNotification(this, username);
        }

        DatabaseReference userRef = usersRef.child(username);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkInternetConnection();
                if (snapshot.exists()) {
                    boolean hasIncome = snapshot.hasChild("incomes");
                    boolean hasExpense = snapshot.hasChild("spends");

                    Double income = hasIncome ? snapshot.child("incomes").getValue(Double.class) : 0.0;
                    Double expense = hasExpense ? snapshot.child("spends").getValue(Double.class) : 0.0;
                    Double balance = snapshot.child("balance").getValue(Double.class);

                    textIncome.setText("Monthly Income: ₪" + String.format("%.2f", income));
                    textExpense.setText("Monthly Expense: ₪" + String.format("%.2f", expense));
                    textBalance.setText(balance != null && balance != 0 ? "₪" + String.format("%.2f", balance) : "₪ -");

                    if (balance != null) {
                        if (balance < 0) {
                            textBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        } else {
                            textBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference budgetRef = usersRef.child(username);

        budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MBudget budget = snapshot.child("Budget").getValue(MBudget.class);
                    Double totalSpends = snapshot.child("spends").getValue(Double.class);

                    if (budget != null && totalSpends != null) {
                        double totalBudget = budget.getBudget();
                        double spentAmount = totalSpends;
                        double leftAmount = totalBudget - spentAmount;

                        int percentSpent = (int) ((spentAmount / totalBudget) * 100);
                        if (percentSpent > 100) percentSpent = 100;

                        donutProgress.setProgress(percentSpent);
                        donutProgress.setText(percentSpent + "%");

                        textBudgetTotal.setText("Total Budget: ₪" + totalBudget);
                        textBudgetSpent.setText("Amount Spent: ₪" + spentAmount);
                        textBudgetLeft.setText("Amount Left: ₪" + leftAmount);
                    } else {
                        Toast.makeText(MainActivity.this, "Budget or spends is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load budget data", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddExpense.setOnClickListener(view -> {
            checkInternetConnection();
            if (editExpense.getVisibility() == View.GONE) {
                editExpense.setVisibility(View.VISIBLE);
            } else {
                String val = editExpense.getText().toString().trim();
                if (!val.isEmpty()) {
                    double added = Double.parseDouble(val);
                    updateValue(username, "spends", added);
                    updateValue(username, "balance", -added);

                    // Save new Spendings object
                    Spendings spendEntry = new Spendings(added); // casting double to int for Spendings constructor
                    DatabaseReference spendRef = FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(username)
                            .child("spendsList");
                    spendRef.push().setValue(spendEntry);  // push creates a unique ID

                    editExpense.setVisibility(View.GONE);
                    editExpense.setText("");
                }
            }
        });

        btnAddBudget.setOnClickListener(view -> {
            checkInternetConnection();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Budget");

            // Inflate a custom layout for the dialog
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_budget, null);
            builder.setView(dialogView);
            EditText editBudgetAmount = dialogView.findViewById(R.id.edit_budget_amount);
            NumberPicker numberPickerResetDay = dialogView.findViewById(R.id.number_picker_reset_day);
            DatePicker datePickerEndDate = dialogView.findViewById(R.id.date_picker_end_date);

            // Set NumberPicker range
            numberPickerResetDay.setMinValue(1);
            numberPickerResetDay.setMaxValue(10);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String amountText = editBudgetAmount.getText().toString().trim();
                int resetDay = numberPickerResetDay.getValue();
                int year = datePickerEndDate.getYear();
                int month = datePickerEndDate.getMonth();
                int day = datePickerEndDate.getDayOfMonth();

                String endDate = day + "/" + (month + 1) + "/" + year;

                if (!amountText.isEmpty()) {
                    double budgetAmount = Double.parseDouble(amountText);

                    // Create MBudget object
                    MBudget mBudget = new MBudget(budgetAmount, resetDay, endDate);

                    // Save to Firebase
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("Users").child(username).child("Budget");

                    databaseReference.setValue(mBudget).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Budget added successfully!", Toast.LENGTH_SHORT).show();

                            // Update TextViews
                            textBudgetTotal.setText("Total Budget: ₪" + mBudget.getBudget());
                            textBudgetLeft.setText("Amount Left: ₪" + mBudget.getBudget());
                            textBudgetSpent.setText("Amount Spent: ₪0");
                        } else {
                            Toast.makeText(this, "Failed to add budget", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void updateValue(String username, String field, double addedAmount) {
        checkInternetConnection();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        userRef.child(field).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double current = snapshot.getValue(Double.class);
                if (current == null) {
                    current = 0.0;
                }
                double updated = current + addedAmount;
                userRef.child(field).setValue(updated);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to update " + field, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        checkInternetConnection();
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        } else if (id == R.id.nav_profile) {
            findViewById(R.id.balance_container).setVisibility(View.VISIBLE);
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer smoothly

            new Handler().postDelayed(() -> {
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                Intent intent = getIntent();
                userKey = intent.getStringExtra("USER_KEY");
                bundle.putString("USER_KEY", userKey); // Pass userKey to ProfileFragment
                profileFragment.setArguments(bundle);
                openFragment(profileFragment);
            }, 200);
        } else if (id == R.id.nav_settings) {
            findViewById(R.id.balance_container).setVisibility(View.VISIBLE);
            SettingsFragment settingsFragment = new SettingsFragment();
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            userKey = intent.getStringExtra("USER_KEY");
            bundle.putString("USER_KEY", userKey); // Make sure userKey is not null
            settingsFragment.setArguments(bundle);
            openFragment(settingsFragment);
        } else if (id == R.id.nav_logout) {
            findViewById(R.id.balance_container).setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        findViewById(R.id.balance_container).setVisibility(View.VISIBLE);
        return true;
    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void UserName() {
        checkInternetConnection();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Intent intent = getIntent();
        userKey = intent.getStringExtra("USER_KEY");
        nav_name.setText(userKey);

        DatabaseReference userRef = usersRef.child(userKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        toolbar.setTitle("Welcome " + name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseData", "Database error: " + error.getMessage());
            }
        });
    }

    private void openFragment(Fragment fragment) {
        checkInternetConnection();
        findViewById(R.id.balance_container).setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void resetValuesIfNeeded() {
        checkInternetConnection();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                // Get current date as string
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
                int year = calendar.get(Calendar.YEAR);
                String currentDate = day + "/" + month + "/" + year;

                // Reset budget if needed or end budget if reached endDate
                if (snapshot.hasChild("Budget")) {
                    MBudget budgetData = snapshot.child("Budget").getValue(MBudget.class);
                    if (budgetData != null) {

                        // Reset budget on resetDay
                        if (day == budgetData.getResetDay()) {
                            budgetData.setBudget(0);
                        }

                        // End budget if current date matches endDate
                        if (currentDate.equals(budgetData.getEndDate())) {
                            budgetData.setBudget(0);
                            budgetData.setHasBudget(false);
                        }

                        // Save updated budget back to Firebase
                        userRef.child("Budget").setValue(budgetData);
                    }
                }

                // Reset spends if 1st of month
                if (day == 1 && snapshot.hasChild("spends")) {
                    userRef.child("spends").setValue(0);
                }

                // Reset incomes if 1st of month
                if (day == 1 && snapshot.hasChild("incomes")) {
                    userRef.child("incomes").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to check resets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void scheduleDailyBudgetNotification(Context context, String username) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

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
    private void checkInternetConnection() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}