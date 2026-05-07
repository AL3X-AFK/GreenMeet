package com.alenic.greenmeet.fragments;

import android.app.AlertDialog;
import android.net.Uri;
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
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import android.app.Activity;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class EditProfileFragment extends Fragment {

    /**
     * Fragment encargado de editar el perfil del usuario.
     */
    private EditText etName, etEmail, etPhone;
    private Spinner spinnerGender;
    private AppCompatButton btnSave;
    private UserViewModel userViewModel;
    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;
    private ShapeableImageView imgProfile;
    private Uri selectedImageUri; // Para guardar la imagen elegida de la galería

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgProfile.setImageURI(selectedImageUri);
                }
            });

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
        tvTitle.setText(getString(R.string.editarPefil));

        imgProfile = view.findViewById(R.id.imgProfile);
    }

    private void setupViewModel() {
        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);
    }

    //Configura el Spinner con las opciones de género.
    private void setupSpinner() {
        String[] genders = {getString(R.string.noEspecificar),getString(R.string.male), getString(R.string.female), getString(R.string.otro)};

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

            // CARGAR IMAGEN CON GLIDE
            if (u.getImagenProfileURL() != null && !u.getImagenProfileURL().isEmpty()) {
                com.bumptech.glide.Glide.with(this)
                        .load(u.getImagenProfileURL())
                        .placeholder(R.drawable.profile_icon) // Imagen mientras carga
                        .error(R.drawable.profile_icon)       // Imagen si falla
                        .into(imgProfile);
            }

            ArrayAdapter adapter = (ArrayAdapter) spinnerGender.getAdapter();
            spinnerGender.setSelection(adapter.getPosition(u.getGenero()));
        });

        etEmail.setText(userViewModel.getEmail());

        userViewModel.getState().observe(getViewLifecycleOwner(), state -> {

            if (state == null) return;

            if (state.equals("UPDATE_SUCCESS")) {

                Toast.makeText(getContext(), getString(R.string.perfilActualizado), Toast.LENGTH_SHORT).show();
                userViewModel.clearState();

                Utils.volver(this);

            } else {

                Toast.makeText(getContext(), R.string.error_al_actualizar_el_perfil, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Configura eventos de botones.
    private void setupListeners() {

        btnBack.setOnClickListener(v ->
                Utils.volver(this));

        btnSave.setOnClickListener(v -> {

            String nombre = etName.getText().toString().trim();
            String telefono = etPhone.getText().toString().trim();
            String genero = spinnerGender.getSelectedItem().toString();

            userViewModel.updateProfile(nombre, telefono, genero, selectedImageUri, requireContext());
        });

        imgProfile.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

}