package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alenic.greenmeet.data.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText signinEmail,signinPassword,signinNombre;
    private MaterialButton signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signinButton = findViewById(R.id.btnRegister);
        signinEmail = findViewById(R.id.etEmail);
        signinPassword = findViewById(R.id.etPassword);
        signinNombre = findViewById(R.id.etNombre);

        TextView txtPregunta = findViewById(R.id.txtPregunta);

        txtPregunta.setOnClickListener( v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = signinNombre.getText().toString().trim();
                String email = signinEmail.getText().toString().trim();
                String pass = signinPassword.getText().toString().trim();

                if (nombre.isEmpty()) {
                    signinNombre.setError("Campo obligatorio");
                    return;
                }
                if(email.isEmpty()){
                    signinEmail.setError("Este campo es obligatorio");
                }
                if(pass.isEmpty()){
                    signinPassword.setError("Este campo es obligatorio");
                } else{
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String uid = auth.getCurrentUser().getUid();

                                Usuario usuario = new Usuario(
                                        nombre,
                                        "",
                                        ""
                                );

                                db.collection("usuarios")
                                        .document(uid)
                                        .set(usuario)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(Register.this,
                                                    "Registro exitoso", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Register.this, Login.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(Register.this,
                                                        "Error al guardar datos", Toast.LENGTH_SHORT).show()
                                        );
                            }else{
                                Toast.makeText(Register.this,"Registro fallido "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}