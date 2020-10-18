package com.example.dision.screens.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dision.R;
import com.example.dision.Repository;
import com.example.dision.helper.NetworkState;
import com.example.dision.helper.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {

    private EditText orgNameEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton registerButton;
    private SwitchCompat registerOrgSwitch;
    private LinearLayout loadingScrim;

    private RegisterViewModel viewModel;
    private RegisterFragment thisFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactory factory = new ViewModelFactory(FirebaseAuth.getInstance(), new Repository(FirebaseFirestore.getInstance()));
        viewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);

        orgNameEditText = view.findViewById(R.id.org_name_edittext);
        nameEditText = view.findViewById(R.id.name_edittext);
        emailEditText = view.findViewById(R.id.email_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        registerButton = view.findViewById(R.id.register_button);
        registerOrgSwitch = view.findViewById(R.id.register_org_switch);
        loadingScrim = view.findViewById(R.id.loading_scrim);

        setupUi();
    }

    private void setupUi() {
        registerOrgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    orgNameEditText.setVisibility(View.VISIBLE);
                else {
                    orgNameEditText.setText("");
                    orgNameEditText.setVisibility(View.GONE);
                    viewModel.orgName = null;
                }
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.email = s.toString().trim();
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
                viewModel.password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        orgNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.orgName = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.name = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.register();
            }
        });

        viewModel.loadState.observe(getViewLifecycleOwner(), new Observer<NetworkState>() {
            @Override
            public void onChanged(NetworkState networkState) {
                switch (networkState){
                    case IDLE:
                        loadingScrim.setVisibility(View.GONE);
                        break;
                    case LOADING:
                        loadingScrim.setVisibility(View.VISIBLE);
                        break;
                    case FAILURE:
                        loadingScrim.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Unable to Register, try again", Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        loadingScrim.setVisibility(View.GONE);
                        // Sign Created user out
                        FirebaseAuth.getInstance().signOut();
                        // Navigate back to login screen
                        NavHostFragment.findNavController(thisFragment).popBackStack();
                }
            }
        });
    }
}