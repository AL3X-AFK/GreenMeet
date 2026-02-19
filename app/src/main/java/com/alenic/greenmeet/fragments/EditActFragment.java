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
import com.alenic.greenmeet.utils.Utils;
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

    // Guardamos la fecha real en millis
    private long selectedDateMillis;

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

    private void setupObservers() {

        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {

            if (act == null) return;

            etTitulo.setText(act.getTitulo());
            etUbicacion.setText(act.getUbicacion());
            etDescripcion.setText(act.getDescripcion());

            // Fecha long → formateada a texto
            selectedDateMillis = act.getFecha();

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            etFecha.setText(sdf.format(new Date(selectedDateMillis)));

            ArrayAdapter<String> adapter =
                    (ArrayAdapter<String>) spinnerCategoria.getAdapter();

            spinnerCategoria.setSelection(
                    adapter.getPosition(act.getCategoria())
            );

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

        if (actOriginal == null) {
            Toast.makeText(requireContext(),
                    getString(R.string.errorObtenerActividad),
                    Toast.LENGTH_SHORT).show();
            return;
        }

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

        // Creamos nueva Act manteniendo datos importantes
        Act actActualizada = new Act(
                titulo,
                categoria,
                selectedDateMillis,
                ubicacion,
                descripcion,
                actOriginal.getImagenUrl(),
                actOriginal.getUserUid()
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
