package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.MisActividadesAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;

import java.util.List;

public class MyactFragment extends Fragment {

    private ImageButton btnBack;
    private RecyclerView recyclerView;
    private TextView tvTitle;
    private View header;
    private MisActividadesAdapter adapter;
    private ActRepository repository;
    private LinearLayout layoutEmpty;

    public MyactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myact, container, false);

        recyclerView = view.findViewById(R.id.actGuardadasList);
        header = view.findViewById(R.id.headerBack);

        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);

        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        tvTitle.setText(getString(R.string.misActs));
        btnBack.setOnClickListener(v -> Utils.volver(this));

        // Inicializamos el repositorio de actividades
        repository = new ActRepository();

        // Adapter
        adapter = new MisActividadesAdapter(act -> {
            // Obtener el ViewModel
            ActViewModel actViewModel = new ViewModelProvider(requireActivity()).get(ActViewModel.class);

            // Seleccionar la actividad
            actViewModel.selectAct(act);

            // Abrir el fragment de edición
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new EditActFragment())
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Cargar actividades desde Firebase
        loadUserActivities();

        return view;
    }

    /**
     * Carga las actividades creadas por el usuario.
     * Actualiza el RecyclerView con los datos obtenidos desde Firebase.
     */
    private void loadUserActivities() {
        repository.getMyActs(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                if (result == null || result.isEmpty()) {
                    // Si no hay datos: mostramos aviso y ocultamos lista
                    layoutEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    // Si hay datos: ocultamos aviso y mostramos lista
                    layoutEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.submitList(result);
                }
            }

            @Override
            public void onError(String error) {
                // En caso de error, también es bueno mostrar el estado vacío o un error visual
                layoutEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }


}
