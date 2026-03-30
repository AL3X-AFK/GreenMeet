package com.alenic.greenmeet.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.User;
import com.alenic.greenmeet.repositories.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    private final MutableLiveData<User> usuario = new MutableLiveData<>();
    private final MutableLiveData<String> state = new MutableLiveData<>();

    // Igual que en actividades
    private static final String SUPABASE_URL = "https://hckkchzuxzmtjdjalohk.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhja2tjaHp1eHptdGpkamFsb2hrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAxMTg5OTIsImV4cCI6MjA4NTY5NDk5Mn0.BfxJp7LdPSDsGm7N4NB8tnuSAQO4lsDzks53Vq2MqMA";
    private static final String BUCKET_NAME = "greenmeet";

    public UserViewModel() {
        repository = new UserRepository();
    }

    public LiveData<User> getUsuario() {
        return usuario;
    }

    public String getEmail() {
        return repository.getCurrentEmail();
    }

    public LiveData<String> getState() {
        return state;
    }

    public void loadUser() {
        repository.getUser(new UserRepository.UserCallback<>() {
            @Override
            public void onSuccess(User result) {
                usuario.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void updateProfile(String nombre,
                              String telefono,
                              String genero,
                              Uri imageUri,
                              Context context) {

        User usuarioActual = usuario.getValue();
        if (usuarioActual == null) {
            state.setValue("Error inesperado");
            return;
        }

        new Thread(() -> {
            try {

                String imageUrl = null;

                //  SUBIDA A SUPABASE (igual que actividades)
                if (imageUri != null) {

                    InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

                    if (inputStream == null) {
                        state.postValue("Error al leer imagen");
                        return;
                    }

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int nRead;

                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }



                    buffer.flush();
                    byte[] bytes = buffer.toByteArray();
                    inputStream.close();

                    String filename = "user_" + System.currentTimeMillis() + ".jpg";

                    String publicUrl = SUPABASE_URL +
                            "/storage/v1/object/public/" +
                            BUCKET_NAME + "/" + filename;

                    String uploadUrl = SUPABASE_URL +
                            "/storage/v1/object/" + BUCKET_NAME + "/" + filename;

                    RequestBody body = RequestBody.create(bytes, MediaType.parse("image/jpeg"));

                    Request request = new Request.Builder()
                            .url(uploadUrl)
                            .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                            .header("Content-Type", "image/jpeg")
                            .put(body)
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()) {
                        Log.e("SUPABASE_ERROR", response.message());
                        Log.e("SUPABASE_CODE", String.valueOf(response.code()));
                        state.postValue("Error al subir imagen");
                        return;
                    }

                    imageUrl = publicUrl;
                }

                //  ACTUALIZAR DATOS
                usuarioActual.setNombre(nombre);
                usuarioActual.setTelefono(telefono);
                usuarioActual.setGenero(genero);

                if (imageUrl != null) {
                    usuarioActual.setImagenProfileURL(imageUrl);
                }



                //  GUARDAR EN FIREBASE
                repository.updateProfile(usuarioActual,
                        new UserRepository.UserCallback<>() {
                            @Override
                            public void onSuccess(Void result) {
                                state.postValue("UPDATE_SUCCESS");
                            }

                            @Override
                            public void onError(String error) {
                                state.postValue(error);
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                state.postValue("Error de conexión");
            }
        }).start();
    }

    public void clearSession() {
        usuario.setValue(null);
    }

    public void clearState() {
        state.setValue(null);
    }
}