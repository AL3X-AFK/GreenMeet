package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.Duda;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.Query;

public class ForoRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface ForoCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public void addDuda(Duda duda, ForoCallback<Void> callback) {
        db.collection("foro").add(duda)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getDudasPorActividad(String actUid, ForoCallback<List<Duda>> callback) {
        db.collection("foro")
                .whereEqualTo("actUid", actUid)
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Duda> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Duda duda = doc.toObject(Duda.class);
                        if (duda != null) {
                            duda.setId(doc.getId());
                            lista.add(duda);
                        }
                    }
                    callback.onSuccess(lista);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void responderDuda(String dudaId, String respuesta, ForoCallback<Void> callback) {
        db.collection("foro").document(dudaId)
                .update("respuesta", respuesta, "respondida", true)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Marcar la notificacion como leida
    public void marcarComoLeida(String dudaId) {
        db.collection("foro").document(dudaId).update("leidaUsuario", true);
    }
}