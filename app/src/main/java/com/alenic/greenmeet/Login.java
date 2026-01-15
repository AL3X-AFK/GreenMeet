package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView txtPregunta = findViewById(R.id.txtPregunta);

        txtPregunta.setOnClickListener( v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener( v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });
    }
}