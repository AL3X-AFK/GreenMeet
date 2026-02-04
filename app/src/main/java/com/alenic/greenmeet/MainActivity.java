package com.alenic.greenmeet;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.databinding.ActivityMainBinding;
import com.alenic.greenmeet.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Inicializamos el ViewModel
        userViewModel = new ViewModelProvider(this)
                .get(UserViewModel.class);

        // 2. Cargamos los datos del usuario (CLAVE)
        userViewModel.loadUser();

        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item->{

            int id = item.getItemId();

            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.explore) {
                replaceFragment(new ExploreFragment());
            } else if (id == R.id.collection) {
                replaceFragment(new SaveFragment());
            } else if (id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }


            return true;
        });

        binding.fabAdd.setOnClickListener(v -> {
            replaceFragment(new CreateActionFragment());
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.addToBackStack(null); // Si pulsas Atrás no cierra la app
        fragmentTransaction.commit();
    }

}