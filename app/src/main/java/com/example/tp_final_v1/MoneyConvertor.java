package com.example.tp_final_v1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MoneyConvertor extends AppCompatActivity {

    private final AtomicReference<JSONObject> ratesUSD = new AtomicReference<>(new JSONObject());
    private final AtomicReference<String> monedaSeleccionada1 = new AtomicReference<>("USD");
    private final AtomicReference<String> monedaSeleccionada2 = new AtomicReference<>("ARS");

    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    private void actualizarVisibilidadTipoDolar(TextInputLayout layoutTipoDolar,
                                                MaterialAutoCompleteTextView spinnerTipoDolar,
                                                String[] tiposDolar) {
        String origen = monedaSeleccionada1.get();
        String destino = monedaSeleccionada2.get();

        if ((origen.equals("USD") && destino.equals("ARS")) || (origen.equals("ARS") && destino.equals("USD"))) {
            layoutTipoDolar.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapterTipoDolar = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    tiposDolar
            );
            spinnerTipoDolar.setAdapter(adapterTipoDolar);

        } else {
            layoutTipoDolar.setVisibility(View.GONE);
        }
    }


    private void actualizarConversionMonedas() {
        String origen = monedaSeleccionada1.get();
        String destino = monedaSeleccionada2.get();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_convertor);

        Log.d("MoneyConvertor", "MoneyConvertor en onCreate");

        TextInputEditText inputCurrency1 = findViewById(R.id.inputCurrency1);
        TextInputEditText inputCurrency2 = findViewById(R.id.inputCurrency2);
        TextView textConversion = findViewById(R.id.textCotizacion);
        MaterialAutoCompleteTextView spinnerOrigen = findViewById(R.id.spinnerMonedaOrigen);
        MaterialAutoCompleteTextView spinnerDestino = findViewById(R.id.spinnerMonedaDestino);
        TextInputLayout layoutTipoDolar = findViewById(R.id.layoutTipoDolar);
        MaterialAutoCompleteTextView spinnerTipoDolar = findViewById(R.id.spinnerTipoDolar);
        Button btnActivityMap = findViewById(R.id.btnVerMapa);

        // --- Configuración de monedas ---
        String[] monedas = {"USD", "ARS", "EUR", "BRL", "JPY"};
        String[] tiposDolar = {"Oficial", "Blue", "MEP", "CCL", "Mayorista"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, // Layout simple para cada item
                monedas
        );
        spinnerOrigen.setAdapter(adapter);
        spinnerDestino.setAdapter(adapter);



        // --- Listeners de selección ---
        spinnerOrigen.setOnItemClickListener((parent, view, position, id) -> {
            monedaSeleccionada1.set(spinnerOrigen.getText().toString());
            actualizarConversion(inputCurrency1, inputCurrency2);
            actualizarVisibilidadTipoDolar(layoutTipoDolar, spinnerTipoDolar, tiposDolar);
        });

        spinnerDestino.setOnItemClickListener((parent, view, position, id) -> {
            monedaSeleccionada2.set(spinnerDestino.getText().toString());
            actualizarConversion(inputCurrency1, inputCurrency2);
            actualizarVisibilidadTipoDolar(layoutTipoDolar, spinnerTipoDolar, tiposDolar);
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



        btnActivityMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "Intent a MapsActivity: " + MapsActivity.class.getName());
                Intent intentAMaps = new Intent(MoneyConvertor.this, MapsActivity.class);
                startActivity(intentAMaps);
            }
        });
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
            input2.setText(String.format("%.2f", resultado));
            isUpdating.set(false);
        } catch (Exception e) {
            input2.setText("Error");
            Log.e("CONVERT", "Error en conversion", e);
        }
    }
}
