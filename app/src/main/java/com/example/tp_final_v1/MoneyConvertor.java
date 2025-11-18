
//CAMBIOSN CANDE
package com.example.tp_final_v1;

// Importaciones necesarias para la funcionalidad de Android y las librerías utilizadas
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Componentes de la librería Material Design para UI más moderna
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

// Para manejar datos JSON
import org.json.JSONObject;

// Para manejar fechas y formatos
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MoneyConvertor extends AppCompatActivity {
    // Almacena las tasas, monedas y tipo de dolar
    private final AtomicReference<JSONObject> ratesUSD = new AtomicReference<>(new JSONObject());
    private final AtomicReference<String> monedaSeleccionada1 = new AtomicReference<>("USD");
    private final AtomicReference<String> monedaSeleccionada2 = new AtomicReference<>("ARS");
    private final AtomicReference<String> tipoDolarSeleccionado = new AtomicReference<>("oficial");
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    private TextView textCotizacion, textActualizacion;

    // Uso de Ciclo de Vida - OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_convertor);

        TextInputEditText inputCurrency1 = findViewById(R.id.inputCurrency1);
        TextInputEditText inputCurrency2 = findViewById(R.id.inputCurrency2);
        MaterialAutoCompleteTextView spinnerOrigen = findViewById(R.id.spinnerMonedaOrigen);
        MaterialAutoCompleteTextView spinnerDestino = findViewById(R.id.spinnerMonedaDestino);
        MaterialAutoCompleteTextView spinnerTipoDolar = findViewById(R.id.spinnerTipoDolar);
        textCotizacion = findViewById(R.id.textCotizacion);
        textActualizacion = findViewById(R.id.textActualizacion);
        Button btnActivityMap = findViewById(R.id.btnVerMapa);

        // --- Tipos de dólar disponibles ---
        String[] tiposDolar = {"oficial", "blue", "bolsa", "tarjeta"};
        // Se crea un adapter para mostrar los tipos de dolar
        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tiposDolar);
        spinnerTipoDolar.setAdapter(adapterTipos);

        // --- Monedas ---
        String[] monedas = {"USD", "ARS", "EUR"};
        ArrayAdapter<String> adapterMonedas = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, monedas);
        spinnerOrigen.setAdapter(adapterMonedas);
        spinnerDestino.setAdapter(adapterMonedas);

        // --- Listeners --- Cuando el usuario selecciona un tipo de dolar - llama a la API
        spinnerTipoDolar.setOnItemClickListener((parent, view, position, id) -> {
            tipoDolarSeleccionado.set(spinnerTipoDolar.getText().toString());
            cargarDolarDesdeAPI(tipoDolarSeleccionado.get());
        });

        spinnerOrigen.setOnItemClickListener((parent, view, position, id) -> {
            monedaSeleccionada1.set(spinnerOrigen.getText().toString());
            actualizarConversion(inputCurrency1, inputCurrency2);
        });

        spinnerDestino.setOnItemClickListener((parent, view, position, id) -> {
            monedaSeleccionada2.set(spinnerDestino.getText().toString());
            actualizarConversion(inputCurrency1, inputCurrency2);
        });

        // Cargar dólar inicial
        cargarDolarDesdeAPI(tipoDolarSeleccionado.get());

        // Listeners de inputs
        inputCurrency1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating.get() || s.toString().isEmpty()) return;
                try {
                    isUpdating.set(true);
                    double valor = Double.parseDouble(s.toString());
                    JSONObject rates = ratesUSD.get();
                    double tasaOrigen = rates.getDouble(monedaSeleccionada1.get());
                    double tasaDestino = rates.getDouble(monedaSeleccionada2.get());
                    double resultado = valor / tasaOrigen * tasaDestino;
                    inputCurrency2.setText(String.format(Locale.US, "%.2f", resultado));
                } catch (Exception e) {
                    inputCurrency2.setText("Error");
                    Log.e("CONVERT", "Error input1→2", e);
                } finally {
                    isUpdating.set(false);
                }
            }
        });

        inputCurrency2.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating.get() || s.toString().isEmpty()) return;
                try {
                    isUpdating.set(true);
                    double valor = Double.parseDouble(s.toString());
                    JSONObject rates = ratesUSD.get();
                    double tasaOrigen = rates.getDouble(monedaSeleccionada1.get());
                    double tasaDestino = rates.getDouble(monedaSeleccionada2.get());
                    double resultado = valor / tasaDestino * tasaOrigen;
                    inputCurrency1.setText(String.format(Locale.US, "%.2f", resultado));
                } catch (Exception e) {
                    inputCurrency1.setText("Error");
                    Log.e("CONVERT", "Error input2→1", e);
                } finally {
                    isUpdating.set(false);
                }
            }
        });

        btnActivityMap.setOnClickListener(v -> {
            Intent intentAMaps = new Intent(MoneyConvertor.this, MapsActivity.class);
            startActivity(intentAMaps);
        });
    }

    // --- Cargar datos del dólar, desde la API---
    private void cargarDolarDesdeAPI(String tipoDolar) {
        new Thread(() -> {
            try {
                String url = "https://dolarapi.com/v1/dolares/" + tipoDolar;
                String jsonResult = ApiHelper.getApiData(url);
                JSONObject jsonObject = new JSONObject(jsonResult);

                double compra = jsonObject.getDouble("compra");
                double venta = jsonObject.getDouble("venta");
                String fechaISO = jsonObject.getString("fechaActualizacion");

                // Formatear fecha - tomando la ultima actualizacion de cada dolar
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                Date date = isoFormat.parse(fechaISO);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String fechaFormateada = outputFormat.format(date);

                runOnUiThread(() -> {
                    textCotizacion.setText(String.format("Compra: %.2f | Venta: %.2f", compra, venta));
                    textActualizacion.setText("Actualizado al: " + fechaFormateada);
                });

                // Guardar tasas base del dolar
                JSONObject rates = new JSONObject();
                rates.put("USD", 1.0);           // Base
                rates.put("ARS", venta);               // Desde la API del dólar
                rates.put("EUR", 1.03);          // Valor ejemplo
                ratesUSD.set(rates);

            } catch (Exception e) {
                Log.e("API_ERROR", "Error al cargar dólar: " + tipoDolar, e);
            }
        }).start();
    }

    // --- Función de conversión centralizada ---
    private void actualizarConversion(TextInputEditText input1, TextInputEditText input2) {
        try {
            String valor = input1.getText().toString();
            if (valor.isEmpty()) return;
            double cantidad = Double.parseDouble(valor);
            JSONObject rates = ratesUSD.get();
            double tasaOrigen = rates.getDouble(monedaSeleccionada1.get());
            double tasaDestino = rates.getDouble(monedaSeleccionada2.get());
            double resultado = cantidad / tasaOrigen * tasaDestino;
            isUpdating.set(true);
            input2.setText(String.format(Locale.US, "%.2f", resultado));
            isUpdating.set(false);
        } catch (Exception e) {
            input2.setText("Error");
            Log.e("CONVERT", "Error en conversion", e);
        }
    }
}

