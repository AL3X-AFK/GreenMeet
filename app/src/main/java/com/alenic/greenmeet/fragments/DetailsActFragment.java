package com.alenic.greenmeet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.NavigationUtils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.bumptech.glide.Glide;

public class DetailsActFragment extends Fragment {

    private ActViewModel actViewModel;

    private TextView tvTitulo, tvCategoria, tvUbicacion, tvDescripcion, tvFecha;
    private ImageView imgHeader;
    private ImageButton btnBack;

    public DetailsActFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_act, container, false);

        // Views
        tvTitulo = view.findViewById(R.id.tvTitulo);
        tvCategoria = view.findViewById(R.id.tvCategory);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        tvFecha = view.findViewById(R.id.tvFecha);
        imgHeader = view.findViewById(R.id.imgHeader);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> NavigationUtils.volver(this));
        // ViewModel compartido
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        // Observar actividad seleccionada
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act == null) return;

            tvTitulo.setText(act.getTitulo());
            tvCategoria.setText(act.getCategoria());
            tvUbicacion.setText("📍 " + act.getUbicacion());
            tvDescripcion.setText(act.getDescripcion());
            tvFecha.setText(act.getFecha());

            Glide.with(this)
                    .load(act.getImagenUrl())
                    .placeholder(R.drawable.arte)
                    .centerCrop()
                    .into(imgHeader);
        });

        return view;
    }
}