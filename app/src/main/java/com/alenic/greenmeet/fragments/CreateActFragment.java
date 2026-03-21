package com.alenic.greenmeet.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.NominatimService;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.CreateActViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private TextInputEditText etTitulo, etDescripcion, etDate;
    private AutoCompleteTextView etUbicacion;
    private AutoCompleteTextView actvCategoria;

    private Button btnNext, btnCancel;

    private LinearLayout layoutUpload;
    private long selectedDateMillis = 0;
    private double selectedLat = 0;
    private double selectedLon = 0;
    private ArrayAdapter<NominatimService.NominatimResult> locationAdapter;

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
        etUbicacion = view.findViewById(R.id.tietLocation);
        setupLocationAutocomplete();

        // Configuración de categoría
        String[] categorias = {(getString(R.string.arteUrbano)), (getString(R.string.verdeYnaturaleza)), (getString(R.string.limpUrbana)), (getString(R.string.salYdeporte)), (getString(R.string.cultYsociedad))};

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,categorias);
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

    private void setupLocationAutocomplete() {
        locationAdapter = new ArrayAdapter<NominatimService.NominatimResult>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()) {

            // Desactiva el filtro interno — nosotros controlamos los resultados
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = null;
                        results.count = 0;
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        // No hacer nada — el adapter ya tiene los datos correctos
                    }
                };
            }
        };

        etUbicacion.setAdapter(locationAdapter);

        etUbicacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            private final Handler searchHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchHandler.removeCallbacksAndMessages(null);
                if (query.length() >= 3) {
                    searchHandler.postDelayed(() -> buscarUbicaciones(query), 500);
                } else {
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etUbicacion.setOnItemClickListener((parent, view, position, id) -> {
            NominatimService.NominatimResult result =
                    (NominatimService.NominatimResult) parent.getItemAtPosition(position);
            selectedLat = result.lat;
            selectedLon = result.lon;
            etUbicacion.setText(result.displayName);
            etUbicacion.dismissDropDown();
        });
    }

    private void buscarUbicaciones(String query) {
        NominatimService.search(query, new NominatimService.NominatimCallback() {
            @Override
            public void onResults(List<NominatimService.NominatimResult> results) {
                if (getActivity() == null || !isAdded()) return;
                getActivity().runOnUiThread(() -> {
                    locationAdapter.clear();
                    locationAdapter.addAll(results);
                    locationAdapter.notifyDataSetChanged();
                    if (!results.isEmpty()) etUbicacion.showDropDown();
                });
            }

            @Override
            public void onError(String error) {
                Log.e("Nominatim", "Error: " + error);
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

        // Validar que se haya seleccionado del autocompletado
        if (selectedLat == 0 && selectedLon == 0) {
            Toast.makeText(requireContext(),
                    "Selecciona una ubicación de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoriaKey = Utils.categoriaToKey(requireContext(), categoria);

        viewModel.uploadAct(requireContext(), imageUri, titulo, fecha, ubicacion, descripcion, categoriaKey,selectedLat, selectedLon);
    }
}
