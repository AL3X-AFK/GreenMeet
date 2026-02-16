package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.Act;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        if (userDoc.getId().equals(currentUser.getUid())) continue;
                        tasks.add(userDoc.getReference()
                                .collection("acciones")
                                .get());
                    }

                    if (tasks.isEmpty()) {
                        callback.onSuccess(lista);
                        return;
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {

                                for (Object snapObj : results) {
                                    QuerySnapshot snap = (QuerySnapshot) snapObj;
                                    for (DocumentSnapshot doc : snap.getDocuments()) {
                                        Act act = doc.toObject(Act.class);
                                        if (act != null) {
                                            act.setId(doc.getId()); // GUARDAMOS EL ID
                                            lista.add(act);
                                        }
                                    }

                                }

                                callback.onSuccess(lista);
                            })
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

}