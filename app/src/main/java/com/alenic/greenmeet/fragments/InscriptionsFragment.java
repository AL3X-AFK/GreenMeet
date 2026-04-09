package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.ActAdapter;
import com.alenic.greenmeet.adapters.GuardadosAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class InscriptionsFragment extends Fragment {

    private RecyclerView rvProximos;
    private RecyclerView rvRealizadas;
    private ActAdapter adapterProximos;
    private ActAdapter adapterRealizadas;
    private ActViewModel actViewModel;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;
    private TabLayout tabLayout;

    public InscriptionsFragment(){}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
             ViewGroup container,
             Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_inscriptions, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        rvProximos = view.findViewById(R.id.rvProximos);
        rvRealizadas = view.findViewById(R.id.rvRealizadas);
        layoutEmpty = view.findViewById(R.id.layoutEmptyInscriptions);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        // ViewModel
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        rvProximos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRealizadas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapters para próximos y realizadas
        adapterProximos = new ActAdapter(R.layout.tarjeta_guardada, this::openDetailsFragment);
        adapterRealizadas = new ActAdapter(R.layout.tarjeta_guardada, this::openDetailsFragment);

        rvProximos.setAdapter(adapterProximos);
        rvRealizadas.setAdapter(adapterRealizadas);

        // Observamos actividades próximas
        actViewModel.getActsProximos().observe(getViewLifecycleOwner(), acts -> {
            adapterProximos.submitList(acts);
            if (tabLayout.getSelectedTabPosition() == 0) {
                updateEmptyState(acts);
            }
        });

        // Observamos actividades realizadas
        actViewModel.getActsRealizadas().observe(getViewLifecycleOwner(), acts -> {
            adapterRealizadas.submitList(acts);
            if (tabLayout.getSelectedTabPosition() == 1) {
                updateEmptyState(acts);
            }
        });

        // Cargar datos desde Firebase
        actViewModel.loadActsProximos();
        actViewModel.loadActsRealizadas();

        // Gestión de Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rvProximos.setVisibility(View.VISIBLE);
                    rvRealizadas.setVisibility(View.GONE);
                    updateEmptyState(actViewModel.getActsProximos().getValue());
                } else {
                    rvProximos.setVisibility(View.GONE);
                    rvRealizadas.setVisibility(View.VISIBLE);
                    updateEmptyState(actViewModel.getActsRealizadas().getValue());
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    // Abrir fragmento de detalles
    private void openDetailsFragment(Act act) {
        actViewModel.selectAct(act);

        DetailsActFragment fragment = new DetailsActFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateEmptyState(List<Act> lista) {
        if (lista == null || lista.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            // Opcional: Cambiar el texto según la pestaña
            if (tabLayout.getSelectedTabPosition() == 0) {
                tvEmptyMessage.setText(R.string.no_proximas);
            } else {
                tvEmptyMessage.setText(R.string.no_realizadas);
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}
