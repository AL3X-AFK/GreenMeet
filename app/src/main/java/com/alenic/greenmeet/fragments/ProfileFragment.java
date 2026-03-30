package com.alenic.greenmeet.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alenic.greenmeet.activities.LoginActivity;
import com.alenic.greenmeet.R;
import com.alenic.greenmeet.repositories.AuthRepository;
import com.alenic.greenmeet.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvName = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);

        com.google.android.material.imageview.ShapeableImageView ivProfile = view.findViewById(R.id.ivProfileMain);

        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);

        userViewModel.loadUser();

        // Observamos los datos del usuario
        userViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                tvName.setText(usuario.getNombre());

                // CARGAR IMAGEN
                if (usuario.getImagenProfileURL() != null && !usuario.getImagenProfileURL().isEmpty()) {
                    com.bumptech.glide.Glide.with(this)
                            .load(usuario.getImagenProfileURL())
                            .into(ivProfile);
                }
            }
        });

        // Mostramos el email del usuario
        tvEmail.setText(userViewModel.getEmail());

        // Configuración de los menús
        view.findViewById(R.id.menu_saves).setOnClickListener(v -> openFragment(new MyactFragment()));
        view.findViewById(R.id.editProfile).setOnClickListener(v -> openFragment(new EditProfileFragment()));
//        view.findViewById(R.id.menu_notifications).setOnClickListener(v -> openFragment(new NotificationsFragment()));
        view.findViewById(R.id.menu_language).setOnClickListener(v -> openFragment(new LanguageFragment()));
        view.findViewById(R.id.menu_privacity).setOnClickListener(v -> openFragment(new PrivacyFragment()));
        view.findViewById(R.id.menu_acerca).setOnClickListener(v -> openFragment(new LicenseFragment()));

        view.findViewById(R.id.disconnect).setOnClickListener(v -> logout());

        return view;
    }


    /**
     * Método genérico para abrir un fragment en el contenedor principal de la actividad.
     */
    private void openFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment) // Usamos el mismo contenedo  r que en MainActivity
                    .addToBackStack(null) // Para permitir volver con el botón de retroceso
                    .commit();
        }
    }

    //Cierra la sesión del usuario y vuelve a la pantalla de login.
    private void logout() {
        // Llamamos al repositorio de autenticación para cerrar sesión
        new AuthRepository().logout();
        // Limpiamos los datos de sesión del ViewModel
        userViewModel.clearSession();

        // Abrimos LoginActivity y limpiamos el stack de actividades
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}