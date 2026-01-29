package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail,loginPassword;
    private TextView txtRedirect;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        txtRedirect = findViewById(R.id.txtPregunta);
        loginEmail = findViewById(R.id.etEmail);
        loginPassword = findViewById(R.id.etPassword);

        txtRedirect.setOnClickListener( v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this,"Inicio de sesión exitoso",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this,MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this,"Inicio de sesión fallida",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else{
                        loginPassword.setError("Este campo no puede estar vacío.");
                    }
                } else if(email.isEmpty()){
                    loginEmail.setError("Este campo no puede estar vacío");
                } else {
                    loginEmail.setError("Introduce un email válido");
                }
            }
        });
    }
}