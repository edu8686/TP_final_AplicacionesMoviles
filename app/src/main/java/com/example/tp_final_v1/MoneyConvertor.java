package com.example.tp_final_v1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

public class MoneyConvertor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_money_convertor);

        AtomicReference<Double> arsRate = new AtomicReference<>((double) 0);
        AtomicReference<Double> eurRate = new AtomicReference<>((double) 0);


        new Thread(() -> {
            try {
                // Llamada a la API
                String jsonResult = ApiHelper.getApiData("https://api.exchangerate-api.com/v4/latest/USD");

                // Parseo del JSON
                JSONObject jsonObject = new JSONObject(jsonResult);
                JSONObject rates = jsonObject.getJSONObject("rates");

                // Extraer valores específicos

                arsRate.set(rates.getDouble("ARS"));
                eurRate.set(rates.getDouble("EUR"));

                runOnUiThread(() -> {
                    Log.d("MoneyConvertor", jsonResult);
                    TextView textoInicial = findViewById(R.id.textoinicial);
                    String message = "1 USD = " + arsRate + " ARS\n1 USD = " + eurRate + " EUR";
                    textoInicial.setText(message);
                    textoInicial.setTextColor(Color.RED);
                    textoInicial.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                Log.e("API_ERROR", "Error al obtener datos de la API", e);
            }
        }).start();

        TextView resultado = findViewById(R.id.montoResultado);
       EditText inputCurrencyOrigin = findViewById(R.id.inputCurrencyOrigin);

        inputCurrencyOrigin.setOnEditorActionListener((v, actionId, event) -> {
            // Esto se dispara cuando se presiona Enter / Done
            String valorIngresado = inputCurrencyOrigin.getText().toString();
            // Hacer algo con el valor, por ejemplo convertirlo
            Log.d("INPUT", "Usuario ingresó: " + valorIngresado);
            double result = Calculations.calculateCurrency(10, arsRate.get());
            resultado.setText(String.valueOf(result));
            return true; // true indica que consumimos el evento
        });





    }
}