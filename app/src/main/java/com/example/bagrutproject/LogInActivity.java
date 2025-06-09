package com.example.bagrutproject;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity responsible for user authentication and login functionality.
 * This activity handles:
 * - User login with username and password
 * - Auto-login functionality for users who chose to stay signed in
 * - Navigation to the main application after successful authentication
 * - User authentication against Firebase database
 */
public class LogInActivity extends AppCompatActivity {
    Button logInBT,signBT;
    EditText userNameET, passwordET;
    private View.OnClickListener logIn;
    private View.OnClickListener signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check for saved credentials
        SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean keepSignedIn = prefs.getBoolean("keep_signed_in", false);
        String savedUsername = prefs.getString("saved_username", null);

        if (keepSignedIn && savedUsername != null) {
            // Auto-login with saved username
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("Users");
            usersRef.orderByChild("userName").equalTo(savedUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User found, proceed to MainActivity
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        intent.putExtra("USER_KEY", savedUsername);
                        startActivity(intent);
                        finish(); // Close the login activity
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("AutoLogin", "Failed to read value.", databaseError.toException());
                }
            });
        }

        logInBT = findViewById(R.id.logBT);
        logInBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    Toast.makeText(LogInActivity.this, "Service not available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                    return;
                }
                String username = userNameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("Users");
                    usersRef.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Username found, now check the password
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String storedPassword = userSnapshot.child("password").getValue(String.class);
                                    if (storedPassword != null && storedPassword.equals(password)) {
                                        // Password matches, login successful
                                        SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                                        prefs.edit().putString("logged_in_user", username).apply();
                                        
                                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                        intent.putExtra("USER_KEY", username);
                                        startActivity(intent);
                                        finish(); // Close the login activity
                                    } else {
                                        // Password does not match
                                        Toast.makeText(LogInActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // No user found with the entered username
                                Toast.makeText(LogInActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any error (e.g., network issues)
                            Log.w("Login", "Failed to read value.", databaseError.toException());
                            Toast.makeText(LogInActivity.this, "Error while checking credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        userNameET = findViewById(R.id.nameET);
        passwordET = findViewById(R.id.pasET);
        signBT = findViewById(R.id.signBT);
        signBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable()) {
                    Toast.makeText(LogInActivity.this, "Service not available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                    return;
                }
                Fragment signUpFragment = new SignUpFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, signUpFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Checks if the device has an active internet connection.
     * @return true if there is an active internet connection, false otherwise
     */
    private boolean isInternetAvailable() {
        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}