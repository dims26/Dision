package com.example.dision.screens.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dision.R;
import com.example.dision.helper.ViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton loginButton;
    private MaterialButton registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ViewModelFactory factory = new ViewModelFactory();
        LoginViewModel viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        emailEditText = view.findViewById(R.id.email_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);


        //TODO check if logged in and open dashboard

        //TODO set up buttons and textfields
        setupUi();
    }

    private void setupUi() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setEnabled(Patterns.EMAIL_ADDRESS.matcher(s).matches());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setEnabled(count >= 8);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //TODO wire buttons login and register buttons
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}