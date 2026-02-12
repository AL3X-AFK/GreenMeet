package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.NavigationUtils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActFragment extends Fragment {

    private ActViewModel actViewModel;

    private ImageView imgHeader;
    private TextInputEditText etTitulo, etUbicacion, etDescripcion, etDate;
    private AutoCompleteTextView actvCategoria;
    private Button btnGuardar;

    public EditActFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_act, container, false);

        // Inicializar Views
        imgHeader = view.findViewById(R.id.imgHeader);
        etTitulo = view.findViewById(R.id.etTitulo);
        etUbicacion = view.findViewById(R.id.etUbicacion);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etDate = view.findViewById(R.id.etFecha);
        actvCategoria = view.findViewById(R.id.etCategoria);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Configurar categoría como menú desplegable
        String[] categorias = {"ARTE URBANO", "VERDE Y NATURALEZA", "LIMPIEZA URBANA", "SALUD Y DEPORTE", "CULTURA Y SOCIEDAD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categorias);
        actvCategoria.setAdapter(adapter);
        actvCategoria.setKeyListener(null);
        actvCategoria.setOnClickListener(v -> actvCategoria.showDropDown());

        // ViewModel compartido
        actViewModel = new ViewModelProvider(requireActivity()).get(ActViewModel.class);

        // Cargar datos de la actividad seleccionada
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act == null) return;

            etTitulo.setText(act.getTitulo());
            etUbicacion.setText(act.getUbicacion());
            etDescripcion.setText(act.getDescripcion());
            etDate.setText(act.getFecha());
            actvCategoria.setText(act.getCategoria(), false);

            Glide.with(this)
                    .load(act.getImagenUrl())
                    .centerCrop()
                    .into(imgHeader);
        });

        // Botón guardar solo muestra un Toast por ahora
        btnGuardar.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Funcionalidad de guardar no implementada aún", Toast.LENGTH_SHORT).show()
        );

        // Botón atrás
        view.findViewById(R.id.btnBack).setOnClickListener(v -> NavigationUtils.volver(this));

        // Fecha picker
        etDate.setOnClickListener(v -> showDatePicker());

        return view;
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
            etDate.setText(sdf.format(new Date(selection)));
        });
    }
}
