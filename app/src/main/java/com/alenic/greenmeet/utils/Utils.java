package com.alenic.greenmeet.utils;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static void volver(Fragment fragment) {
        fragment.requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    public static String formatDate(long millis) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}