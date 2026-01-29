package com.alenic.greenmeet;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class LanguageFragment extends Fragment {


    private Spinner spinnerLanguage;
    private ImageButton btnBack;
    private AppCompatButton btnSave;

    public LanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        // Inicializamos Spinner
        spinnerLanguage = view.findViewById(R.id.spinnerAppLanguage);
        String[] languages = {"Español", "Ingles", "Alemán"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Evento de selección del Spinner
        spinnerLanguage.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
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
            Toast.makeText(getContext(), "Idioma guardado", Toast.LENGTH_SHORT).show();

            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        return view;
    }
}