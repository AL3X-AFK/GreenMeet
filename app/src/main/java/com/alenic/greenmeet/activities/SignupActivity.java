package com.alenic.greenmeet.activities;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.MainActivity;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.viewmodel.AuthViewModel;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {
    /**
     * Activity encargada del registro de nuevos usuarios.
     * Valida los campos introducidos y delega la lógica de autenticación al AuthViewModel.
     */
    private static final String TAG = "SignupActivity";
    private TextInputLayout emailLayout, passwordLayout, nameLayout;
    private EditText etEmail, etPassword, etNombre;
    private MaterialButton btnRegister,btnGoogle;
    private AuthViewModel viewModel;
    private TextView txtPregunta;
    private CredentialManager credentialManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupViewModel();
        credentialManager = CredentialManager.create(this);
        setupListeners();
        observeViewModel();
    }

    //Inicializa las vistas del layout.
    private void initViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        nameLayout = findViewById(R.id.nameLayout);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle     = findViewById(R.id.btnGoogle);
        txtPregunta = findViewById(R.id.txtPregunta);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupListeners() {
        //Redirige al login si el usuario ya tiene cuenta
        txtPregunta.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
// Ejecuta validación al pulsar el botón
        btnRegister.setOnClickListener(v -> validarCampos());
        btnGoogle.setOnClickListener(v -> launchGoogleSignIn());

// Limpia errores automáticamente cuando el usuario escribe
        etEmail.addTextChangedListener(emailWatcher);
        etPassword.addTextChangedListener(passwordWatcher);
        etNombre.addTextChangedListener(nameWatcher);

    }

    // Google Sign-In
    private void launchGoogleSignIn() {
        // Paso 1: Construir la solicitud Google según la guía oficial
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)        // primero cuentas ya usadas
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // Paso 2: Lanzar Credential Manager
        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        runOnUiThread(() -> handleSignIn(result.getCredential()));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        // Sin cuentas autorizadas → mostrar todas las cuentas del dispositivo
                        runOnUiThread(() -> launchGoogleSignInAllAccounts());
                    }
                }
        );
    }

    //muestra selector con TODAS las cuentas Google
    private void launchGoogleSignInAllAccounts() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)       // ← diferencia clave
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        runOnUiThread(() -> handleSignIn(result.getCredential()));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        runOnUiThread(() -> Toast.makeText(SignupActivity.this,
                                "Error Google: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }

    //Manejar la credencial recibida
    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential
                && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            CustomCredential customCredential = (CustomCredential) credential;
            GoogleIdTokenCredential googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(customCredential.getData());

            String idToken = googleIdTokenCredential.getIdToken();
            String nombreGoogle = googleIdTokenCredential.getDisplayName() != null
                    ? googleIdTokenCredential.getDisplayName() : "";

            // Sin diálogo — el repositorio decide si es nuevo o no
            viewModel.loginWithGoogle(idToken, nombreGoogle);

        } else {
            Log.w(TAG, "Credential is not of type Google ID!");
        }
    }

    //Valida los campos antes de registrar el usuario.
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

    //Limpia error del email mientras el usuario escribe.
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

    //Limpia error de la contraseña mientras el usuario escribe.
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

    //Limpia error del nombre mientras el usuario escribe.
    private final TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Observa el estado de autenticación desde el ViewModel.
     * Si el registro es exitoso > abre MainActivity.
     * Si falla > muestra mensaje de error.
     */
    private void observeViewModel() {

        viewModel.getSuccess().observe(this, isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                //Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getError().observe(this, errorMessage -> {
            Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show();
        });
    }
}
