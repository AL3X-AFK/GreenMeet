package com.alenic.greenmeet.fragments;

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

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.GuardadosAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class InscriptionsFragment extends Fragment {

    private RecyclerView rvProximos;
    private RecyclerView rvRealizadas;

    private GuardadosAdapter adapterProximos;
    private GuardadosAdapter adapterRealizadas;

    private ActViewModel actViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_inscriptions, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        rvProximos = view.findViewById(R.id.rvProximos);
        rvRealizadas = view.findViewById(R.id.rvRealizadas);

        rvProximos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRealizadas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter vacío al inicio usando GuardadosAdapter
        adapterProximos = new GuardadosAdapter(new ArrayList<>(), this::openDetailsActivityFragment);
        adapterRealizadas = new GuardadosAdapter(new ArrayList<>(), this::openDetailsActivityFragment);

        rvProximos.setAdapter(adapterProximos);
        rvRealizadas.setAdapter(adapterRealizadas);

        // Mostrar solo próximos por defecto
        rvProximos.setVisibility(View.VISIBLE);
        rvRealizadas.setVisibility(View.GONE);

        // Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rvProximos.setVisibility(View.VISIBLE);
                    rvRealizadas.setVisibility(View.GONE);
                } else {
                    rvProximos.setVisibility(View.GONE);
                    rvRealizadas.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // ViewModel
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        // Observamos actividades próximas
        actViewModel.getActsProximos().observe(getViewLifecycleOwner(), acts -> {
            adapterProximos.setActs(acts);
        });

        // Observamos actividades realizadas
        actViewModel.getActsRealizadas().observe(getViewLifecycleOwner(), acts -> {
            adapterRealizadas.setActs(acts);
        });

        // Cargar datos desde Firebase
        actViewModel.loadActsProximos();
        actViewModel.loadActsRealizadas();

        return view;
    }

    // Abrir fragmento de detalles
    private void openDetailsActivityFragment(Act act) {
        DetailsActFragment fragment = new DetailsActFragment();

        Bundle bundle = new Bundle();
        bundle.putString("titulo", act.getTitulo());
        bundle.putString("descripcion", act.getDescripcion());
        bundle.putString("fecha", act.getFecha());
        bundle.putString("ubicacion", act.getUbicacion());
        bundle.putString("imagenUrl", act.getImagenUrl());
        fragment.setArguments(bundle);

        FragmentTransaction transaction =
                requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
