package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.NavigationUtils;

public class DetailsActFragment extends Fragment {


    private ImageButton btnback;

    public DetailsActFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_act, container, false);

        btnback = view.findViewById(R.id.btnBack);
        btnback.setOnClickListener(v -> NavigationUtils.volver(this));

        return view;
    }
}