package com.alenic.greenmeet.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.NavigationUtils;
import com.alenic.greenmeet.viewmodel.UserViewModel;

public class EditProfileFragment extends Fragment {

    private EditText etName, etEmail, etPhone;
    private Spinner spinnerGender;
    private AppCompatButton btnSave;
    private UserViewModel userViewModel;
    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;

    public EditProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        initViews(view);
        setupViewModel();
        setupSpinner();
        setupObservers();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        btnSave = view.findViewById(R.id.btnSave);

        header = view.findViewById(R.id.headerBack);
        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);
        tvTitle.setText("Editar perfil");
    }

    private void setupViewModel() {
        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);
    }

    private void setupSpinner() {
        String[] genders = {"No especificar","Masculino", "Femenino", "Otro"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genders
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void setupObservers() {

        userViewModel.getUsuario().observe(getViewLifecycleOwner(), u -> {
            if (u == null) return;

            etName.setText(u.getNombre());
            etPhone.setText(u.getTelefono());

            ArrayAdapter adapter = (ArrayAdapter) spinnerGender.getAdapter();
            spinnerGender.setSelection(adapter.getPosition(u.getGenero()));
        });

        userViewModel.getEmail().observe(getViewLifecycleOwner(),
                etEmail::setText);

        userViewModel.getState().observe(getViewLifecycleOwner(), state -> {

            if (state == null) return;

            if (state.equals("UPDATE_SUCCESS")) {

                Toast.makeText(getContext(),
                        "Perfil actualizado",
                        Toast.LENGTH_SHORT).show();
                        userViewModel.clearState();

                requireActivity().getSupportFragmentManager().popBackStack();

            } else {

                Toast.makeText(getContext(),
                        state,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v ->
                NavigationUtils.volver(this));

        btnSave.setOnClickListener(v -> {

            String nombre = etName.getText().toString().trim();
            String emailNuevo = etEmail.getText().toString().trim();
            String telefono = etPhone.getText().toString().trim();
            String genero = spinnerGender.getSelectedItem().toString();

            showReauthDialog(nombre, telefono, genero, emailNuevo);
        });
    }

    private void showReauthDialog(String nombre,
                                  String telefono,
                                  String genero,
                                  String emailNuevo) {

        View dialog = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_password, null);

        EditText etPassword = dialog.findViewById(R.id.etPassword);

        new AlertDialog.Builder(requireContext())
                .setTitle("Verificación")
                .setView(dialog)
                .setPositiveButton("Confirmar", (d, w) -> {

                    String passwordActual =
                            etPassword.getText().toString().trim();

                    userViewModel.updateProfile(
                            nombre,
                            telefono,
                            genero,
                            passwordActual,
                            emailNuevo
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}