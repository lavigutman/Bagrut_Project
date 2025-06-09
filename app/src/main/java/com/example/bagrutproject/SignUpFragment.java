package com.example.bagrutproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Fragment that handles user registration functionality.
 * This fragment provides a form for new users to create an account,
 * including fields for username, password, name, surname, and initial balance.
 * It validates user input and stores the new user data in Firebase.
 */
public class SignUpFragment extends Fragment {
    Button registerBT, backBT;
    EditText userNameET, passwordET, confPasswordET,nameET,surenameET,balanceET;
    TextView error;

    /**
     * Called when the fragment is first created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return The View for the fragment's UI
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_fragment, container, false);
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored.
     * Initializes UI components and sets up click listeners for registration and back buttons.
     * @param view The View returned by onCreateView()
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerBT = view.findViewById(R.id.register);
        userNameET = view.findViewById(R.id.username);
        passwordET = view.findViewById(R.id.password);
        confPasswordET = view.findViewById(R.id.confPassword);
        error = view.findViewById(R.id.error);
        nameET = view.findViewById(R.id.nameET);
        surenameET = view.findViewById(R.id.surenameET);
        balanceET = view.findViewById(R.id.balanceET);
        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://bagrut-project-129a3-default-rtdb.firebaseio.com/");
                DatabaseReference myRef = database.getReference("Users").push();
                error.setVisibility(View.INVISIBLE);
                if (confPasswordET.getText().toString().equals(passwordET.getText().toString())) {
                    User user = new User(userNameET.getText().toString(), passwordET.getText().toString(), nameET.getText().toString(),surenameET.getText().toString(),Double.parseDouble(balanceET.getText().toString()));
                    myRef.setValue(user);
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    startActivity(intent);
                    Intent intent2 = new Intent(getActivity(), MainActivity.class);
                    intent2.putExtra("USER_KEY", userNameET.getText().toString());
                    startActivity(intent2);
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
        backBT = view.findViewById(R.id.backBT);
        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    requireActivity().finish();
                }
            }
        });
    }
}


