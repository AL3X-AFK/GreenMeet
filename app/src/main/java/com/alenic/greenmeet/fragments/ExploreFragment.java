package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.alenic.greenmeet.viewmodel.ActViewModel;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    private ActViewModel actViewModel;
    private ActAdapter adapter;

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

        initViewModel();
        setupRecyclerView(view);
        observeActs();
        loadData();

        return view;
    }

    private void initViewModel() {
        actViewModel = new ViewModelProvider(requireActivity())
                .get(ActViewModel.class);
    }

    private void setupRecyclerView(View view) {

        RecyclerView rvActividades = view.findViewById(R.id.acProx);

        rvActividades.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        adapter = new ActAdapter(R.layout.tarjeta_guardada,this::openDetailsFragment);

        rvActividades.setAdapter(adapter);
    }

    private void observeActs() {

        actViewModel.getActs().observe(getViewLifecycleOwner(), acts -> {
            if (acts != null) {
                adapter.submitList(acts);
            }
        });
    }

    private void loadData() {
        actViewModel.loadActs();
    }


    private void openDetailsFragment(Act act) {
        actViewModel.selectAct(act);

        DetailsActFragment fragment = new DetailsActFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
