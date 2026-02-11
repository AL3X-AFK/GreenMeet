package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }

    // 🔹 Registro
    public void register(String nombre, String email, String password, AuthCallback callback) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = auth.getCurrentUser().getUid();

                    Usuario usuario = new Usuario(nombre, "", "");

                    db.collection("usuarios")
                            .document(uid)
                            .set(usuario)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e ->
                                    callback.onError("Error al guardar datos"));

                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    // 🔹 Login
    public void login(String email, String password, AuthCallback callback) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}