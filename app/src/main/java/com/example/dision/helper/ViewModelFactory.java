package com.example.dision.helper;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.dision.Repository;
import com.example.dision.screens.login.LoginViewModel;
import com.example.dision.screens.register.RegisterViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private FirebaseAuth auth;
    private Repository repository;

    public ViewModelFactory(FirebaseAuth auth) {
        this.auth = auth;
    }

    public ViewModelFactory(FirebaseAuth auth, Repository repository) {
        this.auth = auth;
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class))
            return (T) new LoginViewModel(auth, repository);
        else if (modelClass.isAssignableFrom(RegisterViewModel.class))
            return (T) new RegisterViewModel(auth, repository);
        else throw new IllegalArgumentException("Unsupported ViewModel class: " + modelClass.getName());
    }
}
