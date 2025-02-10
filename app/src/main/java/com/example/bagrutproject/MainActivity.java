package com.example.bagrutproject;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    Button logInBT,signBT;
    EditText userNameET, passwordET;
    private View.OnClickListener logIn;
    private View.OnClickListener signUp;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logInBT = findViewById(R.id.logBT);
        logInBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://bagrut-project-129a3-default-rtdb.firebaseio.com/");
                    database = FirebaseDatabase.getInstance();
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
                                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                        startActivity(intent);
                                        // Continue to the next activity (e.g., MainActivity or Dashboard)
                                        // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        // startActivity(intent);
                                        // finish();  // Optional: Close login screen
                                    } else {
                                        // Password does not match
                                        Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // No user found with the entered username
                                Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any error (e.g., network issues)
                            Log.w("Login", "Failed to read value.", databaseError.toException());
                            Toast.makeText(MainActivity.this, "Error while checking credentials", Toast.LENGTH_SHORT).show();
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
                Fragment signUpFragment = new SignUpFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, signUpFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    
}