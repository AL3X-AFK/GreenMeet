package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.GuardadosAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.viewmodel.ActViewModel;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    private RecyclerView rvActividades;
    private GuardadosAdapter adapterExplorar;
    private ActViewModel actViewModel;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // RecyclerView
        rvActividades = view.findViewById(R.id.acProx);
        rvActividades.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter vacío al inicio
        adapterExplorar = new GuardadosAdapter(new ArrayList<>(), this::openDetailsActFragment);
        rvActividades.setAdapter(adapterExplorar);

        // ViewModel
        actViewModel = new ViewModelProvider(requireActivity()).get(ActViewModel.class);

        // Observamos los actos desde Firebase
        actViewModel.getActs().observe(getViewLifecycleOwner(), acts -> {
            if (acts != null) {
                adapterExplorar.setActs(acts);
            }
        });

        // Cargar datos desde Firebase
        actViewModel.loadActs();

        return view;
    }

    // Método para abrir el fragmento de detalles
    private void openDetailsActFragment(Act act) {
        DetailsActFragment fragment = new DetailsActFragment();

        Bundle bundle = new Bundle();
        bundle.putString("titulo", act.getTitulo());
        bundle.putString("descripcion", act.getDescripcion());
        bundle.putString("fecha", act.getFecha());
        bundle.putString("ubicacion", act.getUbicacion());
        bundle.putString("imagenUrl", act.getImagenUrl());
        fragment.setArguments(bundle);

        // Abrir fragmento
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
