package com.alenic.greenmeet.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.bumptech.glide.Glide;

public class DetailsActFragment extends Fragment {

    private ActViewModel actViewModel;

    private TextView tvTitulo, tvCategoria, tvUbicacion, tvDescripcion, tvFecha;
    private ImageView imgHeader;
    private ImageButton btnBack;
    private Button btnApuntarse;

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
        btnApuntarse = view.findViewById(R.id.btnApuntarse);

        btnBack.setOnClickListener(v -> Utils.volver(this));
        // ViewModel compartido
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        actViewModel.getEstaApuntado().observe(getViewLifecycleOwner(), this::actualizarBoton);

        // Observar actividad seleccionada
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act == null) return;

            tvTitulo.setText(act.getTitulo());
            tvCategoria.setText(act.getCategoria());
            tvUbicacion.setText("📍 " + act.getUbicacion());
            tvDescripcion.setText(act.getDescripcion());
            tvFecha.setText(Utils.formatDate(act.getFecha()));

            Glide.with(this)
                    .load(act.getImagenUrl())
                    .centerCrop()
                    .into(imgHeader);

            // Comprobar si está apuntado
            actViewModel.comprobarSiEstaApuntado(act);
        });

        btnApuntarse.setOnClickListener(v -> {
            Act act = actViewModel.getSelectedAct().getValue();
            Boolean apuntado = actViewModel.getEstaApuntado().getValue();

            if (act == null || apuntado == null) return;

            if (apuntado) {
                actViewModel.desapuntarse(act);
            } else {
                actViewModel.apuntarse(act);
            }

        });

        return view;
    }

    private void actualizarBoton(boolean apuntado) {
        btnApuntarse.setText(apuntado ? "Desapuntarse" : "Me apunto");

        if (apuntado) {
            btnApuntarse.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red));
            btnApuntarse.setTextColor(Color.WHITE);
        } else {
            btnApuntarse.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_100));
            btnApuntarse.setTextColor(Color.BLACK);
        }
    }
}