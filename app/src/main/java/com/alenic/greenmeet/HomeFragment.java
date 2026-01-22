package com.alenic.greenmeet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alenic.greenmeet.objects.Accion;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rvAcciones = view.findViewById(R.id.rvAcciones);

        // Layout horizontal
        rvAcciones.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Datos estáticos (4 cards)
        List<Accion> acciones = new ArrayList<>();
        acciones.add(new Accion("Pintar mural", "Madrid", R.drawable.arte));
        acciones.add(new Accion("Limpieza parque", "Barcelona", R.drawable.arte));
        acciones.add(new Accion("Plantar árboles", "Valencia", R.drawable.arte));
        acciones.add(new Accion("Recogida basura", "Sevilla", R.drawable.arte));

        AccionAdapter adapter = new AccionAdapter(acciones);
        rvAcciones.setAdapter(adapter);

        return view;
    }
}