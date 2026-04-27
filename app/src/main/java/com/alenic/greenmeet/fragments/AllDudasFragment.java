package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.DudaAdapter;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.alenic.greenmeet.viewmodel.ForoViewModel;

public class AllDudasFragment extends Fragment {

    private ForoViewModel foroViewModel;
    private ActViewModel actViewModel;
    private DudaAdapter dudaAdapter;
    private RecyclerView rvAllDudas;
    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_dudas, container, false);

        rvAllDudas = view.findViewById(R.id.rvAllDudas);
        header = view.findViewById(R.id.headerBack);
        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.dudas));

        btnBack.setOnClickListener(v ->
                Utils.volver(this));

        dudaAdapter = new DudaAdapter();
        rvAllDudas.setAdapter(dudaAdapter);
        rvAllDudas.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Compartimos el ViewModel con la actividad
        actViewModel = new ViewModelProvider(requireActivity()).get(ActViewModel.class);
        foroViewModel = new ViewModelProvider(requireActivity()).get(ForoViewModel.class);

        btnBack.setOnClickListener(v -> Utils.volver(this));

        // Observamos la actividad para cargar sus dudas
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act != null) {
                foroViewModel.loadDudas(act.getUid());
            }
        });

        // Mostramos TODAS las dudas
        foroViewModel.getForoActividad().observe(getViewLifecycleOwner(), dudas -> {
            if (dudas != null) {
                dudaAdapter.setDudas(dudas);
            }
        });

        return view;
    }
}