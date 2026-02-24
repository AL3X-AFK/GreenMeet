package com.alenic.greenmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.adapters.ViewPageAdapter;

public class NavigationActivity extends AppCompatActivity {

    /**
     * Gestiona el onboarding (pantallas introductorias)
     * que se muestran la primera vez que el usuario abre la aplicación.
     * Utiliza un ViewPager para mostrar diferentes slides.
     */

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    ViewPageAdapter viewPageAdapter;
    Button skipButton, nextButton;
    TextView[] dots;

    //    Listener para detectar cambios de página en el ViewPager.
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            //  Actualiza los puntos indicadores
            setDotIndicator(position);
            // Cambia el texto del botón en la última página
            if (position == 2) {
                nextButton.setText(R.string.IniciarSesion);
            } else {
                nextButton.setText(R.string.siguiente);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activa el modo EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);

        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);

        /**
         * Botón "Siguiente"
         * - Si no es la última página > avanza
         * - Si es la última > termina el recorrido
         */
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(0) < 2) {
                    slideViewPager.setCurrentItem(getItem(1), true);
                } else {
                    finishNavigation();
                }
            }
        });

        skipButton.setOnClickListener(v -> finishNavigation());

        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);

        viewPageAdapter = new ViewPageAdapter(this);
        slideViewPager.setAdapter(viewPageAdapter);

        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }

    /**
     * Crea y actualiza los puntos indicadores inferiores.
     * El punto activo cambia de color.
     */
    public void setDotIndicator(int position) {
        dots = new TextView[viewPageAdapter.getCount()];
        dotIndicator.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            // Código HTML para crear un punto
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            // Color por defecto
            dots[i].setTextColor(getResources().getColor(R.color.darkgreen, getApplicationContext().getTheme()));
            dotIndicator.addView(dots[i]);
        }
        // Punto activo con color diferente
        dots[position].setTextColor(getResources().getColor(R.color.green_200, getApplicationContext().getTheme()));
    }

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }

    /**
     * Finaliza el onboarding:
     * - Redirige a LoginActivity
     * - Cierra esta Activity
     */
    private void finishNavigation() {
        getSharedPreferences("prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_done", true)
                .apply();

        startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
        finish();
    }

}