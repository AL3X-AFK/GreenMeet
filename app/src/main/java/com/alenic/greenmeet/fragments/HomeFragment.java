package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.adapters.ActAdapter;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.alenic.greenmeet.viewmodel.UserViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ActViewModel actViewModel;
    private ActAdapter adapter;
    private TextView tvNombre;
    private TextView tvEmail;
    private UserViewModel userViewModel;
    private ActAdapter adapterAcciones;
    private ActAdapter adapterSugeridas;

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


        tvNombre = view.findViewById(R.id.tvNombre);
        tvEmail = view.findViewById(R.id.tvEmail);
        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);

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


        RecyclerView rvAcciones = view.findViewById(R.id.rvAcciones);
        RecyclerView rvAccionesSugeridas = view.findViewById(R.id.rvAccionesSugeridas);

        // Layout horizontal
        rvAcciones.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        rvAccionesSugeridas.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Adapters
        adapterAcciones = new ActAdapter(new ArrayList<>(), act -> {
            actViewModel.selectAct(act);
            openDetailsActivityFragment(act);
        });

        adapterSugeridas = new ActAdapter(new ArrayList<>(), act -> {
            actViewModel.selectAct(act);
            openDetailsActivityFragment(act);
        });


        rvAcciones.setAdapter(adapterAcciones);
        rvAccionesSugeridas.setAdapter(adapterSugeridas);

        // ViewModel
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        // Observamos lista de actividades
        actViewModel.getActs().observe(getViewLifecycleOwner(), acts -> {

            if (acts == null || acts.isEmpty()) return;

            //acciones fecha proxima
            List<Act> accionesOrdenadas = new ArrayList<>(acts);

            Collections.sort(accionesOrdenadas, (a1, a2) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                try {
                    Date d1 = sdf.parse(a1.getFecha());
                    Date d2 = sdf.parse(a2.getFecha());

                    if (d1 == null || d2 == null) return 0;

                    return d1.compareTo(d2); // más próxima primero
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            });

            if (accionesOrdenadas.size() > 5) {
                accionesOrdenadas = accionesOrdenadas.subList(0, 5);
            }

            adapterAcciones.setActs(accionesOrdenadas);

            // acciones sugeridas
            List<Act> sugeridas = new ArrayList<>(acts);
            Collections.shuffle(sugeridas);

            if (sugeridas.size() > 5) {
                sugeridas = sugeridas.subList(0, 5);
            }

            adapterSugeridas.setActs(sugeridas);
        });

        // Cargar datos desde Firebase
        actViewModel.loadActs();
        userViewModel.loadUser();

        return view;
    }

    private void openDetailsActivityFragment(Act act) {
        DetailsActFragment fragment = new DetailsActFragment();

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
