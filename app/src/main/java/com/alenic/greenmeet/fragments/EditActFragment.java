package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.alenic.greenmeet.utils.NavigationUtils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActFragment extends Fragment {

    private EditText etTitulo, etUbicacion, etFecha, etDescripcion;
    private Spinner spinnerCategoria;
    private AppCompatButton btnGuardar;
    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;
    private ImageView imgHeader;
    private ActViewModel actViewModel;
    private View rootView;

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
        tvTitle.setText("Editar actividad");
    }

    private void setupViewModel() {
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);
    }

    private void setupSpinner() {
        String[] categorias = {"ARTE URBANO", "VERDE Y NATURALEZA", "LIMPIEZA URBANA", "SALUD Y DEPORTE", "CULTURA Y SOCIEDAD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void setupObservers() {
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act == null) return;

            etTitulo.setText(act.getTitulo());
            etUbicacion.setText(act.getUbicacion());
            etFecha.setText(act.getFecha());
            etDescripcion.setText(act.getDescripcion());

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategoria.getAdapter();
            spinnerCategoria.setSelection(adapter.getPosition(act.getCategoria()));

            // Cargar imagen con Glide
            Glide.with(this)
                    .load(act.getImagenUrl())
                    .centerCrop()
                    .into(imgHeader);

        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavigationUtils.volver(this));

        btnGuardar.setOnClickListener(v -> updateAct());


        etFecha.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selecciona fecha")
                        .setTheme(R.style.MyMaterialCalendarTheme)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        picker.show(getParentFragmentManager(), "DATE_PICKER");
        picker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etFecha.setText(sdf.format(new Date(selection)));
        });
    }

    private void updateAct() {

        Act actOriginal = actViewModel.getSelectedAct().getValue();

        if (actOriginal == null) {
            Toast.makeText(requireContext(), "Error al obtener actividad", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = etTitulo.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        if (titulo.isEmpty() || ubicacion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        //  Creamos nueva Act con MISMO ID e imagen
        Act actActualizada = new Act(
                titulo,
                categoria,
                fecha,
                ubicacion,
                descripcion,
                actOriginal.getImagenUrl()
        );

        actActualizada.setId(actOriginal.getId()); // 🔥 CRUCIAL

        new ActRepository().updateAct(actActualizada, new ActRepository.ActCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(requireContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show();
                NavigationUtils.volver(EditActFragment.this);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
