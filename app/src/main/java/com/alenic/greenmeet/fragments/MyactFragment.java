package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.alenic.greenmeet.adapters.ActividadesGuardadasAdapter;
import com.alenic.greenmeet.R;

import java.util.ArrayList;
import java.util.List;

public class MyactFragment extends Fragment {

    private ImageButton btnBack;
    private RecyclerView recyclerView;

    public MyactFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myact, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        recyclerView = view.findViewById(R.id.actGuardadasList);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Recycler config
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ActividadesGuardadasAdapter(getFakeData()));



        return view;
    }

    private List<String> getFakeData() {
        List<String> list = new ArrayList<>();
        list.add("Jardinería");
        list.add("Limpieza de Parque");
        list.add("Pintar Mural");
        list.add("Reforestación");
        return list;
    }
}