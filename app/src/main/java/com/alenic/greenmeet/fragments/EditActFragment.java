package com.alenic.greenmeet.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.utils.NavigationUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

public class EditActFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_act, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String actId = args.getString("actId");
            String title = args.getString("title");
            String description = args.getString("description");

            // Aquí puedes inicializar tus views con estos datos
        }

        return view;
    }

}
