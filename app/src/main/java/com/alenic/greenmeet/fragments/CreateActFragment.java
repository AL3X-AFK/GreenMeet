package com.alenic.greenmeet.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;


import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.*;
import android.app.Activity;


public class CreateActFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextInputLayout tilDate,tilCategoria;
    private LinearLayout layoutPlaceholder;

    private ImageView imgUpload;
    private Uri imageUri;
    private ImageButton btnBack;
    private Button btnCancel,btnNext;
    private TextInputEditText etTitulo, etUbicacion, etDescripcion,etDate;
    private AutoCompleteTextView actvCategoria;

    private static final String SUPABASE_URL = "https://hckkchzuxzmtjdjalohk.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhja2tjaHp1eHptdGpkamFsb2hrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAxMTg5OTIsImV4cCI6MjA4NTY5NDk5Mn0.BfxJp7LdPSDsGm7N4NB8tnuSAQO4lsDzks53Vq2MqMA";
    private static final String BUCKET_NAME = "actions";

    public CreateActFragment() {
        // Required empty public constructor
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();

                    imgUpload.setImageURI(imageUri);
                    imgUpload.setVisibility(View.VISIBLE);
                    layoutPlaceholder.setVisibility(View.GONE);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_create_act, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etDate = view.findViewById(R.id.etDate);
        tilDate = view.findViewById(R.id.tilDate);
        tilCategoria = view.findViewById(R.id.tilCategoria);
        actvCategoria = view.findViewById(R.id.actvCategoria);

        imgUpload = view.findViewById(R.id.imgUpload);

        etTitulo = view.findViewById(R.id.tietTitle);
        etUbicacion = view.findViewById(R.id.tietLocation);
        etDescripcion = view.findViewById(R.id.tietDescription);

        btnCancel = view.findViewById(R.id.btnCancel);
        btnBack = view.findViewById(R.id.btnBack);
        btnNext = view.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> guardarAccion());
        btnBack.setOnClickListener(v -> volver());
        btnCancel.setOnClickListener(v -> volver());

        LinearLayout layoutUpload = view.findViewById(R.id.layoutUpload);

        layoutPlaceholder = view.findViewById(R.id.layoutPlaceholder);

        layoutUpload.setOnClickListener(v -> openFileChooser());

        View.OnClickListener openCalendarListener = v -> showDatePicker();

        etDate.setOnClickListener(openCalendarListener);
        tilDate.setEndIconOnClickListener(openCalendarListener);

        // Opciones fijas
        String[] categorias = {"ARTE URBANO","VERDE Y NATURALEZA","LIMPIEZA URBANA","SALUD Y DEPORTE", "CULTURA Y SOCIEDAD",};

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );

        actvCategoria.setAdapter(categoriaAdapter);

// Evita escribir texto manual
        actvCategoria.setKeyListener(null);

// Abre el desplegable al tocar
        actvCategoria.setOnClickListener(v -> actvCategoria.showDropDown());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void showDatePicker() {

        MaterialDatePicker<Long> picker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selecciona fecha")
                        .setTheme(R.style.MyMaterialCalendarTheme)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        picker.show(getParentFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            // selection viene en millis (Long)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fecha = sdf.format(new Date(selection));
            etDate.setText(fecha);
        });
    }
    // Opciones fijas
    String[] categorias = {"ARTE URBANO", "CULTURA Y SOCIEDAD"};

    private void volver(){
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void guardarAccion() {

        String titulo = etTitulo.getText().toString().trim();
        String fecha = etDate.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = actvCategoria.getText().toString().trim();

        if (titulo.isEmpty() || fecha.isEmpty() ||
                ubicacion.isEmpty() || descripcion.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

            // ⬇️ ahora pasamos TODOS los datos
            sendToSupabase(base64Image, titulo, fecha, ubicacion, descripcion,categoria);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al leer la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToSupabase(
            String base64Image,
            String titulo,
            String fecha,
            String ubicacion,
            String descripcion,
            String categoria
    ) {
        String filename = "imagen_" + System.currentTimeMillis() + ".jpg";

        String publicUrl = SUPABASE_URL
                + "/storage/v1/object/public/"
                + BUCKET_NAME + "/"
                + filename;

        String uploadUrl = SUPABASE_URL
                + "/storage/v1/object/"
                + BUCKET_NAME + "/"
                + filename;

        RequestBody body = RequestBody.create(
                Base64.decode(base64Image, Base64.DEFAULT),
                MediaType.parse("image/jpeg")
        );

        Request request = new Request.Builder()
                .url(uploadUrl)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .header("Content-Type", "image/jpeg")
                .put(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {

                    guardarAccionFirestore(
                            titulo,
                            fecha,
                            ubicacion,
                            descripcion,
                            categoria,
                            publicUrl
                    );

                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
                    );

                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void guardarAccionFirestore(
            String titulo,
            String fecha,
            String ubicacion,
            String descripcion,
            String categoria,
            String imagenUrl
    ) {
        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> accion = new HashMap<>();
        accion.put("titulo", titulo);
        accion.put("fecha", fecha);
        accion.put("ubicacion", ubicacion);
        accion.put("descripcion", descripcion);
        accion.put("categoria", categoria);
        accion.put("imagenUrl", imagenUrl);
        accion.put("timestamp", FieldValue.serverTimestamp());

        requireActivity().runOnUiThread(() -> {
            db.collection("usuarios")
                    .document(uid)
                    .collection("acciones")
                    .add(accion)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(requireContext(), "Acción guardada", Toast.LENGTH_SHORT).show();
                        volver();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}

