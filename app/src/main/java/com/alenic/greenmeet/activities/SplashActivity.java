package com.alenic.greenmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alenic.greenmeet.MainActivity;
import com.alenic.greenmeet.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView title = findViewById(R.id.title);

        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_in);

        // Animaciones seguras
        logo.startAnimation(fade);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            title.startAnimation(fade);
        }, 200);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            boolean onboardingDone = getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("onboarding_done", false);

            Intent intent;

            if (!onboardingDone) {
                intent = new Intent(this, NavigationActivity.class);
            } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        }, 1000);
    }
}