package com.example.tp_final_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        Button convertorBtn = findViewById(R.id.btnConversor);
        convertorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conversorPage = new Intent(SecondActivity.this, MoneyConvertor.class);
                startActivity(conversorPage);
            }
        });

        Button btnDondeComprar = findViewById(R.id.btnDndComprar);
        btnDondeComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityMap = new Intent(SecondActivity.this, MapsActivity.class);
                startActivity(activityMap);
            }
        });

    }
}