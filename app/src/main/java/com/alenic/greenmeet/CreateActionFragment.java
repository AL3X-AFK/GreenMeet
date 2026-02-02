package com.alenic.greenmeet;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alenic.greenmeet.objects.Accion;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class CreateActionFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private TextInputEditText etDate;
    private TextInputLayout tilDate;

    private ImageView imgUpload;
    private Uri imageUri;

    private TextInputEditText etTitulo, etUbicacion, etDescripcion;

    public CreateActionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_action, container, false);

        auth = FirebaseAuth.getInstance();

        etDate = view.findViewById(R.id.etDate);
        tilDate = view.findViewById(R.id.tilDate);

        imgUpload = view.findViewById(R.id.imgUpload);

        etTitulo = view.findViewById(R.id.tietTitle);
        etUbicacion = view.findViewById(R.id.tietLocation);
        etDescripcion = view.findViewById(R.id.tietDescription);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Button btnNext = view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> guardarAccion());

        LinearLayout layoutUpload = view.findViewById(R.id.layoutUpload);

        layoutUpload.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        View.OnClickListener openCalendarListener = v -> showDatePicker();

        etDate.setOnClickListener(openCalendarListener);
        tilDate.setEndIconOnClickListener(openCalendarListener);

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                R.style.DatePickerTheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    imageUri = uri;
                    imgUpload.setImageURI(uri);
                }
            });

    private void guardarAccion() {

        String titulo = etTitulo.getText().toString().trim();
        String fecha = etDate.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || fecha.isEmpty() || ubicacion.isEmpty()
                || descripcion.isEmpty() || imageUri == null) {

            Toast.makeText(requireContext(),
                    "Rellena todos los campos y sube una imagen",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        subirImagenYGuardar(titulo, fecha, ubicacion, descripcion);
    }

    private void subirImagenYGuardar(String titulo, String fecha,
                                     String ubicacion, String descripcion) {

        String uid = auth.getCurrentUser().getUid();

        String fileName = "acciones/" + uid + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Cuando la subida terminó correctamente
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        Accion accion = new Accion(
                                titulo,
                                fecha,
                                ubicacion,
                                descripcion,
                                imageUrl
                        );

                        db.collection("usuarios")
                                .document(uid)
                                .collection("acciones")
                                .add(accion)
                                .addOnSuccessListener(doc ->
                                        Toast.makeText(requireContext(),
                                                "Acción guardada correctamente",
                                                Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(),
                                                "Error al guardar acción",
                                                Toast.LENGTH_SHORT).show()
                                );
                    }).addOnFailureListener(e ->
                            Toast.makeText(requireContext(),
                                    "Error obteniendo URL de imagen",
                                    Toast.LENGTH_SHORT).show()
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Error subiendo imagen: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

}

