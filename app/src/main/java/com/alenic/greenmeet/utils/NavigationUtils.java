package com.alenic.greenmeet.utils;

import androidx.fragment.app.Fragment;

public class NavigationUtils {

    public static void volver(Fragment fragment) {
        fragment.requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}