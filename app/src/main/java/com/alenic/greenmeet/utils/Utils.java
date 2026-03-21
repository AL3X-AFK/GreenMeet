package com.alenic.greenmeet.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alenic.greenmeet.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // Método para volver al fragment anterior
    public static void volver(Fragment fragment) {
        fragment.requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    // Método para formatear la fecha
    public static String formatDate(long millis) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
    public static String categoriaToKey(Context context, String textoVisible) {
        String[] textos = {
                context.getString(R.string.arteUrbano),
                context.getString(R.string.verdeYnaturaleza),
                context.getString(R.string.limpUrbana),
                context.getString(R.string.salYdeporte),
                context.getString(R.string.cultYsociedad)
        };
        String[] keys = {
                "Arte urbano", "Verde y naturaleza",
                "Limpieza urbana", "Salud y deporte", "Cultura y sociedad"
        };

        int idx = Arrays.asList(textos).indexOf(textoVisible);
        return idx >= 0 ? keys[idx] : textoVisible;
    }

    public static String keyToCategoria(Context context, String key) {
        String[] keys = {
                "Arte urbano", "Verde y naturaleza",
                "Limpieza urbana", "Salud y deporte", "Cultura y sociedad"
        };
        String[] textos = {
                context.getString(R.string.arteUrbano),
                context.getString(R.string.verdeYnaturaleza),
                context.getString(R.string.limpUrbana),
                context.getString(R.string.salYdeporte),
                context.getString(R.string.cultYsociedad)
        };

        int idx = Arrays.asList(keys).indexOf(key);
        return idx >= 0 ? textos[idx] : key;
    }

}