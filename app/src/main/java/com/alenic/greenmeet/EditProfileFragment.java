package com.alenic.greenmeet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.viewmodel.UserViewModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private EditText etName, etEmail, etPhone;
    private Spinner spinnerGender;
    private AppCompatButton btnSave;
    private ImageButton btnBack;

    private UserViewModel userViewModel;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        btnSave = view.findViewById(R.id.btnSave);
        btnBack = view.findViewById(R.id.btnBack);


        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);

        // Inicializamos Spinner
        String[] genders = {"Masculino", "Femenino", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                genders
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Evento de selección del Spinner
        spinnerGender.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Nada
            }
        });

        userViewModel.getUsuario().observe(getViewLifecycleOwner(), u -> {
            if (u == null) return;
            etName.setText(u.getNombre());
            etPhone.setText(u.getTelefono());
            spinnerGender.setSelection(adapter.getPosition(u.getGenero()));
        });


        userViewModel.getEmail().observe(getViewLifecycleOwner(), etEmail::setText);

        // Inicializamos botón de retroceso
        btnBack.setOnClickListener(v -> {
            // Retrocede en el stack de fragments
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        // Botón Guardar
        btnSave.setOnClickListener(v -> {
            String nombre = etName.getText().toString().trim();
            String emailNuevo = etEmail.getText().toString().trim();
            String telefono = etPhone.getText().toString().trim();
            String genero = spinnerGender.getSelectedItem().toString();

            showReauthDialog(emailNuevo, nombre, telefono, genero);
        });


        return view;
    }
    private void showReauthDialog(String email,
                                  String nombre,
                                  String telefono,
                                  String genero) {

        View dialog = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_password, null);

        EditText etPassword = dialog.findViewById(R.id.etPassword);

        new AlertDialog.Builder(requireContext())
                .setTitle("Verificación")
                .setView(dialog)
                .setPositiveButton("Confirmar", (d, w) -> {

                    userViewModel.updateProfile(
                            nombre,
                            telefono,
                            genero,
                            () -> {
                                Toast.makeText(getContext(),
                                        "Perfil actualizado",
                                        Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed();
                            },
                            () -> Toast.makeText(getContext(),
                                    "Contraseña incorrecta",
                                    Toast.LENGTH_SHORT).show(),
                            etPassword.getText().toString(),
                            email
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
