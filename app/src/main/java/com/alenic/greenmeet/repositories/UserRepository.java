package com.alenic.greenmeet.repositories;

import com.alenic.greenmeet.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    //Recoger los datos del usuario que ha iniciado sesión
    public void getUser(UserCallback<User> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                        callback.onSuccess(user);

                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    //Recoger el email del user Auth
    public String getCurrentEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    //Actualizar los datos del usuario
    public void updateProfile(User usuario, UserCallback<Void> callback) {

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("usuarios")
                .document(firebaseUser.getUid())
                .set(usuario)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getUserById(String uid, UserCallback<User> callback) {

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        user.setUid(uid);
                    }
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}