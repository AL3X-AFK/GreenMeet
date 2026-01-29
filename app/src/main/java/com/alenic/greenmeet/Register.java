package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signinEmail,signinPassword;
    private MaterialButton signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        signinButton = findViewById(R.id.btnRegister);
        signinEmail = findViewById(R.id.etEmail);
        signinPassword = findViewById(R.id.etPassword);

        TextView txtPregunta = findViewById(R.id.txtPregunta);

        txtPregunta.setOnClickListener( v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signinEmail.getText().toString().trim();
                String pass = signinPassword.getText().toString().trim();

                if(user.isEmpty()){
                    signinEmail.setError("Este campo es obligatorio");
                }
                if(pass.isEmpty()){
                    signinPassword.setError("Este campo es obligatorio");
                } else{
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this,"Registro exitoso",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this,Login.class));
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