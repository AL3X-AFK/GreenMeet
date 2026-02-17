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

    public void getAllActs(ActCallback<List<Act>> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .get()
                .addOnSuccessListener(usersSnapshot -> {

                    List<Act> lista = new ArrayList<>();
                    List<Task<?>> tasks = new ArrayList<>();

                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {

                        String ownerUid = userDoc.getId();

                        if (ownerUid.equals(currentUser.getUid())) continue;

                        Task<QuerySnapshot> task = userDoc.getReference()
                                .collection("acciones")
                                .get()
                                .addOnSuccessListener(snapshot -> {

                                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                                        Act act = doc.toObject(Act.class);

                                        if (act != null) {
                                            act.setId(doc.getId());
                                            act.setOwnerUid(ownerUid); // ✅ CORRECTO
                                            lista.add(act);
                                        }
                                    }
                                });

                        tasks.add(task);
                    }

                    Tasks.whenAllComplete(tasks)
                            .addOnSuccessListener(unused ->
                                    callback.onSuccess(lista))
                            .addOnFailureListener(e ->
                                    callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    public void getMyActs(ActCallback<List<Act>> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("acciones")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Act> acts = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Act act = doc.toObject(Act.class);

                        if (act != null) {
                            act.setId(doc.getId()); // 🔥 GUARDAMOS EL ID
                            acts.add(act);
                        }
                    }

                    callback.onSuccess(acts);
                })

                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


    public void addAct(Act act, ActCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(user.getUid())
                .collection("acciones")
                .add(act)
                .addOnSuccessListener(doc -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateAct(Act act, ActCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        if (act.getId() == null) {
            callback.onError("ID de actividad inválido");
            return;
        }

        db.collection("usuarios")
                .document(user.getUid())
                .collection("acciones")
                .document(act.getId())
                .set(act)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void apuntarseActividad(Act act, ActCallback<Void> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fechaInscripcion", FieldValue.serverTimestamp());

        db.collection("usuarios")
                .document(act.getOwnerUid())
                .collection("acciones")
                .document(act.getId())
                .collection("asistentes")
                .document(currentUser.getUid())
                .set(data)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void desapuntarseActividad(Act act, ActCallback<Void> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(act.getOwnerUid())
                .collection("acciones")
                .document(act.getId())
                .collection("asistentes")
                .document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void isUserApuntado(Act act, ActCallback<Boolean> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(act.getOwnerUid())
                .collection("acciones")
                .document(act.getId())
                .collection("asistentes")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onSuccess(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

}