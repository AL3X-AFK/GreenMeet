package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public interface UserCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }

    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void getUser(UserCallback<Usuario> callback) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess(doc.toObject(Usuario.class));
                    } else {
                        callback.onError("Usuario no encontrado");
                    }
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    public String getCurrentEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public void updateProfile(String nombre,
                              String telefono,
                              String genero,
                              String passwordActual,
                              String emailNuevo,
                              UserCallback<Void> callback) {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), passwordActual);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    user.verifyBeforeUpdateEmail(emailNuevo)
                            .addOnSuccessListener(unused2 -> {

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("nombre", nombre);
                                updates.put("telefono", telefono);
                                updates.put("genero", genero);

                                db.collection("usuarios")
                                        .document(user.getUid())
                                        .update(updates)
                                        .addOnSuccessListener(unused3 ->
                                                callback.onSuccess(null))
                                        .addOnFailureListener(e ->
                                                callback.onError(e.getMessage()));
                            });
                })
                .addOnFailureListener(e ->
                        callback.onError("Contraseña incorrecta"));
    }
}