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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.adapters.ActAdapter;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.alenic.greenmeet.viewmodel.UserViewModel;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ActViewModel actViewModel;
    private UserViewModel userViewModel;
    private ActAdapter adapterAcciones;
    private ActAdapter adapterSugeridas;
    private TextView tvNombre;
    private TextView tvEmail;
    private LinearLayout catArte,catNaturaleza,catLimpieza,catSalud,catCultura;

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

        initViewModels();
        initViews(view);
        setupRecyclerViews(view);
        setupListeners();
        observeUser();
        observeActs();
        loadData();

        return view;
    }

    private void initViewModels() {
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);
    }
    private void loadData() {
        actViewModel.loadActsByCreate();
        actViewModel.loadActsByFecha();
    }

    private void initViews(View view) {
        tvNombre = view.findViewById(R.id.tvNombre);
        tvEmail = view.findViewById(R.id.tvEmail);
        catArte = view.findViewById(R.id.catArte);
        catCultura = view.findViewById(R.id.catCultura);
        catLimpieza = view.findViewById(R.id.catLimpieza);
        catNaturaleza = view.findViewById(R.id.catNaturaleza);
        catSalud = view.findViewById(R.id.catSalud);
    }

    private void setupListeners(){
        catArte.setOnClickListener(v -> openCategory("Arte urbano"));

        catNaturaleza.setOnClickListener(v -> openCategory("Verde y naturaleza"));

        catLimpieza.setOnClickListener(v -> openCategory("Limpieza urbana"));

        catSalud.setOnClickListener(v -> openCategory("Salud y deporte"));

        catCultura.setOnClickListener(v -> openCategory("Cultura y sociedad"));
    }

    private void setupRecyclerViews(View view) {
        RecyclerView rvAcciones = view.findViewById(R.id.rvAcciones);
        RecyclerView rvSugeridas = view.findViewById(R.id.rvAccionesSugeridas);

        rvAcciones.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvSugeridas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapterAcciones = new ActAdapter(R.layout.act_card, this::openDetailsFragment);
        adapterSugeridas = new ActAdapter(R.layout.act_card, this::openDetailsFragment);

        rvAcciones.setAdapter(adapterAcciones);
        rvSugeridas.setAdapter(adapterSugeridas);
    }

    private void observeUser() {
        userViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                tvNombre.setText("Hola, " + usuario.getNombre());
            }
        });

        userViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null) {
                tvEmail.setText(email);
            }
        });
    }

    private void observeActs() {
        actViewModel.getActsByCreate().observe(getViewLifecycleOwner(), acts -> {
            if (acts == null) return;
            List<Act> limitedActsByCreate = new ArrayList<>(acts.subList(0, Math.min(5, acts.size())));
            adapterSugeridas.submitList(limitedActsByCreate);
        });
        actViewModel.getActsByFecha().observe(getViewLifecycleOwner(), acts -> {
            if (acts == null) return;
            List<Act> limitedActsByFecha = new ArrayList<>(acts.subList(0, Math.min(5, acts.size())));
            adapterAcciones.submitList(limitedActsByFecha);
        });
    }

    private void openDetailsFragment(Act act) {
        actViewModel.selectAct(act);

        DetailsActFragment fragment = new DetailsActFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openCategory(String categoryName) {

        CategoryFragment fragment = CategoryFragment.newInstance(categoryName);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
