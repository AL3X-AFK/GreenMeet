package com.alenic.greenmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etNombre;
    private MaterialButton btnRegister;
    private AuthViewModel viewModel;
    private TextView txtPregunta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        btnRegister = findViewById(R.id.btnRegister);
        txtPregunta = findViewById(R.id.txtPregunta);

        txtPregunta.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {

            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("Campo obligatorio");
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Campo obligatorio");
                return;
            }

            if (pass.isEmpty()) {
                etPassword.setError("Campo obligatorio");
                return;
            }

            viewModel.register(nombre, email, pass);
        });

        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getAuthState().observe(this, state -> {

            if (state.equals("REGISTER_SUCCESS")) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
