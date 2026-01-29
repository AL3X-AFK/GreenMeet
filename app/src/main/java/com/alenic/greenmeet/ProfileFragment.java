package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ProfileFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout editProfile = view.findViewById(R.id.editProfile);
        LinearLayout notifications = view.findViewById(R.id.menu_notifications);
        LinearLayout language = view.findViewById(R.id.menu_language);
        LinearLayout privacity = view.findViewById(R.id.menu_privacity);
        LinearLayout disconnect = view.findViewById(R.id.disconnect);

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