package com.alenic.greenmeet.utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NominatimService {

    public interface NominatimCallback {
        void onResults(List<NominatimResult> results);
        void onError(String error);
    }

    public static class NominatimResult {
        public String displayName;  // texto corto para mostrar en el dropdown
        public String fullAddress;  // para guardar en Firestore si necesitas
        public double lat;
        public double lon;

        public NominatimResult(String displayName, String fullAddress, double lat, double lon) {
            this.displayName = displayName;
            this.fullAddress = fullAddress;
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public String toString() {
            return displayName; // el AutoCompleteTextView usa esto para mostrar
        }
    }

    private static final OkHttpClient client = new OkHttpClient();

    public static void search(String query, double lat, double lon, NominatimCallback callback) {
        String url = "https://nominatim.openstreetmap.org/search?q="
                + Uri.encode(query)
                + "&format=json&limit=8&accept-language=es"
                + "&countrycodes=es"
                + "&addressdetails=1"   // ← pedir desglose de dirección
                + "&lat=" + lat
                + "&lon=" + lon;

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "GreenMeet/1.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error: " + response.code());
                    return;
                }

                String json = response.body().string();
                List<NominatimResult> results = parseResults(json);
                callback.onResults(results);
            }
        });
    }

    private static List<NominatimResult> parseResults(String json) {
        List<NominatimResult> results = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");
                results.add(new NominatimResult(
                        buildShortName(obj),        // bonito para el usuario
                        obj.getString("display_name"), // completo para guardar
                        lat, lon
                ));
            }
        } catch (Exception e) {
            Log.e("Nominatim", "Parse error: " + e.getMessage());
        }
        return results;
    }

    private static String buildShortName(JSONObject obj) throws JSONException {
        JSONObject address = obj.optJSONObject("address");
        if (address == null) return obj.getString("display_name");

        StringBuilder sb = new StringBuilder();

        // Línea 1: calle + número
        String road = address.optString("road", "");
        String number = address.optString("house_number", "");
        if (!road.isEmpty()) {
            sb.append(road);
            if (!number.isEmpty()) sb.append(", ").append(number);
        }

        // Si no hay calle, usar el nombre del lugar (parque, bar, etc.)
        if (sb.length() == 0) {
            String name = address.optString("amenity",
                    address.optString("leisure",
                            address.optString("tourism",
                                    address.optString("shop", ""))));
            sb.append(name);
        }

        // Línea 2: barrio/distrito + ciudad
        String suburb = address.optString("suburb",
                address.optString("quarter",
                        address.optString("neighbourhood", "")));
        String city = address.optString("city",
                address.optString("town",
                        address.optString("village", "")));

        StringBuilder line2 = new StringBuilder();
        if (!suburb.isEmpty()) line2.append(suburb);
        if (!city.isEmpty()) {
            if (line2.length() > 0) line2.append(", ");
            line2.append(city);
        }

        if (line2.length() > 0) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(line2);
        }

        return sb.length() > 0 ? sb.toString() : obj.getString("display_name");
    }
}