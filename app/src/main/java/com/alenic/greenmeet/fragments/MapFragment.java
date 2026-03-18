package com.alenic.greenmeet.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alenic.greenmeet.R;
import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.viewmodel.ActViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private ActViewModel actViewModel;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    iniciarMapa();
                } else {
                    Toast.makeText(requireContext(),
                            "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                    // Carga el mapa igualmente sin ubicación
                    iniciarMapaSinUbicacion();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        // Pide el permiso al entrar al fragment
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarMapa();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        view.findViewById(R.id.btnZoomIn).setOnClickListener(v ->
                mapView.getController().zoomIn()
        );
        view.findViewById(R.id.btnZoomOut).setOnClickListener(v ->
                mapView.getController().zoomOut()
        );
        view.findViewById(R.id.btnOpenExplorer).setOnClickListener(v -> openFragment(new ExploreFragment()));

        actViewModel = new ViewModelProvider(this).get(ActViewModel.class);
        actViewModel.loadActsByFecha(); // Solo actividades futuras
        actViewModel.getActsByFecha().observe(getViewLifecycleOwner(), this::pintarActividades);

        return view;
    }

    private void iniciarMapa() {
        FusedLocationProviderClient fusedClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        // 1. Intenta con la última ubicación conocida (instantáneo)
        fusedClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                centrarEnUbicacion(new GeoPoint(location.getLatitude(), location.getLongitude()));
            } else {
                // 2. Si no hay última ubicación, pide una nueva
                LocationRequest request = new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 5000)
                        .setMaxUpdates(1)
                        .build();

                fusedClient.requestLocationUpdates(request,
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult result) {
                                Location loc = result.getLastLocation();
                                if (loc != null) {
                                    centrarEnUbicacion(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                                }
                                fusedClient.removeLocationUpdates(this);
                            }
                        },
                        Looper.getMainLooper()
                );
            }
        });

        // El punto azul del overlay sigue funcionando visualmente
        locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()), mapView
        );
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);
    }

    private void centrarEnUbicacion(GeoPoint punto) {
        mapView.getController().setZoom(16.0);
        mapView.getController().animateTo(punto);

        Marker marker = new Marker(mapView);
        marker.setPosition(punto);
        marker.setIcon(ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_you_are_here, null));
        marker.setOnMarkerClickListener((m, map) -> true);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void iniciarMapaSinUbicacion() {
        // Fallback: Madrid centrado
        mapView.getController().setZoom(13.0);
        mapView.getController().setCenter(new GeoPoint(40.4168, -3.7038));
        mapView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationOverlay != null) locationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationOverlay != null) locationOverlay.disableMyLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationOverlay != null) locationOverlay.disableMyLocation();
        mapView.onDetach();
    }

    private void openFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment) // Usamos el mismo contenedo  r que en MainActivity
                    .addToBackStack(null) // Para permitir volver con el botón de retroceso
                    .commit();
        }
    }

    private void pintarActividades(List<Act> acts) {
        // Elimina marcadores anteriores de actividades (no el de ubicación)
        mapView.getOverlays().removeIf(o -> o instanceof Marker && ((Marker) o).getTitle() != null);

        for (Act act : acts) {
            if (act.getLatitud() == 0 && act.getLongitud() == 0) continue;

            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(act.getLatitud(), act.getLongitud()));
            marker.setTitle(act.getTitulo());
            marker.setSnippet(act.getDescripcion());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.ic_act_marker, null));

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();
    }
}