package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.ActAdapter;
import com.alenic.greenmeet.adapters.GuardadosAdapter;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.Utils;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * Fragment encargado de mostrar la lista de actividades
 * y permitir su búsqueda en tiempo real.
 */

    public class ExploreFragment extends Fragment {

        private ActViewModel actViewModel;
        private ActAdapter adapter;
        private ImageButton btnBack;
        private TextView tvTitle;
        private View header;

        public ExploreFragment() {
            // Required empty public constructor
        }

        @Nullable
        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_explore, container, false);

            header = view.findViewById(R.id.headerBack);
            btnBack = header.findViewById(R.id.btnBack);
            tvTitle = header.findViewById(R.id.tvTitle);
            tvTitle.setText(getString(R.string.explorar));

            btnBack.setOnClickListener(v ->
                    Utils.volver(this));

            initViewModel();
            setupRecyclerView(view);
            setupSearch(view);
            observeActs();
            loadData();

            return view;
        }

        private void initViewModel() {
            actViewModel = new ViewModelProvider(requireActivity())
                    .get(ActViewModel.class);
        }

        //Configura el RecyclerView
        private void setupRecyclerView(View view) {

            RecyclerView rvActividades = view.findViewById(R.id.acProx);

            rvActividades.setLayoutManager(
                    new LinearLayoutManager(getContext())
            );

            adapter = new ActAdapter(R.layout.tarjeta_guardada,this::openDetailsFragment);

            rvActividades.setAdapter(adapter);
        }

    /**
     * Observa la lista de actividades desde el ViewModel.
     * Cuando cambian los datos, se actualiza la lista automáticamente.
     */
        private void observeActs() {

            actViewModel.getActsByCreate().observe(getViewLifecycleOwner(), acts -> {
                if (acts != null) {
                    adapter.submitList(acts);
                }
            });
        }

        private void loadData() {
            actViewModel.loadActsByCreate();
        }


    /**
     * Se ejecuta cuando el usuario pulsa una actividad.
     * Guarda la actividad seleccionada y navega al fragment de detalles.
     */
        private void openDetailsFragment(Act act) {
            actViewModel.selectAct(act);

            DetailsActFragment fragment = new DetailsActFragment();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    /**
     * Configura la búsqueda en tiempo real.
     * Cada vez que el texto cambia, se filtra la lista.
     */
        private void setupSearch(View view) {

            TextInputEditText searchEditText =
                    view.findViewById(R.id.searchEditText);

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    actViewModel.filterActs(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
