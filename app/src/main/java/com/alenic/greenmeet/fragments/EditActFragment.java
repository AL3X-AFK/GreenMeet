package com.alenic.greenmeet.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.alenic.greenmeet.utils.NominatimService;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActFragment extends Fragment {

    private EditText etTitulo, etFecha, etDescripcion;
    private AutoCompleteTextView etUbicacion;
    private Spinner spinnerCategoria;
    private AppCompatButton btnGuardar;
    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;
    private ImageView imgHeader;
    private ActViewModel actViewModel;
    private View rootView;

    // Guardamos la fecha real en millis
    private long selectedDateMillis;
    private double selectedLat = 0;
    private double selectedLon = 0;
    private ArrayAdapter<NominatimService.NominatimResult> locationAdapter;

    public EditActFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_edit_act, container, false);

        initViews(rootView);
        setupViewModel();
        setupSpinner();
        setupObservers();
        setupListeners();
        return rootView;
    }

    private void initViews(View view) {
        etTitulo = view.findViewById(R.id.etTitulo);
        etUbicacion = view.findViewById(R.id.etUbicacion);
        etFecha = view.findViewById(R.id.etFecha);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        imgHeader = view.findViewById(R.id.imgHeader);

        header = view.findViewById(R.id.headerBack);
        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.editarAct));

        setupLocationAutocomplete();
    }

    private void setupViewModel() {
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);
    }

    private void setupSpinner() {
        String[] categorias = {
                getString(R.string.arteUrbano),
                getString(R.string.verdeYnaturaleza),
                getString(R.string.limpUrbana),
                getString(R.string.salYdeporte),
                getString(R.string.cultYsociedad)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }


    private void setupLocationAutocomplete() {
        locationAdapter = new ArrayAdapter<NominatimService.NominatimResult>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()) {

            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults r = new FilterResults();
                        r.values = null; r.count = 0;
                        return r;
                    }
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {}
                };
            }
        };

        etUbicacion.setAdapter(locationAdapter);

        etUbicacion.addTextChangedListener(new TextWatcher() {
            private final Handler searchHandler = new Handler(Looper.getMainLooper());

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

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

    private void setupObservers() {

        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {

            if (act == null) return;

            etTitulo.setText(act.getTitulo());
            etUbicacion.setText(act.getUbicacion());
            etDescripcion.setText(act.getDescripcion());

            selectedLat = act.getLatitud();
            selectedLon = act.getLongitud();
            // Fecha long → formateada a texto
            selectedDateMillis = act.getFecha();

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            etFecha.setText(sdf.format(new Date(selectedDateMillis)));

            ArrayAdapter<String> adapter =
                    (ArrayAdapter<String>) spinnerCategoria.getAdapter();

            String textoCategoria = Utils.keyToCategoria(requireContext(), act.getCategoria());
            spinnerCategoria.setSelection(adapter.getPosition(textoCategoria));

            // Cargar imagen
            Glide.with(this)
                    .load(act.getImagenUrl())
                    .centerCrop()
                    .into(imgHeader);
        });
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v ->
                Utils.volver(this)
        );

        btnGuardar.setOnClickListener(v ->
                updateAct()
        );

        etFecha.setOnClickListener(v ->
                showDatePicker()
        );
    }

    private void showDatePicker() {

        MaterialDatePicker<Long> picker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.selecFecha))
                        .setTheme(R.style.MyMaterialCalendarTheme)
                        .setSelection(selectedDateMillis != 0
                                ? selectedDateMillis
                                : MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        picker.show(getParentFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {

            selectedDateMillis = selection;

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            etFecha.setText(sdf.format(new Date(selection)));
        });
    }

    private void updateAct() {

        Act actOriginal = actViewModel.getSelectedAct().getValue();

        if (actOriginal == null) return;

        String titulo = etTitulo.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        if (titulo.isEmpty() || ubicacion.isEmpty() || selectedDateMillis == 0) {
            Toast.makeText(requireContext(),
                    getString(R.string.complCamposObl),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Validar que se haya seleccionado del autocompletado
        if (selectedLat == 0 && selectedLon == 0) {
            Toast.makeText(requireContext(),
                    "Selecciona una ubicación de la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        String categoriaKey = Utils.categoriaToKey(requireContext(), categoria);

        // Creamos nueva Act manteniendo datos importantes
        Act actActualizada = new Act(
                titulo,
                categoriaKey,
                selectedDateMillis,
                ubicacion,
                descripcion,
                actOriginal.getImagenUrl(),
                actOriginal.getUserUid(),
                selectedLat,selectedLon
        );

        // Mantenemos ID
        actActualizada.setUid(actOriginal.getUid());

        // Mantenemos fechaCreacion original
        actActualizada.setFechaCreacion(
                actOriginal.getFechaCreacion()
        );

        new ActRepository().updateAct(
                actActualizada,
                new ActRepository.ActCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {

                        Toast.makeText(requireContext(),
                                getString(R.string.actActualizada),
                                Toast.LENGTH_SHORT).show();

                        Utils.volver(EditActFragment.this);
                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(requireContext(),
                                error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
