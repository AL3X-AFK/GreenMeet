package com.alenic.greenmeet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SaveFragment extends Fragment {

    private RecyclerView rvProximos;
    private RecyclerView rvRealizadas;

    public SaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_save, container, false);

        // Inicializar vistas
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        rvProximos = view.findViewById(R.id.rvProximos);
        rvRealizadas = view.findViewById(R.id.rvRealizadas);

        // LayoutManagers
        rvProximos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRealizadas.setLayoutManager(new LinearLayoutManager(getContext()));


        // Mostrar por defecto "Próximos"
        rvProximos.setVisibility(View.VISIBLE);
        rvRealizadas.setVisibility(View.GONE);

        // Listener de tabs
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

        return view;
    }

    // 🔽 Datos de ejemplo (luego los conectas a BD o API)
    private List<String> getListaProximos() {
        List<String> lista = new ArrayList<>();
        lista.add("Jardinería");
        lista.add("Limpieza de Parque");
        return lista;
    }

    private List<String> getListaRealizadas() {
        List<String> lista = new ArrayList<>();
        lista.add("Clase de Patinaje");
        lista.add("Reciclaje Comunitario");
        return lista;
    }
}
