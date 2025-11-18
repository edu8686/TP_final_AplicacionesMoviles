package com.example.tp_final_v1;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private OkHttpClient client = new OkHttpClient();
    private String apiKey = "AIzaSyAicwc72kA3JQ58Yc5ctmkd3JYWysAR0_Q"; // 🔒 Reemplazá con tu API Key real

    private Map<String, LatLng> provinciasCoords = new HashMap<>();
    private String provinciaSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d("MapsActivity", "onCreate iniciado");

        // 🗺️ Spinner / Dropdown de provincias
        MaterialAutoCompleteTextView spinnerProvincias = findViewById(R.id.spinnerProvincia);
        String[] provincias = {
                "Buenos Aires", "Ciudad Autónoma de Buenos Aires", "Córdoba", "Santa Fe", "Mendoza", "Salta", "Tucumán",
                "Entre Ríos", "Misiones", "Neuquén", "Chubut", "San Luis", "San Juan",
                "Corrientes", "La Pampa", "Río Negro", "Formosa", "Jujuy", "La Rioja",
                "Catamarca", "Santiago del Estero", "Chaco", "Tierra del Fuego"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, provincias);
        spinnerProvincias.setAdapter(adapter);

        // 📍 Coordenadas aproximadas del centro de cada provincia
        provinciasCoords.put("Buenos Aires", new LatLng(-35.000, -59.000));
        provinciasCoords.put("Ciudad Autónoma de Buenos Aires", new LatLng(-34.6083, -58.3712));
        provinciasCoords.put("Córdoba", new LatLng(-31.4167, -64.1833));
        provinciasCoords.put("Santa Fe", new LatLng(-31.6333, -60.7000));
        provinciasCoords.put("Mendoza", new LatLng(-32.8908, -68.8272));
        provinciasCoords.put("Salta", new LatLng(-24.7821, -65.4232));
        provinciasCoords.put("Tucumán", new LatLng(-26.8167, -65.2167));
        provinciasCoords.put("Entre Ríos", new LatLng(-31.7833, -60.5167));
        provinciasCoords.put("Misiones", new LatLng(-27.3667, -55.9000));
        provinciasCoords.put("Neuquén", new LatLng(-38.9500, -68.0667));
        provinciasCoords.put("Chubut", new LatLng(-43.3000, -65.1000));
        provinciasCoords.put("San Luis", new LatLng(-33.3000, -66.3500));
        provinciasCoords.put("San Juan", new LatLng(-31.5333, -68.5167));
        provinciasCoords.put("Corrientes", new LatLng(-27.4667, -58.8333));
        provinciasCoords.put("La Pampa", new LatLng(-36.6167, -65.5000));
        provinciasCoords.put("Río Negro", new LatLng(-39.8000, -67.5000));
        provinciasCoords.put("Formosa", new LatLng(-26.1833, -58.1833));
        provinciasCoords.put("Jujuy", new LatLng(-24.1833, -65.3000));
        provinciasCoords.put("La Rioja", new LatLng(-29.4167, -66.8500));
        provinciasCoords.put("Catamarca", new LatLng(-28.4667, -65.7833));
        provinciasCoords.put("Santiago del Estero", new LatLng(-27.8000, -64.2667));
        provinciasCoords.put("Chaco", new LatLng(-26.9167, -60.6667));
        provinciasCoords.put("Tierra del Fuego", new LatLng(-54.8000, -68.3000));

        // 🎯 Listener para selección de provincia
        spinnerProvincias.setOnItemClickListener((parent, view, position, id) -> {
            provinciaSeleccionada = parent.getItemAtPosition(position).toString();
            Log.d("MapsActivity", "Provincia seleccionada: " + provinciaSeleccionada);
            if (gMap != null) {
                buscarCasasDeCambio(provinciaSeleccionada);
            } else {
                Log.w("MapsActivity", "Mapa no listo aún");
            }
        });

        // 🗺️ Inicializar mapa
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsActivity", "Error: fragmento de mapa no encontrado");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        Log.d("MapsActivity", "onMapReady: mapa inicializado");

        // Habilitar los controles de zoom en pantalla
        gMap.getUiSettings().setZoomControlsEnabled(true);

        // Posición inicial: centro del país
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-38.4161, -63.6167), 4f));

        // Si no hay selección, por defecto usar CABA
        if (provinciaSeleccionada == null) {
            provinciaSeleccionada = "Ciudad Autónoma de Buenos Aires";
        }

        buscarCasasDeCambio(provinciaSeleccionada);
    }

    private void buscarCasasDeCambio(String provincia) {
        if (gMap == null || provincia == null) {
            Log.w("MapsActivity", "buscarCasasDeCambio: mapa o provincia nulos");
            return;
        }

        Log.d("MapsActivity", "Buscando casas de cambio en: " + provincia);
        gMap.clear();

        String query = "casa de cambio en " + provincia + ", Argentina";
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="
                + Uri.encode(query) + "&key=" + apiKey;

        Log.d("MapsActivity", "URL de búsqueda: " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MapsActivity", "Error HTTP: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("MapsActivity", "Respuesta no exitosa: " + response.code());
                    return;
                }

                String body = response.body().string();
                Log.d("MapsActivity", "Respuesta recibida de la API");

                try {
                    JSONObject json = new JSONObject(body);
                    String status = json.getString("status");
                    Log.d("MapsActivity", "Estado de la API: " + status);

                    if (!status.equals("OK")) {
                        Log.e("MapsActivity", "Error en respuesta de API: " + status);
                        return;
                    }

                    JSONArray results = json.getJSONArray("results");

                    runOnUiThread(() -> {
                        try {
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);
                                JSONObject loc = place.getJSONObject("geometry").getJSONObject("location");
                                LatLng latLng = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                                String name = place.getString("name");

                                gMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                Log.d("MapsActivity", "Marcador agregado: " + name);
                            }

                            // Centrar cámara según provincia
                            LatLng center = provinciasCoords.get(provincia);
                            if (center != null) {
                                float zoom = provincia.equals("Ciudad Autónoma de Buenos Aires") ? 13f : 8.5f;
                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));
                                Log.d("MapsActivity", "Centrando cámara en " + provincia);
                            }

                        } catch (Exception e) {
                            Log.e("MapsActivity", "Error al procesar JSON", e);
                        }
                    });

                } catch (Exception e) {
                    Log.e("MapsActivity", "Error al parsear JSON", e);
                }
            }
        });
    }
}
