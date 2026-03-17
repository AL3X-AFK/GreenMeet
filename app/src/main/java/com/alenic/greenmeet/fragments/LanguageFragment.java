package com.alenic.greenmeet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.Utils;

import java.util.Locale;

public class LanguageFragment extends Fragment {


    private Spinner spinnerLanguage;
    private ImageButton btnBack;
    private AppCompatButton btnSave;
    private TextView tvTitle;
    private View header;

    public LanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        header = view.findViewById(R.id.headerBack);

        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.idioma));

        // Inicializamos Spinner
        spinnerLanguage = view.findViewById(R.id.spinnerAppLanguage);
        String[] languages = {(getString(R.string.spanish)), (getString(R.string.english))};

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

        btnBack.setOnClickListener(v -> Utils.volver(this));




        // Botón Guardar
        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            int position = spinnerLanguage.getSelectedItemPosition();
            String langCode = (position == 0) ? "es" : "en";
            setLocale(langCode);
        });


        return view;
    }

    /**
     * Guarda el idioma seleccionado en SharedPreferences.
     * Esto permite que el idioma persista al cerrar la app.
     */
    private void saveLanguage(String langCode) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("Settings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_lang", langCode);
        editor.apply();
    }

    /**
     * Aplica el nuevo idioma a la aplicación.
     * Se recrea la Activity para que todos los recursos se actualicen.
     */
    private void setLocale(String langCode) {
        // Guarda primero
        saveLanguage(langCode);

        // Usa AppCompatDelegate (funciona en Android 13+)
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(langCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }

}