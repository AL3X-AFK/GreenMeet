package com.alenic.greenmeet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void loadUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        // Email SIEMPRE desde Auth
        email.setValue(currentUser.getEmail());

        db.collection("usuarios")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Usuario u = doc.toObject(Usuario.class);
                        usuario.setValue(u);
                    }
                });
    }

    public void updateProfile(String nombre,
                              String telefono,
                              String genero,
                              Runnable onSuccess,
                              Runnable onWrongPassword,
                              String passwordActual,
                              String emailNuevo) {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

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
                                        .addOnSuccessListener(unused3 -> {

                                            usuario.setValue(
                                                    new Usuario(nombre, telefono, genero)
                                            );
                                            email.setValue(emailNuevo);

                                            onSuccess.run();
                                        });
                            });
                })
                .addOnFailureListener(e -> onWrongPassword.run());
    }

    public void clearSession() {
        usuario.setValue(null);
        email.setValue(null);
    }
}