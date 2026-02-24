package com.alenic.greenmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.MainActivity;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

//    Activity encargada del inicio de sesión del usuario.
//    Valida los campos de entrada y se comunica con el AuthViewModel
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private AuthViewModel viewModel;
    private TextView txtRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupViewModel();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRedirect = findViewById(R.id.txtPregunta);
    }

    //    Inicializa el ViewModel asociado a esta Activity.
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    //    Configura los listeners de botones y campos de texto.
    private void setupListeners() {

        txtRedirect.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );

        btnLogin.setOnClickListener(v -> validarCampos());

        etEmail.addTextChangedListener(emailWatcher);
        etPassword.addTextChangedListener(passwordWatcher);
    }

    private void validarCampos() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Limpiar errores previos
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Validación de email vacío
        if (email.isEmpty()) {
            emailLayout.setError("Introduce el email");
            return;
        }

        // Validación de formato de email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Email inválido");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Introduce la contraseña");
            return;
        }
    // Si es correcto, llamar al ViewModel
        viewModel.login(email, password);

    }

    //    TextWatcher para limpiar error del email cuando el usuario escribe.
    private final TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

//    TextWatcher para limpiar error de la contraseña cuando el usuario escribe.
    private final TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passwordLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Observa el resultado del login desde el ViewModel.
     * Si es exitoso > abre MainActivity.
     * Si falla > muestra mensaje de error.
     */
    private void observeViewModel() {

        viewModel.getLoginResult().observe(this, success -> {

            if (success == null) return;

            if (success) {
                Toast.makeText(this, "Credenciales correctas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                // Cerrar LoginActivity para que no se pueda volver atrás
                finish();
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}