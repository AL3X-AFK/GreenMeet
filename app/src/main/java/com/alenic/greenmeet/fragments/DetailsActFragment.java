package com.alenic.greenmeet.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.UserAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.alenic.greenmeet.data.User;
import com.alenic.greenmeet.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment que muestra el detalle completo de una actividad.
 * Permite:
 * - Ver información detallada
 * - Apuntarse o desapuntarse
 */
public class DetailsActFragment extends Fragment {

    private ActViewModel actViewModel;

    private TextView tvTitulo, tvCategoria, tvUbicacion, tvDescripcion, tvFecha;
    private ImageView imgHeader;
    private ImageButton btnBack;
    private MaterialButton btnApuntarse;
    private RecyclerView rvTeam;
    private UserAdapter userAdapter;
    private UserRepository userRepository;

    private ActRepository actRepository;


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
        rvTeam = view.findViewById(R.id.rvTeam);

        //Inicializar repositorio
        userRepository = new UserRepository();
        actRepository = new ActRepository();

        //Configurar RecyclerView
        userAdapter = new UserAdapter();
        rvTeam.setAdapter(userAdapter);
        rvTeam.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Botón volver
        btnBack.setOnClickListener(v -> Utils.volver(this));
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);

        // Observar si el usuario está apuntado
        actViewModel.getEstaApuntado().observe(getViewLifecycleOwner(), this::actualizarBoton);

        // Observar actividad seleccionada
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act == null) return;

            // Mostrar datos
            tvTitulo.setText(act.getTitulo());
            tvCategoria.setText(act.getCategoria());
            tvUbicacion.setText("📍 " + act.getUbicacion());
            tvDescripcion.setText(act.getDescripcion());
            tvFecha.setText(Utils.formatDate(act.getFecha()));

            // Cargar imagen con Glide
            Glide.with(this)
                    .load(act.getImagenUrl())
                    .centerCrop()
                    .into(imgHeader);

            long currentTime = System.currentTimeMillis();

            // La actividad ya pasó pues mostrar botón gris
            if (act.getFecha() < currentTime) {
                btnApuntarse.setEnabled(false);
                btnApuntarse.setText(getString(R.string.completed));
                btnApuntarse.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray));
                btnApuntarse.setTextColor(Color.WHITE);

            } else {
                // La actividad aún no ha pasado pues comportamiento normal
                btnApuntarse.setEnabled(true);
                actViewModel.comprobarSiEstaApuntado(act);
            }
            userAdapter.setUsers(new ArrayList<>());
            cargarParticipantes(act);
        });

        // Click en apuntarse / desapuntarse
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

    /**
     * Actualiza el estado visual del botón según
     * si el usuario está apuntado o no.
     */
    private void actualizarBoton(boolean apuntado) {
        btnApuntarse.setText(apuntado ? getString(R.string.desapuntarse) : getString(R.string.meApunto));

        if (apuntado) {
            btnApuntarse.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red));
            btnApuntarse.setTextColor(Color.WHITE);
        } else {
            btnApuntarse.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_100));
            btnApuntarse.setTextColor(Color.BLACK);
        }
    }

    /**
     *Cargar participantes desde Firestore
     */
    private void cargarParticipantes(Act act) {
        if (act == null || act.getUid() == null) return;

        // Llamamos al método limpio del repositorio
        actRepository.getAsistentesByAct(act.getUid(), new ActRepository.ActCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> asistentes) {
                // El adapter simplemente recibe la lista final
                userAdapter.setUsers(asistentes);
            }

            @Override
            public void onError(String error) {
                // Manejar error de carga si es necesario
            }
        });
    }
}