package com.alenic.greenmeet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class ExploreFragment extends Fragment {

    private RecyclerView rvActividades;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        rvActividades = view.findViewById(R.id.acProx);

        // LayoutManagers
        rvActividades.setLayoutManager(new LinearLayoutManager(getContext()));

        GuardadosAdapter adapterExplorar = new GuardadosAdapter(getListaExplorar());


        rvActividades.setAdapter(adapterExplorar);

        // Mostrar por defecto
        rvActividades.setVisibility(View.VISIBLE);



        return view;

    }
    // Datos de ejemplo
    private List<String> getListaExplorar() {
        List<String> lista = new ArrayList<>();
        lista.add("Jardinería");
        lista.add("Limpieza de Parque");
        lista.add("Limpieza de Playa");
        lista.add("Pintar mural");
        lista.add("Clase de yoga gratis");

        return lista;
    }


}