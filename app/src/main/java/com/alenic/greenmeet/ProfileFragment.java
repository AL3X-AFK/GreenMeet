package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alenic.greenmeet.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

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

        userViewModel = new ViewModelProvider(requireActivity())
                .get(UserViewModel.class);

        userViewModel.getUsuario().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                tvName.setText(usuario.getNombre());
            }
        });

        userViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            tvEmail.setText(email);
        });

        view.findViewById(R.id.menu_saves).setOnClickListener(v -> openFragment(new SavesFragment()));
        view.findViewById(R.id.editProfile).setOnClickListener(v -> openFragment(new EditProfileFragment()));
//        view.findViewById(R.id.menu_notifications).setOnClickListener(v -> openFragment(new NotificationsFragment()));
        view.findViewById(R.id.menu_language).setOnClickListener(v -> openFragment(new LanguageFragment()));
        view.findViewById(R.id.menu_privacity).setOnClickListener(v -> openFragment(new PrivacyFragment()));
        view.findViewById(R.id.disconnect).setOnClickListener(v -> logout());

        return view;
    }


    private void openFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment) // Usamos el mismo contenedo  r que en MainActivity
                    .addToBackStack(null) // Para permitir volver con el botón de retroceso
                    .commit();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        userViewModel.clearSession();

        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}