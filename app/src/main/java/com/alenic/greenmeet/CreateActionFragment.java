package com.alenic.greenmeet;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;


import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.*;


public class CreateActionFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextInputEditText etDate;
    private TextInputLayout tilDate;

    private ImageView imgUpload;
    private Uri imageUri;

    private TextInputEditText etTitulo, etUbicacion, etDescripcion;

    private static final String SUPABASE_URL = "https://hckkchzuxzmtjdjalohk.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhja2tjaHp1eHptdGpkamFsb2hrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAxMTg5OTIsImV4cCI6MjA4NTY5NDk5Mn0.BfxJp7LdPSDsGm7N4NB8tnuSAQO4lsDzks53Vq2MqMA";
    private static final String BUCKET_NAME = "actions";

    public CreateActionFragment() {
        // Required empty public constructor
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgUpload.setImageURI(imageUri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_action, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etDate = view.findViewById(R.id.etDate);
        tilDate = view.findViewById(R.id.tilDate);

        imgUpload = view.findViewById(R.id.imgUpload);

        etTitulo = view.findViewById(R.id.tietTitle);
        etUbicacion = view.findViewById(R.id.tietLocation);
        etDescripcion = view.findViewById(R.id.tietDescription);

        Button btnNext = view.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> guardarAccion());

        LinearLayout layoutUpload = view.findViewById(R.id.layoutUpload);

        layoutUpload.setOnClickListener(v -> openFileChooser());

        View.OnClickListener openCalendarListener = v -> showDatePicker();

        etDate.setOnClickListener(openCalendarListener);
        tilDate.setEndIconOnClickListener(openCalendarListener);

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                requireContext(),
                R.style.DatePickerTheme,
                (view, y, m, d) -> etDate.setText(d + "/" + (m + 1) + "/" + y),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }



    private void guardarAccion() {

        String titulo = etTitulo.getText().toString().trim();
        String fecha = etDate.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || fecha.isEmpty() ||
                ubicacion.isEmpty() || descripcion.isEmpty()) {

            Toast.makeText(requireContext(),
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
            sendToSupabase(base64Image);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al leer la imagen", Toast.LENGTH_SHORT).show();
        }

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> accion = new HashMap<>();
        accion.put("titulo", titulo);
        accion.put("fecha", fecha);
        accion.put("ubicacion", ubicacion);
        accion.put("descripcion", descripcion);
        accion.put("imagenUrl", "imagen_hola"); //  por ahora fija
        accion.put("timestamp", FieldValue.serverTimestamp());

        db.collection("usuarios")
                .document(uid)
                .collection("acciones")
                .add(accion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(),
                            "Acción guardada",
                            Toast.LENGTH_SHORT).show();


                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error al guardar",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void sendToSupabase(String base64Image) {
        String filename = "imagen_" + System.currentTimeMillis() + ".jpg";
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;

        RequestBody body = RequestBody.create(
                Base64.decode(base64Image, Base64.DEFAULT),
                MediaType.parse("image/jpeg")
        );

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .header("Content-Type", "image/jpeg")
                .put(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("Supabase", "Imagen subida con éxito: " + url);
                    runOnUiThread(() -> Toast.makeText(requireContext(), "Imagen subida con éxito", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("Supabase", "Error al subir imagen: " + response.message());
                    runOnUiThread(() -> Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

