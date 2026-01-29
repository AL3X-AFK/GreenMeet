package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signinEmail,signinPassword;
    private Button signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        signinButton = findViewById(R.id.btnRegister);
        signinEmail = findViewById(R.id.etEmail);
        signinPassword = findViewById(R.id.etNombre);

        TextView txtPregunta = findViewById(R.id.txtPregunta);

        txtPregunta.setOnClickListener( v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        })
    }
}