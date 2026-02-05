package com.alenic.greenmeet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.viewmodel.ActViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ActViewModel actViewModel;
    private ActAdapter adapter;

    public HomeFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rvAcciones = view.findViewById(R.id.rvAcciones);
        RecyclerView rvAccionesSugeridas = view.findViewById(R.id.rvAccionesSugeridas);

        // Layout horizontal
        rvAcciones.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        rvAccionesSugeridas.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Adapter vacío al inicio
        adapter = new ActAdapter(new ArrayList<>(), act -> {
            // Guardamos la actividad seleccionada en el ViewModel
            actViewModel.selectAct(act);

            // Abrimos fragmento de detalles
            openDetailsActivityFragment(act);
        });

        rvAcciones.setAdapter(adapter);
        rvAccionesSugeridas.setAdapter(adapter);

        // ViewModel
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        // Observamos lista de actividades
        actViewModel.getActs().observe(getViewLifecycleOwner(), acts -> {
            adapter.setActs(acts);
        });

        // Cargar datos desde Firebase
        actViewModel.loadActs();

        return view;
    }

    private void openDetailsActivityFragment(Act act) {
        DetailsActionFragment fragment = new DetailsActionFragment();

        // Pasamos datos opcionales por bundle si quieres, pero puedes usar ViewModel
        Bundle bundle = new Bundle();
        bundle.putString("titulo", act.getTitulo());
        bundle.putString("descripcion", act.getDescripcion());
        bundle.putString("fecha", act.getFecha());
        bundle.putString("ubicacion", act.getUbicacion());
        bundle.putString("imagenUrl", act.getImagenUrl());
        fragment.setArguments(bundle);

        // Abrir fragmento
        FragmentTransaction transaction =
                requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
