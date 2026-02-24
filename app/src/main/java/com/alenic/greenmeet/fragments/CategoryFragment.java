package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.ActAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment encargado de mostrar las actividades filtradas
 * por una categoría seleccionada.
 */
public class CategoryFragment extends Fragment {
    // Clave para recibir la categoría como argumento
    private static final String ARG_CATEGORY = "category_name";
    private ActViewModel actViewModel;
    private ActAdapter adapter;
    private String categoriaSeleccionada;
    private TextView tvTitle;
    private View header;
    private ImageView btnBack;

    /**
     * Métod estático para crear una nueva instancia del fragment
     * pasando la categoría como argumento.
     */
    public static CategoryFragment newInstance(String categoryName) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        initViewModel();
        initHeader(view);
        setupRecycler(view);
        observeActs();
        loadData();

        return view;
    }

    private void initViewModel() {
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);
    }

    /**
     * Inicializa el header:
     * - Botón volver
     * - Título con la categoría seleccionada
     */
    private void initHeader(View view) {

        header = view.findViewById(R.id.headerBack);
        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);

        btnBack.setOnClickListener(v -> Utils.volver(this));

        if (getArguments() != null) {
            categoriaSeleccionada = getArguments().getString(ARG_CATEGORY);
            tvTitle.setText(categoriaSeleccionada);
        }
    }


    /**
     * Configura el RecyclerView y su adapter.
     */
    private void setupRecycler(View view) {

        RecyclerView rv = view.findViewById(R.id.rvAcciones);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ActAdapter(
                R.layout.tarjeta_guardada,
                this::openDetailsFragment
        );

        rv.setAdapter(adapter);
    }

    /**
     * Observa la lista de actividades desde el ViewModel.
     * Filtra por categoría antes de mostrar.
     */
    private void observeActs() {

        actViewModel.getActsByCreate().observe(getViewLifecycleOwner(), acts -> {

            if (acts == null) return;

            if (categoriaSeleccionada == null || categoriaSeleccionada.isEmpty()) {
                adapter.submitList(acts);
                return;
            }

            List<Act> filtradas = new ArrayList<>();

            for (Act act : acts) {
                if (act.getCategoria() != null &&
                        act.getCategoria().trim()
                                .equalsIgnoreCase(categoriaSeleccionada.trim())) {

                    filtradas.add(act);
                }
            }

            adapter.submitList(filtradas);
        });
    }

    //Solicita al ViewModel que cargue las actividades.
    private void loadData() {
        actViewModel.loadActsByCreate();
    }

    //Abre el fragmento de detalles al pulsar una actividad.
    private void openDetailsFragment(Act act) {

        actViewModel.selectAct(act);

        DetailsActFragment fragment = new DetailsActFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}