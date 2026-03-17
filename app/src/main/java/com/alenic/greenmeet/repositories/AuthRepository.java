package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
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

    // Registro
    public void register(String nombre, String email, String password, AuthCallback callback) {
        //Crear el usuario en Auth, si todo va bien guardar usuario en coleccion
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    //Inicializar el usuario solo con nombre
                    User usuario = new User(nombre, "", "");

                    db.collection("usuarios")
                            .document(uid)
                            .set(usuario)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));

                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Login
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void loginWithGoogle(String idToken, String nombreSugerido, AuthCallback callback) {
        // Crear credencial Firebase desde el token de Google
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    // ¿Es la primera vez que entra con Google?
                    db.collection("usuarios").document(uid).get()
                            .addOnSuccessListener(doc -> {
                                if (!doc.exists()) {
                                    // Primera vez pues guardar con nombre elegido
                                    User usuario = new User(nombreSugerido, "", "");
                                    db.collection("usuarios").document(uid)
                                            .set(usuario)
                                            .addOnSuccessListener(unused -> callback.onSuccess())
                                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                                } else {
                                    // Ya existe pues login directo
                                    callback.onSuccess();
                                }
                            })
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    //Log out
    public void logout() {
        auth.signOut();
    }

}