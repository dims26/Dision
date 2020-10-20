package com.example.dision.screens.register;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.dision.Repository;
import com.example.dision.helper.NetworkState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {

    //pass in repo class to handle all backend communication
    public RegisterViewModel(FirebaseAuth auth, Repository repository){
        this.auth = auth;
        this.repository = repository;
        loadState.observeForever(loadStateObserver);
    }

    private FirebaseAuth auth;
    private Repository repository;
    String orgName = "";
    String name = "";
    String email = "";
    String password = "";

    private MutableLiveData<NetworkState> _loadState = new MutableLiveData<>(NetworkState.IDLE);
    LiveData<NetworkState> loadState = _loadState;

    Observer<NetworkState> loadStateObserver = new Observer<NetworkState>() {
        @Override
        public void onChanged(NetworkState networkState) {
            if (networkState == NetworkState.FAILURE || networkState == NetworkState.SUCCESS)
                auth.signOut();
        }
    };

    public void register() {
        if (!(name.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 8))
            return;

        _loadState.setValue(NetworkState.LOADING);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        assert user != null;
                        if (orgName == null) {
                            createUserRecord(user);
                        }
                        else
                            createUserOrgRecord(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        _loadState.postValue(NetworkState.FAILURE);
                    }
                });
    }

    private void createUserRecord(FirebaseUser user) {
        //Create User document containing email, name, and other relevant info
        //noinspection ConstantConditions
        repository.createUserRecord(name, user.getEmail(), user.getUid(), _loadState);
    }

    private void createUserOrgRecord(FirebaseUser user) {
        //Create organization with unique ID and add currently signed user
        //noinspection ConstantConditions
        repository.createUserAndOrgRecord(orgName, name, user.getEmail(), user.getUid(), _loadState);
    }

    @Override
    protected void onCleared() {
        loadState.removeObserver(loadStateObserver);
        super.onCleared();
    }
}
