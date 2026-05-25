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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

//    Activity encargada del inicio de sesión del usuario.
//    Valida los campos de entrada y se comunica con el AuthViewModel
    private static final String TAG = "LoginActivity";
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin,btnGoogle;
    private AuthViewModel viewModel;
    private TextView txtRedirect;
    private CredentialManager credentialManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupViewModel();
        credentialManager = CredentialManager.create(this);
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle     = findViewById(R.id.btnGoogle);
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
        btnGoogle.setOnClickListener(v -> launchGoogleSignIn());
        etEmail.addTextChangedListener(emailWatcher);
        etPassword.addTextChangedListener(passwordWatcher);
    }

    // Google Sign-In
    private void launchGoogleSignIn() {
        //Construir la solicitud Google según la guía oficial
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)        // primero cuentas ya usadas
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        //Lanzar Credential Manager
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
                        // Sin cuentas autorizadas, mostrar todas las cuentas del dispositivo
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
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this,
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

        viewModel.getSuccess().observe(this, isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                //Toast.makeText(this, "Credenciales correctas", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class));
                // Cerrar LoginActivity para que no se pueda volver atrás
                finish();
            }
        });

        viewModel.getError().observe(this, errorMessage -> {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        });
    }
}