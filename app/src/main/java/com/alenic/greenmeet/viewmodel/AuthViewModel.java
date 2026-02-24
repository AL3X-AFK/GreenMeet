package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.repositories.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<String> authState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<String> getAuthState() {
        return authState;
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }


    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();

    public void register(String nombre, String email, String password) {
        loading.setValue(true);

        repository.register(nombre, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                loading.postValue(false);
                authState.postValue("REGISTER_SUCCESS");
            }

            @Override
            public void onError(String error) {
                loading.postValue(false);
                authState.postValue(error);
            }
        });
    }

    public void login(String email, String password) {
        loading.setValue(true);

        repository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                loginResult.postValue(true);
            }

            @Override
            public void onError(String error) {
                loginResult.postValue(false);
            }
        });
    }
}
