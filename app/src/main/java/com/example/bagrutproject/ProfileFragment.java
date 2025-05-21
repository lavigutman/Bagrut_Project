package com.example.bagrutproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText editUsername, editName, editSurname, editCurrentPassword, editNewPassword, editConfirmPassword;
    private ImageView profileImageView;
    private Button uploadImageBtn, saveChangesBtn;
    private String userKey;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Uri selectedImageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
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

        // Fetch user data
        fetchUserData();
    }

    private void fetchUserData() {
        usersRef.orderByChild("userName").equalTo(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userKey = snapshot.getKey(); // ✅ Get the unique key for this user
                        User user = snapshot.getValue(User.class);

                        if (user != null) {
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

    private void saveChanges() {
        if (userKey == null || userKey.isEmpty()) {
            Toast.makeText(getContext(), "User ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String newUsername = editUsername.getText().toString().trim();
        String newName = editName.getText().toString().trim();
        String newSurname = editSurname.getText().toString().trim();

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
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

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

    private void uploadProfilePicture() {
        StorageReference profileImageRef = storageRef.child("profile_pictures/" + userKey + ".jpg");
        profileImageRef.putFile(selectedImageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    usersRef.child(userKey).child("profileImageUrl").setValue(uri.toString());
                    Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        editUsername = view.findViewById(R.id.edit_username);
        editName = view.findViewById(R.id.edit_name);
        editSurname = view.findViewById(R.id.edit_surname);
        editCurrentPassword = view.findViewById(R.id.edit_current_password);
        editNewPassword = view.findViewById(R.id.edit_new_password);
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        profileImageView = view.findViewById(R.id.profile_image);
        uploadImageBtn = view.findViewById(R.id.upload_image_btn);
        saveChangesBtn = view.findViewById(R.id.save_changes_btn);

        // Set Button Click Listeners
        uploadImageBtn.setOnClickListener(v -> openImagePicker());
        saveChangesBtn.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void openImagePicker() {
        // Open gallery or camera to select profile image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }
}
