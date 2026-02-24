package com.alenic.greenmeet;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.databinding.ActivityMainBinding;
import com.alenic.greenmeet.fragments.CreateActFragment;
import com.alenic.greenmeet.fragments.ExploreFragment;
import com.alenic.greenmeet.fragments.HomeFragment;
import com.alenic.greenmeet.fragments.InscriptionsFragment;
import com.alenic.greenmeet.fragments.ProfileFragment;
import com.alenic.greenmeet.viewmodel.UserViewModel;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        loadLocale();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this)
                .get(UserViewModel.class);
        userViewModel.loadUser();

        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.explore) {
                replaceFragment(new ExploreFragment());
            } else if (id == R.id.collection) {
                replaceFragment(new InscriptionsFragment());
            } else if (id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        //Abrir Crear Actividad Fragment
        binding.fabAdd.setOnClickListener(v -> {
            replaceFragment(new CreateActFragment());
        });
    }

    //Método para cambiar entre fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Método para la configuración del idioma
    private void loadLocale() {

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String langCode = prefs.getString("app_lang", "es");

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }

}