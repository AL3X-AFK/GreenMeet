package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.User;
import com.alenic.greenmeet.repositories.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    private final MutableLiveData<User> usuario = new MutableLiveData<>();
    private final MutableLiveData<String> state = new MutableLiveData<>();

    public UserViewModel() {
        repository = new UserRepository();
    }

    public LiveData<User> getUsuario() {
        return usuario;
    }

    public String getEmail() {
        return repository.getCurrentEmail();
    }

    public LiveData<String> getState() {
        return state;
    }

    // Cargar los datos del usuario sobre el objeto User
    public void loadUser() {

        repository.getUser(new UserRepository.UserCallback<>() {
            @Override
            public void onSuccess(User result) {
                usuario.setValue(result);
            }
            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    //Actualizar los datos del usuario
    public void updateProfile(String nombre,
                              String telefono,
                              String genero) {

        User usuarioActual = usuario.getValue();
        if (usuarioActual == null) {
            state.setValue("Error inesperado");
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setTelefono(telefono);
        usuarioActual.setGenero(genero);

        repository.updateProfile(usuarioActual,
                new UserRepository.UserCallback<>() {
                    @Override
                    public void onSuccess(Void result) {
                        state.setValue("UPDATE_SUCCESS");
                    }

                    @Override
                    public void onError(String error) {
                        state.setValue(error);
                    }
                });
    }

    public void clearSession() {
        usuario.setValue(null);
    }

    public void clearState() {
        state.setValue(null);
    }
}