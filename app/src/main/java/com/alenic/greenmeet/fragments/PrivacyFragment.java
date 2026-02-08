package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.alenic.greenmeet.R;


public class PrivacyFragment extends Fragment {

    private ImageButton btnBack;

    public PrivacyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        // Inicializamos botón de retroceso
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Retrocede en el stack de fragments
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return  view;
    }

}