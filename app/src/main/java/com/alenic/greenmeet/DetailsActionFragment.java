package com.alenic.greenmeet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class DetailsActionFragment extends Fragment {


    private ImageButton btnback;

    public DetailsActionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_action, container, false);

        btnback = view.findViewById(R.id.btnBack);
        btnback.setOnClickListener(v -> {
            // Retrocede en el stack de fragments
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}