package com.example.tp_final_v1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MoneyConvertor extends AppCompatActivity {

    private final AtomicReference<JSONObject> ratesUSD = new AtomicReference<>(new JSONObject());
    private final AtomicReference<String> monedaSeleccionada1 = new AtomicReference<>("USD");
    private final AtomicReference<String> monedaSeleccionada2 = new AtomicReference<>("ARS");
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_convertor);

        EditText inputCurrency1 = findViewById(R.id.inputCurrency1);
        EditText inputCurrency2 = findViewById(R.id.inputCurrency2);
        Spinner spinner1 = findViewById(R.id.spinnerMonedaOrigen);
        Spinner spinner2 = findViewById(R.id.spinnerMonedaDestino);

        // --- Spinner setup ---
        String[] monedas = {"USD", "ARS", "EUR", "BRL", "JPY"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        // --- Selección de moneda ---
        spinner1.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                monedaSeleccionada1.set(parent.getItemAtPosition(position).toString());
                actualizarConversion(inputCurrency1, inputCurrency2);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinner2.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                monedaSeleccionada2.set(parent.getItemAtPosition(position).toString());
                actualizarConversion(inputCurrency1, inputCurrency2);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // --- Cargar tasas desde API ---
        new Thread(() -> {
            try {
                String jsonResultUSD = ApiHelper.getApiData("https://api.exchangerate-api.com/v4/latest/USD");
                JSONObject jsonObject = new JSONObject(jsonResultUSD);
                ratesUSD.set(jsonObject.getJSONObject("rates"));
                Log.d("API", "Tasas cargadas: " + ratesUSD.get().toString());
            } catch (Exception e) {
                Log.e("API_ERROR", "Error al obtener tasas USD", e);
            }
        }).start();

        // --- Listeners de inputs ---
        inputCurrency1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating.get()) return;
                if (s.toString().isEmpty()) return;
                try {
                    isUpdating.set(true);
                    double valor = Double.parseDouble(s.toString());
                    JSONObject rates = ratesUSD.get();
                    double tasaOrigen = rates.getDouble(monedaSeleccionada1.get());
                    double tasaDestino = rates.getDouble(monedaSeleccionada2.get());
                    double resultado = valor / tasaOrigen * tasaDestino;
                    inputCurrency2.setText(String.format("%.2f", resultado));
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
                if (isUpdating.get()) return;
                if (s.toString().isEmpty()) return;
                try {
                    isUpdating.set(true);
                    double valor = Double.parseDouble(s.toString());
                    JSONObject rates = ratesUSD.get();
                    double tasaOrigen = rates.getDouble(monedaSeleccionada1.get());
                    double tasaDestino = rates.getDouble(monedaSeleccionada2.get());
                    double resultado = valor / tasaDestino * tasaOrigen;
                    inputCurrency1.setText(String.format("%.2f", resultado));
                } catch (Exception e) {
                    inputCurrency1.setText("Error");
                    Log.e("CONVERT", "Error input2→1", e);
                } finally {
                    isUpdating.set(false);
                }
            }
        });


    }

    // --- Función de conversión centralizada ---
    private void actualizarConversion(EditText input1, EditText input2) {
        try {
            String valor = input1.getText().toString();
            if (valor.isEmpty()) return;
            double cantidad = Double.parseDouble(valor);
            JSONObject rates = ratesUSD.get();
            double tasaOrigen = rates.getDouble(monedaSeleccionada1.get());
            double tasaDestino = rates.getDouble(monedaSeleccionada2.get());
            double resultado = cantidad / tasaOrigen * tasaDestino;
            isUpdating.set(true);
            input2.setText(String.format("%.2f", resultado));
            isUpdating.set(false);
        } catch (Exception e) {
            input2.setText("Error");
            Log.e("CONVERT", "Error en conversion", e);
        }
    }
}
