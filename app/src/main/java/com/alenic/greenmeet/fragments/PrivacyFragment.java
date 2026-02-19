package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.Utils;


public class PrivacyFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;

    public PrivacyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        header = view.findViewById(R.id.headerBack);

        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.polPrivacidad));
        btnBack.setOnClickListener(v -> Utils.volver(this));

        return  view;
    }

}