package com.alenic.greenmeet.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.utils.Utils;

public class LicenseFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvTitle;
    private View header;

    public LicenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_license, container, false);

        // Header y Título
        header = view.findViewById(R.id.headerBack);
        btnBack = header.findViewById(R.id.btnBack);
        tvTitle = header.findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.about_title));
        btnBack.setOnClickListener(v -> Utils.volver(this));

        // --- ENLACES PERSONA 1 ---
        view.findViewById(R.id.linkInstagram1).setOnClickListener(v -> abrirUrl("https://instagram.com/niicokngzz._"));
        view.findViewById(R.id.linkLinkedin1).setOnClickListener(v -> abrirUrl("https://www.linkedin.com/in/nicolas-gua%C3%B1una-45aa00332/"));
        view.findViewById(R.id.linkEmail1).setOnClickListener(v -> enviarEmail("nicolasguanuna19@gmail.com"));

        // --- ENLACES PERSONA 2 ---
        view.findViewById(R.id.linkInstagram2).setOnClickListener(v -> abrirUrl("https://instagram.com/user2"));
        view.findViewById(R.id.linkLinkedin2).setOnClickListener(v -> abrirUrl("https://linkedin.com/in/user2"));
        view.findViewById(R.id.linkEmail2).setOnClickListener(v -> enviarEmail("correo2@ejemplo.com"));

        // Lista de autores de fotos
        TextView tvListaAutores = view.findViewById(R.id.tvListaAutores);
        setupAutores(tvListaAutores);

        return view;
    }

    private void enviarEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la App");
        startActivity(intent);
    }

    private void abrirUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    private void setupAutores(TextView tv) {
        int[] autoresIds = {
                R.string.author_ryoji, R.string.author_martin, R.string.author_rhondak,
                R.string.author_eugene, R.string.author_guillermo, R.string.author_gia,
                R.string.author_hannah
        };

        StringBuilder sb = new StringBuilder();
        for (int id : autoresIds) {
            sb.append(getString(R.string.photo_by, getString(id))).append("\n");
        }
        tv.setText(sb.toString());
    }

}