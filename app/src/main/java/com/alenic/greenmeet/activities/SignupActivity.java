package com.alenic.greenmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.MainActivity;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout,nameLayout;
    private EditText etEmail, etPassword, etNombre;
    private MaterialButton btnRegister;
    private AuthViewModel viewModel;
    private TextView txtPregunta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupViewModel();
        setupListeners();
        observeViewModel();
    }

    private void initViews(){
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        nameLayout = findViewById(R.id.nameLayout);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        btnRegister = findViewById(R.id.btnRegister);
        txtPregunta = findViewById(R.id.txtPregunta);
    }

    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupListeners(){
        txtPregunta.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> validarCampos());

        etEmail.addTextChangedListener(emailWatcher);
        etPassword.addTextChangedListener(passwordWatcher);
        etNombre.addTextChangedListener(nameWatcher);

    }

    private void validarCampos() {

        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        emailLayout.setError(null);
        passwordLayout.setError(null);
        nameLayout.setError(null);

        if (nombre.isEmpty()) {
            nameLayout.setError("Introduce el nombre");
            return;
        }

        if (email.isEmpty()) {
            emailLayout.setError("Introduce el email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Email inválido");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Introduce la contraseña");
            return;
        }

        viewModel.register(nombre, email, password);

    }

    private final TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private final TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passwordLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private final TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void observeViewModel() {

        viewModel.getSuccess().observe(this, isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        viewModel.getError().observe(this, errorMessage -> {
            Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show();
        });
    }
}
