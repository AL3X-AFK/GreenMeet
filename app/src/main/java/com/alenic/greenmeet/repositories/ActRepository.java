package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.Act;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public interface ActCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }

    public ActRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // Obtener todas las actividades (menos las mías)
    public void getAllActsByCreate(ActCallback<List<Act>> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("acciones")
                .orderBy("fechaCreacion")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Act> lista = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Act act = doc.toObject(Act.class);

                        if (act != null) {
                            act.setUid(doc.getId());

                            // Excluir mis propias actividades
                            if (!currentUser.getUid().equals(act.getUserUid())) {
                                lista.add(act);
                            }
                        }
                    }

                    callback.onSuccess(lista);
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    public void getAllActsByFecha(ActCallback<List<Act>> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }
        long currentTime = System.currentTimeMillis();

        db.collection("acciones")
                .whereGreaterThanOrEqualTo("fecha", currentTime)
                .orderBy("fecha")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Act> lista = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Act act = doc.toObject(Act.class);
                        if (act != null) {
                            act.setUid(doc.getId());
                            // Excluir mis propias actividades
                            if (!currentUser.getUid().equals(act.getUserUid())) {
                                lista.add(act);
                            }
                        }
                    }
                    callback.onSuccess(lista);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getNextActsForUser(ActCallback<List<Act>> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        long currentTime = System.currentTimeMillis();

        db.collection("acciones")
                .whereGreaterThanOrEqualTo("fecha", currentTime)
                .orderBy("fecha")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Act> lista = new ArrayList<>();
                    List<DocumentSnapshot> documents = snapshot.getDocuments();

                    if (documents.isEmpty()) {
                        callback.onSuccess(lista);
                        return;
                    }

                    final int totalDocs = documents.size();
                    final int[] processedCount = {0};

                    for (DocumentSnapshot doc : documents) {

                        Act act = doc.toObject(Act.class);

                        if (act != null) {
                            act.setUid(doc.getId());

                            if (!currentUser.getUid().equals(act.getUserUid())) {

                                isUserApuntado(act, new ActCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean isApuntado) {

                                        if (isApuntado) {
                                            lista.add(act);
                                        }

                                        processedCount[0]++;

                                        if (processedCount[0] == totalDocs) {
                                            callback.onSuccess(lista);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        processedCount[0]++;

                                        if (processedCount[0] == totalDocs) {
                                            callback.onSuccess(lista);
                                        }
                                    }
                                });

                            } else {
                                processedCount[0]++;
                                if (processedCount[0] == totalDocs) {
                                    callback.onSuccess(lista);
                                }
                            }
                        } else {
                            processedCount[0]++;
                            if (processedCount[0] == totalDocs) {
                                callback.onSuccess(lista);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


    // Obtener solo mis actividades
    public void getMyActs(ActCallback<List<Act>> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("acciones")
                .whereEqualTo("userUid", currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Act> acts = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Act act = doc.toObject(Act.class);

                        if (act != null) {
                            act.setUid(doc.getId());
                            acts.add(act);
                        }
                    }

                    callback.onSuccess(acts);
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    // Añadir actividad
    public void addAct(Act act, ActCallback<Void> callback) {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        act.setUserUid(user.getUid());
        act.setFechaCreacion(System.currentTimeMillis());

        db.collection("acciones")
                .add(act)
                .addOnSuccessListener(doc -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Actualizar actividad
    public void updateAct(Act act, ActCallback<Void> callback) {

        if (act.getUid() == null) {
            callback.onError("ID inválido");
            return;
        }

        db.collection("acciones")
                .document(act.getUid())
                .set(act)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Apuntarse
    public void apuntarseActividad(Act act, ActCallback<Void> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fechaInscripcion", FieldValue.serverTimestamp());

        db.collection("acciones")
                .document(act.getUid())
                .collection("asistentes")
                .document(currentUser.getUid())
                .set(data)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // 🔹 Desapuntarse
    public void desapuntarseActividad(Act act, ActCallback<Void> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("acciones")
                .document(act.getUid())
                .collection("asistentes")
                .document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Comprobar si está apuntado
    public void isUserApuntado(Act act, ActCallback<Boolean> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("acciones")
                .document(act.getUid())
                .collection("asistentes")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot ->
                        callback.onSuccess(documentSnapshot.exists()))
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }


    public void getPastActsForUser(ActCallback<List<Act>> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        long currentTime = System.currentTimeMillis();

        db.collection("acciones")
                .whereLessThan("fecha", currentTime)
                .orderBy("fecha")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Act> lista = new ArrayList<>();
                    List<DocumentSnapshot> documents = snapshot.getDocuments();

                    if (documents.isEmpty()) {
                        callback.onSuccess(lista);
                        return;
                    }

                    final int totalDocs = documents.size();
                    final int[] processedCount = {0};

                    for (DocumentSnapshot doc : documents) {

                        Act act = doc.toObject(Act.class);

                        if (act != null) {
                            act.setUid(doc.getId());

                            if (!currentUser.getUid().equals(act.getUserUid())) {

                                isUserApuntado(act, new ActCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean isApuntado) {

                                        if (isApuntado) {
                                            lista.add(act);
                                        }

                                        processedCount[0]++;

                                        if (processedCount[0] == totalDocs) {
                                            callback.onSuccess(lista);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        processedCount[0]++;

                                        if (processedCount[0] == totalDocs) {
                                            callback.onSuccess(lista);
                                        }
                                    }
                                });

                            } else {
                                processedCount[0]++;
                                if (processedCount[0] == totalDocs) {
                                    callback.onSuccess(lista);
                                }
                            }
                        } else {
                            processedCount[0]++;
                            if (processedCount[0] == totalDocs) {
                                callback.onSuccess(lista);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

}
