package com.alenic.greenmeet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class EditProfileFragment extends Fragment {

    private Spinner spinnerGender;
    private ImageButton btnBack;
    private AppCompatButton btnSave;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Inicializamos Spinner
        spinnerGender = view.findViewById(R.id.spinnerGender);
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

        // Inicializamos botón de retroceso
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Retrocede en el stack de fragments
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        // Botón Guardar
        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            // Aquí podrías guardar los datos en tu base de datos o ViewModel
            // Por ahora mostramos Toast y volvemos atrás
            Toast.makeText(getContext(), "Perfil guardado", Toast.LENGTH_SHORT).show();

            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        return view;
    }
}
