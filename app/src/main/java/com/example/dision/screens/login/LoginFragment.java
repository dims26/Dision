package com.example.dision.screens.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dision.R;
import com.example.dision.Repository;
import com.example.dision.helper.NetworkState;
import com.example.dision.helper.ViewModelFactory;
import com.example.dision.screens.list_bottomsheet.ItemListCallback;
import com.example.dision.screens.list_bottomsheet.ItemListDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginFragment extends Fragment implements ItemListCallback {

    private static final String TAG = LoginFragment.class.getName();
    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton loginButton;
    private MaterialButton registerButton;
    private LinearLayout loadingScrim;

    private LoginFragment thisFragment = this;
    private LoginViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ViewModelFactory factory = new ViewModelFactory(mAuth, new Repository(FirebaseFirestore.getInstance()));
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        emailEditText = view.findViewById(R.id.email_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);
        loadingScrim = view.findViewById(R.id.loading_scrim);

        //check if logged in and load appropriate data
        viewModel.checkLoggedIn();

        //set up buttons and textfields
        setupUi();
    }

    private void setupUi() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.email = s.toString();
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

        //TODO wire buttons login and register buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.login();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
                NavHostFragment.findNavController(thisFragment).navigate(direction);
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
                        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        loadingScrim.setVisibility(View.GONE);
                        //TODO Show organization selection in modal bottom sheet
                        ArrayList<String> orgNames = new ArrayList<>(viewModel.user.getValue().getOrganizations().keySet());
                        ItemListDialogFragment bottomSheet = ItemListDialogFragment.newInstance(orgNames, (ItemListCallback) thisFragment);
                        bottomSheet.show(thisFragment.getChildFragmentManager(), TAG);
                        break;
                }
            }
        });
    }

    @Override
    public void onItemSelected(Object value) {
        //TODO Resolve the value from view model User and navigate
        Toast.makeText(getContext(), viewModel.user.getValue().getOrganizations().get(value).toString(), Toast.LENGTH_SHORT).show();
    }
}