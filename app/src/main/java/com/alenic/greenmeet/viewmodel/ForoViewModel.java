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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;
        String miUid = auth.getCurrentUser().getUid();

        //buscar el nombre del usuario en Firebase
        FirebaseFirestore.getInstance().collection("usuarios").document(miUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nombre = "Usuario"; // Valor por defecto por si falla
                    // Si el documento existe, sacamos el campo "nombre"
                    if (documentSnapshot.exists() && documentSnapshot.getString("nombre") != null) {
                        nombre = documentSnapshot.getString("nombre");
                    }
                    // creamos la Duda
                    Duda nueva = new Duda(actUid, creadorActUid, miUid, nombre, pregunta, titulo);

                    repository.addDuda(nueva, new ForoRepository.ForoCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            loadDudas(actUid); // Recargar al enviar
                        }
                        @Override
                        public void onError(String error) {
                        }
                    });
                })
                .addOnFailureListener(e -> {
                });
    }

    public void responder(String dudaId, String respuesta, ForoRepository.ForoCallback<Void> callback) {
        repository.responderDuda(dudaId, respuesta, callback);
    }

    public void loadNotificacionesCombinadas() {
        String myUid = FirebaseAuth.getInstance().getUid();
        if (myUid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ver las dudas para responder
        db.collection("foro")
                .whereEqualTo("creadorActUid", myUid)
                .whereEqualTo("respondida", false)
                .addSnapshotListener((snap1, e1) -> {

                    // Ver las dudas con respuesta y no leído
                    db.collection("foro")
                            .whereEqualTo("userUidPregunta", myUid)
                            .whereEqualTo("respondida", true)
                            .whereEqualTo("leidaUsuario", false)
                            .addSnapshotListener((snap2, e2) -> {

                                List<Duda> listaUnida = new ArrayList<>();

                                if (snap1 != null) {
                                    for (DocumentSnapshot doc : snap1.getDocuments()) {
                                        Duda d = doc.toObject(Duda.class);
                                        d.setId(doc.getId());
                                        listaUnida.add(d);
                                    }
                                }

                                if (snap2 != null) {
                                    for (DocumentSnapshot doc : snap2.getDocuments()) {
                                        Duda d = doc.toObject(Duda.class);
                                        d.setId(doc.getId());
                                        listaUnida.add(d);
                                    }
                                }

                                // Ordenar la listaUnida por fecha
                                listaUnida.sort((d1, d2) -> Long.compare(d2.getFechaCreacion(), d1.getFechaCreacion()));

                                notificaciones.setValue(listaUnida);
                            });
                });
    }
}