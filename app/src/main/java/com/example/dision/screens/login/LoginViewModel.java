package com.example.dision.screens.login;

import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.dision.Repository;
import com.example.dision.helper.NetworkState;
import com.example.dision.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    public LoginViewModel(FirebaseAuth auth, Repository repository){
        this.auth = auth;
        this.repository = repository;
        loadState.observeForever(loadStateObserver);
    }

    private FirebaseAuth auth;
    private Repository repository;
    String email = "";
    String password = "";
    private MutableLiveData<User> _user = new MutableLiveData<>(null);
    LiveData<User> user = _user;

    private MutableLiveData<NetworkState> _loadState = new MutableLiveData<>(NetworkState.IDLE);
    LiveData<NetworkState> loadState = _loadState;

    Observer<NetworkState> loadStateObserver = new Observer<NetworkState>() {
        @Override
        public void onChanged(NetworkState networkState) {
            if (networkState == NetworkState.FAILURE)
                auth.signOut();//logout if login flow fails
        }
    };

    public void login(){
        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 8))
            return;

        _loadState.setValue(NetworkState.LOADING);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        loadOrgList();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LOGINVIEWMODEL", "Couldn't sign in");
                        _loadState.postValue(NetworkState.FAILURE);
                    }
                });
    }

    void checkLoggedIn(){
        if (auth.getCurrentUser() == null)
            return;
        _loadState.setValue(NetworkState.LOADING);
        loadOrgList();
    }

    private void loadOrgList() {
        FirebaseUser authCurrentUser = auth.getCurrentUser();
        if (authCurrentUser == null) _loadState.postValue(NetworkState.FAILURE);
        repository.loadOrgList(authCurrentUser.getUid(), _loadState, _user);
    }

    @Override
    protected void onCleared() {
        loadState.removeObserver(loadStateObserver);
        super.onCleared();
    }
}
