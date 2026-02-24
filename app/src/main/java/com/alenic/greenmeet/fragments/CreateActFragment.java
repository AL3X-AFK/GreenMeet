package com.alenic.greenmeet.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.CreateActViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;

/**
 * Fragment encargado de crear una nueva actividad.
 * Permite al usuario:
 * - Introducir datos
 * - Seleccionar una imagen
 * - Guardar la actividad en la base de datos
 */

public class CreateActFragment extends Fragment {

    private CreateActViewModel viewModel;
    private ImageView imgUpload, btnBack;
    private Uri imageUri;
    private TextView tvTitle;
    private View header;
    private TextInputEditText etTitulo, etUbicacion, etDescripcion, etDate;
    private AutoCompleteTextView actvCategoria;

    private Button btnNext, btnCancel;

    private LinearLayout layoutUpload;
    private long selectedDateMillis = 0;

    /**
     * Launcher moderno para abrir el selector de imágenes
     * y recibir el resultado.
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgUpload.setImageURI(imageUri);
                    imgUpload.setVisibility(View.VISIBLE);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_act, container, false);

        initViews(view);
        initViewModel();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etTitulo = view.findViewById(R.id.tietTitle);
        etUbicacion = view.findViewById(R.id.tietLocation);
        etDescripcion = view.findViewById(R.id.tietDescription);
        etDate = view.findViewById(R.id.etDate);
        actvCategoria = view.findViewById(R.id.actvCategoria);

        header = view.findViewById(R.id.headerBack);
        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);

        layoutUpload = view.findViewById(R.id.layoutUpload);
        imgUpload = view.findViewById(R.id.imgUpload);

        btnNext = view.findViewById(R.id.btnNext);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Configuración de categoría
        String[] categorias = {(getString(R.string.arteUrbano)), (getString(R.string.verdeYnaturaleza)), (getString(R.string.limpUrbana)), (getString(R.string.salYdeporte)), (getString(R.string.cultYsociedad))};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias);
        actvCategoria.setAdapter(categoriaAdapter);
        // Evita escritura manual (solo selección)
        actvCategoria.setKeyListener(null);
        actvCategoria.setOnClickListener(v -> actvCategoria.showDropDown());
    }

    /**
     * Inicializa el ViewModel y observa resultados.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(CreateActViewModel.class);

        // Observa éxito en subida
        viewModel.getUploadSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), (getString(R.string.actCreadaConExito)), Toast.LENGTH_SHORT).show();
                Utils.volver(this);
            }
        });

        // Observa errores
        viewModel.getUploadError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        layoutUpload.setOnClickListener(v -> openFileChooser());
        etDate.setOnClickListener(v -> showDatePicker());
        btnNext.setOnClickListener(v -> guardarAccion());
        btnCancel.setOnClickListener(v -> Utils.volver(this));
        tvTitle.setText(getString(R.string.crearActividad));
        btnBack.setOnClickListener(v -> Utils.volver(this));
    }

    /**
     * Abre el selector de archivos para elegir imagen.
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Muestra el selector de fecha con MaterialDatePicker.
     */
    private void showDatePicker() {
        MaterialDatePicker<Long> picker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.selecFecha))
                        .setTheme(R.style.MyMaterialCalendarTheme)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        picker.show(getParentFragmentManager(), "DATE_PICKER");
        picker.addOnPositiveButtonClickListener(selection -> {
            selectedDateMillis = selection;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etDate.setText(sdf.format(new Date(selection)));
        });
    }

    /**
     * Valida campos y envía datos al ViewModel.
     */
    private void guardarAccion() {
        String titulo = etTitulo.getText().toString().trim();
        long fecha = selectedDateMillis;
        String ubicacion = etUbicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = actvCategoria.getText().toString().trim();

        if (titulo.isEmpty() || fecha == 0 || ubicacion.isEmpty() ||
                descripcion.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.rellenaTodosLosCampos), Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.uploadAct(requireContext(), imageUri, titulo, fecha, ubicacion, descripcion, categoria);
    }
}
