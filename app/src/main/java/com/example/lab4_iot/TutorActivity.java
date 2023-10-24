package com.example.lab4_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TutorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor );
        Button descarga = findViewById(R.id.btnDownloadList);
        Button trabajador = findViewById(R.id.btnSearchW);
        Button asignar = findViewById(R.id.btnAsignTutor);
        descarga.setOnClickListener(view -> {
            Intent intent = new Intent(TutorActivity.this, DescargaActivity.class);
            startActivity(intent);
        });
        trabajador.setOnClickListener(view -> {
            Intent intent = new Intent(TutorActivity.this, BuscaWorkerActivity.class);
            startActivity(intent);
        });
        asignar.setOnClickListener(view -> {
            Intent intent = new Intent(TutorActivity.this, AsignarTutorActivity.class);
            startActivity(intent);
        });
    }
}