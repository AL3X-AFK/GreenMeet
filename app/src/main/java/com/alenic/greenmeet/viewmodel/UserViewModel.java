package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Usuario;
import com.alenic.greenmeet.repositories.UserRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> state = new MutableLiveData<>();

    public UserViewModel() {
        repository = new UserRepository();
    }

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getState() {
        return state;
    }

    public void loadUser() {

        email.setValue(repository.getCurrentEmail());

        repository.getUser(new UserRepository.UserCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                usuario.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void updateProfile(String nombre,
                              String telefono,
                              String genero,
                              String passwordActual,
                              String emailNuevo) {

        repository.updateProfile(nombre, telefono, genero,
                passwordActual, emailNuevo,
                new UserRepository.UserCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        state.setValue("UPDATE_SUCCESS");
                        loadUser(); // refrescar datos
                    }

                    @Override
                    public void onError(String error) {
                        state.setValue(error);
                    }
                });
    }

    public void clearSession() {
        usuario.setValue(null);
        email.setValue(null);
    }

    public void clearState() {
        state.setValue(null);
    }
}