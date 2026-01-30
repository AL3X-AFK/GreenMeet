package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ProfileFragment extends Fragment {



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout editProfile = view.findViewById(R.id.editProfile);
        LinearLayout savesProfile = view.findViewById(R.id.menu_saves);
        LinearLayout notifications = view.findViewById(R.id.menu_notifications);
        LinearLayout language = view.findViewById(R.id.menu_language);
        LinearLayout privacity = view.findViewById(R.id.menu_privacity);
        LinearLayout disconnect = view.findViewById(R.id.disconnect);

        savesProfile.setOnClickListener(v -> openFragment(new SavesFragment()));
        editProfile.setOnClickListener(v -> openFragment(new EditProfileFragment()));
        notifications.setOnClickListener(v -> openFragment(new NotificationsFragment()));
        language.setOnClickListener(v -> openFragment(new LanguageFragment()));
        privacity.setOnClickListener(v -> openFragment(new PrivacyFragment()));
        disconnect.setOnClickListener(v -> {
            logout();
        });

        return view;
    }


    private void openFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment) // Usamos el mismo contenedor que en MainActivity
                    .addToBackStack(null) // Para permitir volver con el botón de retroceso
                    .commit();
        }
    }

    private void logout() {
        // Aquí puedes limpiar cualquier dato de sesión, SharedPreferences, token, etc.
        // Por ahora solo vamos a abrir Login y cerrar MainActivity

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // FLAG_ACTIVITY_CLEAR_TASK + NEW_TASK asegura que MainActivity se cierre y no puedas volver con back
            startActivity(intent);
            getActivity().finish();
        }
    }




}