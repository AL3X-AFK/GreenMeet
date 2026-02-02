package com.alenic.greenmeet;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean onboardingDone = getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("onboarding_done", false);

        if (!onboardingDone) {
            // PRIMERA VEZ → navigation
            startActivity(new Intent(this, NavigationActivity.class));
        } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Ya vio navigation pero NO está logueado
            startActivity(new Intent(this, Login.class));
        } else {
            // Ya vio navigation y está logueado
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}
