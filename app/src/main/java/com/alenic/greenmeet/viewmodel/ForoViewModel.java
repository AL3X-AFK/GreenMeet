package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Duda;
import com.alenic.greenmeet.repositories.ForoRepository;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class ForoViewModel extends ViewModel {
    private final ForoRepository repository = new ForoRepository();
    private final MutableLiveData<List<Duda>> foroActividad = new MutableLiveData<>();
    public LiveData<List<Duda>> getForoActividad() { return foroActividad; }
    private MutableLiveData<List<Duda>> dudasPendientes = new MutableLiveData<>();
    public LiveData<List<Duda>> getDudasPendientes() { return dudasPendientes; }

    public void loadDudas(String actUid) {
        repository.getDudasPorActividad(actUid, new ForoRepository.ForoCallback<List<Duda>>() {
            @Override
            public void onSuccess(List<Duda> result) { foroActividad.setValue(result); }
            @Override
            public void onError(String error) {}
        });
    }

    public void enviarDuda(String actUid, String creadorActUid, String pregunta, String titulo) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        // Intentamos pillar el nombre del perfil de Google/Firebase
        String nombre = auth.getCurrentUser().getDisplayName();
        if (nombre == null || nombre.isEmpty()) nombre = "Usuario";

        Duda nueva = new Duda(actUid, creadorActUid, auth.getCurrentUser().getUid(), nombre, pregunta,titulo);

        repository.addDuda(nueva, new ForoRepository.ForoCallback<Void>() {
            @Override
            public void onSuccess(Void result) { loadDudas(actUid); } // Recargar al enviar
            @Override
            public void onError(String error) {}
        });
    }

    public void loadDudasPendientes() {
        repository.getDudasPendientes(new ForoRepository.ForoCallback<List<Duda>>() {
            @Override
            public void onSuccess(List<Duda> result) {
                dudasPendientes.setValue(result);
                // El count se actualiza automáticamente si observas el size de esta lista
            }
            @Override public void onError(String error) {}
        });
    }

    public void responder(String dudaId, String respuesta, ForoRepository.ForoCallback<Void> callback) {
        repository.responderDuda(dudaId, respuesta, callback);
    }
}