package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.MisActividadesAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.alenic.greenmeet.utils.NavigationUtils;

import java.util.List;

public class MyactFragment extends Fragment {

    private ImageButton btnBack;
    private RecyclerView recyclerView;
    private TextView tvTitle;
    private View header;
    private MisActividadesAdapter adapter;
    private ActRepository repository;

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

        tvTitle.setText("Mis actividades");
        btnBack.setOnClickListener(v -> NavigationUtils.volver(this));

        repository = new ActRepository();

        // Adapter
        adapter = new MisActividadesAdapter(act -> {
            // Click en "Ver más"
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Cargar actividades desde Firebase
        loadUserActivities();

        return view;
    }

    private void loadUserActivities() {
        repository.getMyActs(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                if (result.isEmpty()) {
                    Toast.makeText(getContext(), "No hay actividades", Toast.LENGTH_SHORT).show();
                }
                adapter.submitList(result); // Actualizamos el RecyclerView
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
