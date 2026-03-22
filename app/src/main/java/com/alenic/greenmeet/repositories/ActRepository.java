package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    // Obtener todas las actividades ordenadas por fecha de creación (menos las mías)
    public void getAllActsByCreate(ActCallback<List<Act>> callback) {

        // Validar si el usuario ha iniciado sesión
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        // Recorremos la colección de Firebase
        db.collection("acciones")
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Act> acts = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Act act = doc.toObject(Act.class);
                        if (act != null) {
                            act.setUid(doc.getId());
                            // Excluir mis propias actividades
                            if (!currentUser.getUid().equals(act.getUserUid())) {
                                acts.add(act);
                            }
                        }
                    }
                    callback.onSuccess(acts);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Obtener todas las actividades ordenadas por fecha de actividad (menos las mías y las actividades pasadas)
    public void getAllActsByDate(ActCallback<List<Act>> callback) {

        // Validar si el usuario ha iniciado sesión
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
                    List<Act> acts = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Act act = doc.toObject(Act.class);
                        if (act != null) {
                            act.setUid(doc.getId());
                            // Excluir mis propias actividades
                            if (!currentUser.getUid().equals(act.getUserUid())) {
                                acts.add(act);
                            }
                        }
                    }
                    callback.onSuccess(acts);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Obtener solo mis actividades
    public void getMyActs(ActCallback<List<Act>> callback) {

        // Validar si el usuario ha iniciado sesión
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
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Crear actividad
    public void addAct(Act act, ActCallback<Void> callback) {

        // Validar si el usuario ha iniciado sesión
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        act.setUserUid(currentUser.getUid());
        act.setFechaCreacion(System.currentTimeMillis());

        db.collection("acciones")
                .add(act)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Actualizar actividad
    public void updateAct(Act act, ActCallback<Void> callback) {

        // Validar si el usuario ha iniciado sesión
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        // Validar si la actividad existe
        if (act.getUid() == null) {
            callback.onError("ID inválido");
            return;
        }

        //Modicar actividad
        db.collection("acciones")
                .document(act.getUid())
                .set(act)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Apuntarse a una actividad
    public void apuntarseActividad(Act act, ActCallback<Void> callback) {

        // Validar si el usuario ha iniciado sesión
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        // Esto es momentaneo, lo sustituiremos por un objeto de tipo Asistente
        Map<String, Object> data = new HashMap<>();
        data.put("fechaInscripcion", FieldValue.serverTimestamp());

        //Guardamos los datos
        db.collection("acciones")
                .document(act.getUid())
                .collection("asistentes")
                .document(currentUser.getUid())
                .set(data)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Desapuntarse de un a actividad (Borrar el documento)
    public void desapuntarseActividad(Act act, ActCallback<Void> callback) {

        // Validar si el usuario ha iniciado sesión
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

        // Validar si el usuario ha iniciado sesión
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        //De momento, solo verifica si el documento existe o no (Si participamos en la actividad)
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


    // Recoger todas las actividades a las que el usuario esta apuntado (Actividades pasadas)
    public void getPastActsForUser(ActCallback<List<Act>> callback) {

        // Validar si el usuario ha iniciado sesión
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

                    final int totalDocs = documents.size();
                    final int[] processedCount = {0};

                    for (DocumentSnapshot doc : documents) {
                        Act act = doc.toObject(Act.class);
                        if (act != null) {
                            act.setUid(doc.getId());
                                isUserApuntado(act, new ActCallback<>() {
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
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Recoger todas las actividades a las que el usuario esta apuntado (Proximas a la fecha actual)
    public void getNextActsForUser(ActCallback<List<Act>> callback) {

        // Validar si el usuario ha iniciado sesión
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

                    final int totalDocs = documents.size();
                    final int[] processedCount = {0};

                    for (DocumentSnapshot doc : documents) {
                        Act act = doc.toObject(Act.class);
                        if (act != null) {
                            act.setUid(doc.getId());
                            isUserApuntado(act, new ActCallback<>() {
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
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Obtener la lista de objetos User que están apuntados a una actividad
    public void getAsistentesByAct(String actUid, ActCallback<List<User>> callback) {
        db.collection("acciones")
                .document(actUid)
                .collection("asistentes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> listaUsuarios = new ArrayList<>();
                    int totalAsistentes = querySnapshot.size();

                    if (totalAsistentes == 0) {
                        callback.onSuccess(listaUsuarios);
                        return;
                    }

                    // Contador para controlar las peticiones asíncronas
                    final int[] procesados = {0};

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userUid = doc.getId(); // El ID del doc es el UID del usuario

                        // Reutilizamos la lógica de buscar usuario por ID
                        // (Asumiendo que tienes acceso a UserRepository o la misma lógica aquí)
                        db.collection("usuarios").document(userUid).get()
                                .addOnSuccessListener(userDoc -> {
                                    User usuario = userDoc.toObject(User.class);
                                    if (usuario != null) {
                                        usuario.setUid(userDoc.getId());
                                        listaUsuarios.add(usuario);
                                    }

                                    procesados[0]++;
                                    if (procesados[0] == totalAsistentes) {
                                        callback.onSuccess(listaUsuarios);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    procesados[0]++;
                                    if (procesados[0] == totalAsistentes) {
                                        callback.onSuccess(listaUsuarios);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

}
