package com.alenic.greenmeet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

        RecyclerView rvAccionesSugeridas = view.findViewById(R.id.rvAccionesSugeridas);

        // Layout horizontal
        rvAcciones.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        rvAccionesSugeridas.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Datos estáticos (4 cards)
        List<Accion> acciones = new ArrayList<>();
        acciones.add(new Accion("Pintar mural","28/10/2024", "Madrid","lalalallaa", "R.drawable.arte"));
        acciones.add(new Accion("Pintar mural","28/10/2024", "Madrid","lalalallaa", "R.drawable.arte"));
        acciones.add(new Accion("Pintar mural","28/10/2024", "Madrid","lalalallaa", "R.drawable.arte"));
        acciones.add(new Accion("Pintar mural","28/10/2024", "Madrid","lalalallaa", "R.drawable.arte"));

        // Configurar el adaptador con el listener
        AccionAdapter adapter = new AccionAdapter(acciones, accion -> {
            // Al hacer clic en una acción, abre el fragment de detalles
            openDetailsActionFragment(accion);
        });

        rvAcciones.setAdapter(adapter);
        rvAccionesSugeridas.setAdapter(adapter);

        return view;
    }

    // Método para abrir DetailsActionFragment cuando se hace clic en una acción
    private void openDetailsActionFragment(Accion accion) {
        DetailsActionFragment fragment = new DetailsActionFragment();

        // Pasar los datos de la acción como argumentos al fragmento
        Bundle bundle = new Bundle();
        bundle.putString("titulo", accion.getTitulo());
        bundle.putString("ubicacion", accion.getUbicacion());
//        bundle.putInt("imagen", accion.imagen); // Si quieres pasar la imagen también
        fragment.setArguments(bundle);

        // Reemplazar el fragmento con DetailsActionFragment
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null); // Para permitir retroceder
        fragmentTransaction.commit();
    }
}