package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Duda;
import com.alenic.greenmeet.repositories.ForoRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ForoViewModel extends ViewModel {
    private final ForoRepository repository = new ForoRepository();

    private final MutableLiveData<List<Duda>> foroActividad = new MutableLiveData<>();
    public LiveData<List<Duda>> getForoActividad() { return foroActividad; }

    private final MutableLiveData<List<Duda>> notificaciones = new MutableLiveData<>();
    public LiveData<List<Duda>> getNotificaciones() { return notificaciones; }

    public void loadDudas(String actUid) {
        repository.getDudasPorActividad(actUid, new ForoRepository.ForoCallback<List<Duda>>() {
            @Override
            public void onSuccess(List<Duda> result) { foroActividad.setValue(result); }
            @Override
            public void onError(String error) {}
        });
    }

    public void enviarDuda(String actUid, String creadorActUid, String pregunta, String titulo) {
        String miUid = repository.getCurrentUserUid();
        if (miUid == null) return;

        repository.getDatosUsuario(miUid, new ForoRepository.ForoCallback<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                String nombre = "Usuario";
                String fotoUrl = "";

                if (doc.exists()) {
                    if (doc.getString("nombre") != null) nombre = doc.getString("nombre");
                    if (doc.getString("imagenProfileURL") != null) fotoUrl = doc.getString("imagenProfileURL");
                }

                Duda nueva = new Duda(actUid, creadorActUid, miUid, nombre, fotoUrl, pregunta, titulo);
                repository.addDuda(nueva, new ForoRepository.ForoCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) { loadDudas(actUid); }
                    @Override
                    public void onError(String error) {}
                });
            }

            @Override
            public void onError(String error) {}
        });
    }

    public void responder(String dudaId, String respuesta, ForoRepository.ForoCallback<Void> callback) {
        repository.responderDuda(dudaId, respuesta, callback);
    }

    public void loadNotificacionesCombinadas() {
        String miUid = repository.getCurrentUserUid();
        if (miUid == null) return;

        repository.setNotificacionesListener(miUid, new ForoRepository.ForoCallback<List<Duda>>() {
            @Override
            public void onSuccess(List<Duda> result) {
                notificaciones.setValue(result);
            }
            @Override
            public void onError(String error) {}
        });
    }
}