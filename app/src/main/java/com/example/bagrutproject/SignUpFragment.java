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

public class SignUpFragment extends Fragment {
    Button registerBT, backBT;
    EditText userNameET, passwordET, confPasswordET;
    TextView error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerBT = view.findViewById(R.id.register);
        userNameET = view.findViewById(R.id.username);
        passwordET = view.findViewById(R.id.password);
        confPasswordET = view.findViewById(R.id.confPassword);
        error = view.findViewById(R.id.error);
        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://bagrut-project-129a3-default-rtdb.firebaseio.com/");
                DatabaseReference myRef = database.getReference("Users").push();
                error.setVisibility(View.INVISIBLE);
                if (confPasswordET.getText().toString().equals(passwordET.getText().toString())) {
                    User user = new User(userNameET.getText().toString(), passwordET.getText().toString());
                    myRef.setValue(user);
                    Intent intent = new Intent(requireActivity(), MainActivity2.class);
                    startActivity(intent);
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


