package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.repositories.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    //Método para registrarse
    public void register(String nombre, String email, String password) {

        repository.register(nombre, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                success.postValue(true);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }

    //Método para iniciar sesión
    public void login(String email, String password) {

        repository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                success.postValue(true);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }

    public void loginWithGoogle(String idToken, String nombre) {
        repository.loginWithGoogle(idToken, nombre, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() { success.postValue(true); }

            @Override
            public void onError(String errorMessage) { error.postValue(errorMessage); }
        });
    }
}