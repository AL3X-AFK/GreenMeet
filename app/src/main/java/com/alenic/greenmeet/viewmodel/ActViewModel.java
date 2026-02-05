package com.alenic.greenmeet.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Act;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ActViewModel extends ViewModel {

    // Lista completa de actividades
    private final MutableLiveData<List<Act>> acts = new MutableLiveData<>();

    // Actividad seleccionada
    private final MutableLiveData<Act> selectedAct = new MutableLiveData<>();

    // Campos individuales de la actividad seleccionada
    private final MutableLiveData<String> titulo = new MutableLiveData<>();
    private final MutableLiveData<String> descripcion = new MutableLiveData<>();
    private final MutableLiveData<String> fecha = new MutableLiveData<>();
    private final MutableLiveData<String> ubicacion = new MutableLiveData<>();
    private final MutableLiveData<String> imagenUrl = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // ---------------- Lista de actividades ----------------
    public LiveData<List<Act>> getActs() {
        return acts;
    }

    public void loadActs() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("ActViewModel", "Usuario no logueado");
            return;
        }

        db.collection("usuarios")
                .document(user.getUid())
                .collection("acciones") // cambia a "actividades" si tu subcolección se llama así
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Act> lista = snapshot.toObjects(Act.class);
                    acts.setValue(lista);
                });
    }

    // ---------------- Actividad seleccionada ----------------
    public LiveData<Act> getSelectedAct() {
        return selectedAct;
    }

    public void selectAct(Act act) {
        selectedAct.setValue(act);

        // Actualizamos campos individuales
        titulo.setValue(act.getTitulo());
        descripcion.setValue(act.getDescripcion());
        fecha.setValue(act.getFecha());
        ubicacion.setValue(act.getUbicacion());
        imagenUrl.setValue(act.getImagenUrl());
    }

    // ---------------- Campos individuales ----------------
    public LiveData<String> getTitulo() { return titulo; }
    public LiveData<String> getDescripcion() { return descripcion; }
    public LiveData<String> getFecha() { return fecha; }
    public LiveData<String> getUbicacion() { return ubicacion; }
    public LiveData<String> getImagenUrl() { return imagenUrl; }
}
