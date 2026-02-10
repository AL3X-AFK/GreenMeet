package com.alenic.greenmeet.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Act;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class ActViewModel extends ViewModel {

    // Lista completa de actividades
    private final MutableLiveData<List<Act>> acts = new MutableLiveData<>();

    // ---------------- Para InscriptionsFragment ----------------
    private final MutableLiveData<List<Act>> actsProximos = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsRealizadas = new MutableLiveData<>();

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

    // ---------------- Lista completa ----------------
    public LiveData<List<Act>> getActs() { return acts; }

    public void loadActs() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("usuarios")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    List<Act> lista = new ArrayList<>();
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        if (userDoc.getId().equals(currentUser.getUid())) continue;
                        tasks.add(userDoc.getReference().collection("acciones").get());
                    }

                    if (tasks.isEmpty()) {
                        acts.setValue(lista);
                        return;
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {
                                for (Object snapObj : results) {
                                    QuerySnapshot snap = (QuerySnapshot) snapObj;
                                    lista.addAll(snap.toObjects(Act.class));
                                }
                                acts.setValue(lista);
                            })
                            .addOnFailureListener(e -> Log.e("ActViewModel", "Error cargando actividades", e));
                })
                .addOnFailureListener(e -> Log.e("ActViewModel", "Error cargando usuarios", e));
    }

    // ---------------- Próximas y realizadas ----------------
    public LiveData<List<Act>> getActsProximos() { return actsProximos; }
    public LiveData<List<Act>> getActsRealizadas() { return actsRealizadas; }

    public void loadActsProximos() { loadActsByDate(true); }
    public void loadActsRealizadas() { loadActsByDate(false); }

    // Método interno para filtrar por fecha
    private void loadActsByDate(boolean proximos) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("usuarios")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    List<Act> lista = new ArrayList<>();
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        if (!userDoc.getId().equals(currentUser.getUid())) continue;
                        tasks.add(userDoc.getReference().collection("acciones").get());
                    }

                    if (tasks.isEmpty()) {
                        if (proximos) actsProximos.setValue(lista);
                        else actsRealizadas.setValue(lista);
                        return;
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {
                                long now = System.currentTimeMillis();
                                for (Object snapObj : results) {
                                    QuerySnapshot snap = (QuerySnapshot) snapObj;
                                    for (Act act : snap.toObjects(Act.class)) {
                                        if (act.getCreatedAt() >= now && proximos) lista.add(act);
                                        else if (act.getCreatedAt() < now && !proximos) lista.add(act);
                                    }
                                }
                                if (proximos) actsProximos.setValue(lista);
                                else actsRealizadas.setValue(lista);
                            })
                            .addOnFailureListener(e -> {
                                if (proximos) actsProximos.setValue(new ArrayList<>());
                                else actsRealizadas.setValue(new ArrayList<>());
                                Log.e("ActViewModel", "Error cargando actividades filtradas", e);
                            });
                })
                .addOnFailureListener(e -> Log.e("ActViewModel", "Error cargando usuarios", e));
    }

    // ---------------- Actividad seleccionada ----------------
    public LiveData<Act> getSelectedAct() { return selectedAct; }

    public void selectAct(Act act) {
        selectedAct.setValue(act);
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
