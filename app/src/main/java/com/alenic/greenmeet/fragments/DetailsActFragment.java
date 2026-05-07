package com.alenic.greenmeet.fragments;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.DudaAdapter;
import com.alenic.greenmeet.adapters.UserAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.alenic.greenmeet.viewmodel.ForoViewModel;
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
    private ForoViewModel foroViewModel;
    private DudaAdapter dudaAdapter;
    private RecyclerView rvForoPreview;

    private TextView tvTitulo, tvCategoria, tvUbicacion, tvDescripcion, tvFecha;
    private ImageView imgHeader;
    private ImageButton btnBack;
    private MaterialButton btnApuntarse;
    private RecyclerView rvTeam;
    private UserAdapter userAdapter;
    private UserRepository userRepository;

    private ActRepository actRepository;
    private TextView tvParticipantesLabel;
    private TextView btnVerTodoForo;
    private EditText etDudaInput;
    private ImageButton btnEnviarDuda;


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
        tvParticipantesLabel = view.findViewById(R.id.tvParticipantesLabel);
        btnVerTodoForo = view.findViewById(R.id.btnVerTodoForo);
        etDudaInput = view.findViewById(R.id.etDudaInput);
        btnEnviarDuda = view.findViewById(R.id.btnEnviarDuda);

        //Inicializar repositorio
        userRepository = new UserRepository();
        actRepository = new ActRepository();

        //Configurar RecyclerView
        userAdapter = new UserAdapter();
        rvTeam.setAdapter(userAdapter);
        rvTeam.setLayoutManager(new LinearLayoutManager(requireContext()));

        //Configurar RecyclerView Foro
        rvForoPreview = view.findViewById(R.id.rvForoPreview);
        dudaAdapter = new DudaAdapter();
        rvForoPreview.setAdapter(dudaAdapter);
        rvForoPreview.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Botón volver
        btnBack.setOnClickListener(v -> Utils.volver(this));
        actViewModel = new ViewModelProvider(requireActivity()).get(ActViewModel.class);
        foroViewModel = new ViewModelProvider(requireActivity()).get(ForoViewModel.class);

        // Observar si el usuario está apuntado
        actViewModel.getEstaApuntado().observe(getViewLifecycleOwner(), this::actualizarBoton);

        // Observar actividad seleccionada
        actViewModel.getSelectedAct().observe(getViewLifecycleOwner(), act -> {
            if (act == null) return;

            foroViewModel.loadDudas(act.getUid());

            btnEnviarDuda.setOnClickListener(v -> {
                String texto = etDudaInput.getText().toString().trim();
                if (!texto.isEmpty()) {
                    // Enviamos la duda
                    foroViewModel.enviarDuda(act.getUid(), act.getUserUid(), texto,act.getTitulo());

                    // Limpiamos el input y bajamos el teclado
                    etDudaInput.setText("");
                    ocultarTeclado();

                    Toast.makeText(requireContext(), R.string.pregunta_enviada, Toast.LENGTH_SHORT).show();
                } else {
                    etDudaInput.setError("Escribe algo");
                }
            });

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

        btnVerTodoForo.setOnClickListener(v -> {
            openFragment(new AllDudasFragment());
        });

        foroViewModel.getForoActividad().observe(getViewLifecycleOwner(), dudas -> {
            if (dudas != null) {
                btnVerTodoForo.setText(getString(R.string.ver_todo)+" (" + dudas.size() + ")");
                if (dudas.size() > 2) {
                    // Si hay más de 2, enviamos solo una sublista con las 2 primeras
                    dudaAdapter.setDudas(dudas.subList(0, 2));
                    btnVerTodoForo.setVisibility(View.VISIBLE); // Mostramos el botón
                } else {
                    dudaAdapter.setDudas(dudas);
                    btnVerTodoForo.setVisibility(View.GONE); // Ocultamos si hay pocas
                }
            }
        });

        return view;
    }

    private void openFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment) // Usamos el mismo contenedo  r que en MainActivity
                    .addToBackStack(null) // Para permitir volver con el botón de retroceso
                    .commit();
        }
    }

    private void ocultarTeclado() {
        View view = this.getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

        actRepository.getAsistentesByAct(act.getUid(), new ActRepository.ActCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> asistentes) {
                // 1. Actualizar el RecyclerView
                userAdapter.setUsers(asistentes);

                // 2. Calcular el tamaño y actualizar el label
                int conteo = (asistentes != null) ? asistentes.size() : 0;
                tvParticipantesLabel.setText(getString(R.string.participantes)+" (" + conteo + ")");
            }

            @Override
            public void onError(String error) {
                // En caso de error, mostramos (0) para no dejar el texto roto
                tvParticipantesLabel.setText(getString(R.string.participantes)+" (0)");
            }
        });
    }
}