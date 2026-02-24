package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;


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
    private ImageView imgArte, imgNaturaleza, imgLimpieza, imgSalud, imgCultura;
    private View loadingLayout;

    private boolean isUserLoaded = false;
    private boolean isActsCreateLoaded = false;
    private boolean isActsFechaLoaded = false;

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
        loadCategoryImages();
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

        imgArte = view.findViewById(R.id.imgArte);
        imgNaturaleza = view.findViewById(R.id.imgNaturaleza);
        imgLimpieza = view.findViewById(R.id.imgLimpieza);
        imgSalud = view.findViewById(R.id.imgSalud);
        imgCultura = view.findViewById(R.id.imgCultura);

        loadingLayout = view.findViewById(R.id.loadingLayout);
    }

    private void setupListeners(){
        catArte.setOnClickListener(v -> openCategory(getString(R.string.arteUrbano)));

        catNaturaleza.setOnClickListener(v -> openCategory(getString(R.string.verdeYnaturaleza)));

        catLimpieza.setOnClickListener(v -> openCategory(getString(R.string.limpUrbana)));

        catSalud.setOnClickListener(v -> openCategory(getString(R.string.salYdeporte)));

        catCultura.setOnClickListener(v -> openCategory(getString(R.string.cultYsociedad)));
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

    private void loadCategoryImages() {

        String baseUrl = "https://hckkchzuxzmtjdjalohk.supabase.co/storage/v1/object/public/greenmeet/";

        Glide.with(this)
                .load(baseUrl + "arte.jpg")
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imgArte);

        Glide.with(this)
                .load(baseUrl + "natural.jpg")
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imgNaturaleza);

        Glide.with(this)
                .load(baseUrl + "clean.jpg")
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imgLimpieza);

        Glide.with(this)
                .load(baseUrl + "deporte.jpg")
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imgSalud);

        Glide.with(this)
                .load(baseUrl + "sociedad.jpg")
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(imgCultura);
    }

    private void observeUser() {
        userViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                tvNombre.setText(getString(R.string.hola) + usuario.getNombre());
                isUserLoaded = true;
                checkIfAllLoaded();
            }
        });

        tvEmail.setText(userViewModel.getEmail());
    }

    private void observeActs() {
        actViewModel.getActsByCreate().observe(getViewLifecycleOwner(), acts -> {
            if (acts == null) return;
            List<Act> limitedActsByCreate = new ArrayList<>(acts.subList(0, Math.min(5, acts.size())));
            adapterSugeridas.submitList(limitedActsByCreate);
            isActsCreateLoaded = true;
            checkIfAllLoaded();
        });
        actViewModel.getActsByFecha().observe(getViewLifecycleOwner(), acts -> {
            if (acts == null) return;
            List<Act> limitedActsByFecha = new ArrayList<>(acts.subList(0, Math.min(5, acts.size())));
            adapterAcciones.submitList(limitedActsByFecha);
            isActsFechaLoaded = true;
            checkIfAllLoaded();
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
    private void checkIfAllLoaded() {
        if (isUserLoaded && isActsCreateLoaded && isActsFechaLoaded) {
            loadingLayout.setVisibility(View.GONE);
        }
    }
}