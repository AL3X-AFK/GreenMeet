package com.alenic.greenmeet.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.ResponderDudasAdapter;
import com.alenic.greenmeet.data.Duda;
import com.alenic.greenmeet.repositories.ForoRepository;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ForoViewModel;

public class ResponderDudasFragment extends Fragment {

    private ForoViewModel foroViewModel;
    private ResponderDudasAdapter adapter;
    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responder_dudas, container, false);

        header = view.findViewById(R.id.headerBack);

        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);

        tvTitle.setText(getString(R.string.notifications));
        btnBack.setOnClickListener(v -> Utils.volver(this));

        RecyclerView rv = view.findViewById(R.id.rvDudasPendientes);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ResponderDudasAdapter(this::decidirAccion);
        rv.setAdapter(adapter);

        foroViewModel = new ViewModelProvider(requireActivity()).get(ForoViewModel.class);

        // Carga y muestra en tiempo real
        foroViewModel.loadNotificacionesCombinadas();
        foroViewModel.getNotificaciones().observe(getViewLifecycleOwner(), dudas -> {
            if(dudas != null) adapter.setDudas(dudas);
        });

        return view;
    }

    private void decidirAccion(Duda duda) {
        if (!duda.isRespondida()) {
            mostrarDialogoRespuesta(duda);
        } else {
            mostrarDialogoVerRespuesta(duda);
        }
    }

    private void mostrarDialogoRespuesta(Duda duda) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_responder, null);
        bottomSheetDialog.setContentView(sheetView);

        // Hacemos el fondo transparente para que se vean nuestras esquinas redondeadas
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternal != null) {
            bottomSheetInternal.setBackgroundResource(android.R.color.transparent);
        }

        // Vinculamos las vistas
        TextView tvActividad = sheetView.findViewById(R.id.tvContextoActividad);
        TextView tvPregunta = sheetView.findViewById(R.id.tvContextoPregunta);
        EditText etRespuesta = sheetView.findViewById(R.id.etRespuestaDuda);
        com.google.android.material.button.MaterialButton btnEnviar = sheetView.findViewById(R.id.btnConfirmarRespuesta);

        //Rellenar los datos
        tvActividad.setText("📍 " + duda.getTituloActividad() + "  |  👤 " + duda.getNombreAutor());
        tvPregunta.setText(duda.getPregunta());

        // Configurar el botón de enviar
        btnEnviar.setOnClickListener(v -> {
            String respuesta = etRespuesta.getText().toString().trim();
            if (!respuesta.isEmpty()) {
                bottomSheetDialog.dismiss(); // Cerrar el panel inferior
                confirmarEnvioFinal(duda.getId(), respuesta); // Lanzar el aviso final de seguridad
            } else {
                Toast.makeText(requireContext(), "La respuesta no puede estar vacía", Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar el panel
        bottomSheetDialog.show();
    }

    private void mostrarDialogoVerRespuesta(Duda duda) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bottom_sheet_leer, null);
        bottomSheetDialog.setContentView(sheetView);

        // Fondo transparente para bordes curvos
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternal != null) bottomSheetInternal.setBackgroundResource(android.R.color.transparent);

        TextView tvMiPregunta = sheetView.findViewById(R.id.tvMiPregunta);
        TextView tvLaRespuesta = sheetView.findViewById(R.id.tvLaRespuesta);
        com.google.android.material.button.MaterialButton btnEntendido = sheetView.findViewById(R.id.btnEntendido);

        tvMiPregunta.setText("Tú preguntaste:\n" + duda.getPregunta());
        tvLaRespuesta.setText(duda.getRespuesta());

        btnEntendido.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Al pulsar "Entendido", la marcamos como leída en Firebase
            new ForoRepository().marcarComoLeida(duda.getId());
        });

        bottomSheetDialog.show();
    }

    private void confirmarEnvioFinal(String dudaId, String respuesta) {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Confirmar envío")
                .setMessage("Una vez enviada, la respuesta será pública y no podrás borrarla ni modificarla. ¿Estás seguro?")
                .setPositiveButton("SÍ, ENVIAR", (dialog, which) -> {
                    foroViewModel.responder(dudaId, respuesta, new ForoRepository.ForoCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(requireContext(), "Respuesta enviada correctamente", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(String error) {
                            Toast.makeText(requireContext(), "Error al enviar", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("REVISAR", null)
                .show();
    }
}