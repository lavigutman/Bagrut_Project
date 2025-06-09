package com.example.bagrutproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment that manages user profile settings and information.
 * This fragment handles:
 * - Display and editing of user's personal information (name, surname, username)
 * - Password change functionality
 * - Profile data persistence in Firebase
 * - User data validation and updates
 */
public class ProfileFragment extends Fragment {

    private EditText editUsername, editName, editSurname, editCurrentPassword, editNewPassword, editConfirmPassword;
    private Button saveChangesBtn;
    private String userKey;
    private boolean isViewsInitialized = false;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        // Get user key (UID) from Arguments
        if (getArguments() != null) {
            userKey = getArguments().getString("USER_KEY");
        }

        // Ensure we have a valid user key
        if (userKey == null || userKey.isEmpty()) {
            Log.e("ProfileFragment", "User key is missing!");
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        initializeViews(view);
        
        // Set Button Click Listener
        saveChangesBtn.setOnClickListener(v -> saveChanges());

        // Fetch user data after views are initialized
        fetchUserData();

        return view;
    }

    /**
     * Initializes all the view components of the fragment.
     * @param view The root view of the fragment
     */
    private void initializeViews(View view) {
        editUsername = view.findViewById(R.id.edit_username);
        editName = view.findViewById(R.id.edit_name);
        editSurname = view.findViewById(R.id.edit_surname);
        editCurrentPassword = view.findViewById(R.id.edit_current_password);
        editNewPassword = view.findViewById(R.id.edit_new_password);
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        saveChangesBtn = view.findViewById(R.id.save_changes_btn);
        isViewsInitialized = true;
    }

    /**
     * Fetches the current user's data from Firebase and populates the UI fields.
     * Retrieves username, name, and surname from the database and updates the corresponding EditText fields.
     */
    private void fetchUserData() {
        if (!isViewsInitialized) {
            Log.e("ProfileFragment", "Views not initialized yet!");
            return;
        }

        usersRef.orderByChild("userName").equalTo(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userKey = snapshot.getKey(); // Get the unique key for this user
                        User user = snapshot.getValue(User.class);

                        if (user != null && isViewsInitialized) {
                            editUsername.setText(user.getUserName());
                            editName.setText(user.getName());
                            editSurname.setText(user.getSurname());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to load user data", databaseError.toException());
            }
        });
    }

    /**
     * Saves changes made to the user's profile information.
     * Handles both username changes and basic profile updates:
     * - If username is changed, creates a new entry and removes the old one
     * - If only name/surname is changed, updates the existing entry
     * - If password fields are filled, verifies current password and updates to new password
     * Updates are performed in Firebase and user feedback is provided via Toast messages.
     */
    private void saveChanges() {
        if (userKey == null || userKey.isEmpty()) {
            Toast.makeText(getContext(), "User ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String newUsername = editUsername.getText().toString().trim();
        String newName = editName.getText().toString().trim();
        String newSurname = editSurname.getText().toString().trim();
        String currentPassword = editCurrentPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Check if user is trying to change password
        if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            // Validate password fields
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all password fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verify current password and update to new password
            checkCurrentPassword(currentPassword, newPassword);
        }

        // Check if the username has changed
        if (!newUsername.equals(userKey)) {
            // Step 1: Copy existing user data to the new username key
            usersRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Map<String, Object> userData = (Map<String, Object>) snapshot.getValue();
                        userData.put("userName", newUsername); // Update username field

                        // Step 2: Save under new username
                        usersRef.child(newUsername).setValue(userData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Step 3: Delete the old entry
                                usersRef.child(userKey).removeValue();
                                userKey = newUsername; // Update userKey reference
                                Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to update username", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error updating username", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If username hasn't changed, update only name & surname
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("surname", newSurname);

            usersRef.child(userKey).updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Clear password fields after successful update
                    editCurrentPassword.setText("");
                    editNewPassword.setText("");
                    editConfirmPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Verifies the current password and updates to the new password if correct.
     * @param currentPassword The user's current password for verification
     * @param newPassword The new password to be set if verification succeeds
     */
    private void checkCurrentPassword(String currentPassword, String newPassword) {
        usersRef.child(userKey).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String storedPassword = dataSnapshot.getValue(String.class);

                if (storedPassword != null && storedPassword.equals(currentPassword)) {
                    // Update password in Firebase only if the current password is correct
                    usersRef.child(userKey).child("password").setValue(newPassword);
                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error checking password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
