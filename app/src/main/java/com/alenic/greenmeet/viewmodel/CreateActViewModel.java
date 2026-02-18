package com.alenic.greenmeet.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateActViewModel extends ViewModel {

    private final MutableLiveData<Boolean> uploadSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();
    private final ActRepository repository;

    private static final String SUPABASE_URL = "https://hckkchzuxzmtjdjalohk.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhja2tjaHp1eHptdGpkamFsb2hrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAxMTg5OTIsImV4cCI6MjA4NTY5NDk5Mn0.BfxJp7LdPSDsGm7N4NB8tnuSAQO4lsDzks53Vq2MqMA";
    private static final String BUCKET_NAME = "actions";

    public CreateActViewModel() {
        repository = new ActRepository();
    }

    public LiveData<Boolean> getUploadSuccess() {
        return uploadSuccess;
    }

    public LiveData<String> getUploadError() {
        return uploadError;
    }

    public void uploadAct(Context context,
                          Uri imageUri,
                          String titulo,
                          long fechaMillis,
                          String ubicacion,
                          String descripcion,
                          String categoria) {

        if (imageUri == null) {
            uploadError.postValue("Selecciona una imagen primero");
            return;
        }

        if (titulo == null || titulo.isEmpty()) {
            uploadError.postValue("El título es obligatorio");
            return;
        }

        if (fechaMillis==0) {
            uploadError.postValue("La fecha es obligatoria");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            uploadError.postValue("Usuario no autenticado");
            return;
        }

        new Thread(() -> {
            try {
                InputStream inputStream =
                        context.getContentResolver().openInputStream(imageUri);

                if (inputStream == null) {
                    uploadError.postValue("No se pudo leer la imagen");
                    return;
                }

                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();

                String filename = "imagen_" + System.currentTimeMillis() + ".jpg";

                String publicUrl = SUPABASE_URL +
                        "/storage/v1/object/public/" +
                        BUCKET_NAME + "/" + filename;

                String uploadUrl = SUPABASE_URL +
                        "/storage/v1/object/" +
                        BUCKET_NAME + "/" + filename;

                RequestBody body = RequestBody.create(
                        bytes,
                        MediaType.parse("image/jpeg")
                );

                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .header("Content-Type", "image/jpeg")
                        .put(body)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    uploadError.postValue("Error al subir imagen");
                    return;
                }

                // Crear objeto Act
                Act act = new Act(
                        titulo,
                        categoria,
                        fechaMillis,
                        ubicacion,
                        descripcion,
                        publicUrl,
                        user.getUid()
                );

                repository.addAct(act, new ActRepository.ActCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        uploadSuccess.postValue(true);
                    }

                    @Override
                    public void onError(String error) {
                        uploadError.postValue(error);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                uploadError.postValue("Error de conexión");
            }
        }).start();
    }
}