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
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface ForoCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // Obtener UID del usuario actual
    public String getCurrentUserUid() {
        return (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;
    }

    // Obtener datos del perfil para la duda
    public void getDatosUsuario(String uid, ForoCallback<DocumentSnapshot> callback) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
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

    // Listener de notificaciones combinadas
    public void setNotificacionesListener(String uid, ForoCallback<List<Duda>> callback) {
        // Query 1: Dudas que debo responder
        db.collection("foro")
                .whereEqualTo("creadorActUid", uid)
                .whereEqualTo("respondida", false)
                .addSnapshotListener((snap1, e1) -> {
                    if (e1 != null) return;

                    // Query 2: Respuestas que he recibido y no he leído
                    db.collection("foro")
                            .whereEqualTo("userUidPregunta", uid)
                            .whereEqualTo("respondida", true)
                            .whereEqualTo("leidaUsuario", false)
                            .addSnapshotListener((snap2, e2) -> {
                                if (e2 != null) return;

                                List<Duda> listaUnida = new ArrayList<>();
                                if (snap1 != null) {
                                    for (DocumentSnapshot doc : snap1.getDocuments()) {
                                        Duda d = doc.toObject(Duda.class);
                                        if (d != null) { d.setId(doc.getId()); listaUnida.add(d); }
                                    }
                                }
                                if (snap2 != null) {
                                    for (DocumentSnapshot doc : snap2.getDocuments()) {
                                        Duda d = doc.toObject(Duda.class);
                                        if (d != null) { d.setId(doc.getId()); listaUnida.add(d); }
                                    }
                                }
                                // Ordenar por fecha descendente
                                listaUnida.sort((d1, d2) -> Long.compare(d2.getFechaCreacion(), d1.getFechaCreacion()));
                                callback.onSuccess(listaUnida);
                            });
                });
    }

    public void responderDuda(String dudaId, String respuesta, ForoCallback<Void> callback) {
        db.collection("foro").document(dudaId)
                .update("respuesta", respuesta, "respondida", true)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void marcarComoLeida(String dudaId) {
        db.collection("foro").document(dudaId).update("leidaUsuario", true);
    }
}