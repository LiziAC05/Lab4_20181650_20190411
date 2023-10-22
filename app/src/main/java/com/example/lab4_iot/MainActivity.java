package com.example.lab4_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.number.IntegerWidth;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button worker = findViewById(R.id.btnTrabajador);
        worker.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TrabajadorActivity.class);
            startActivity(intent);
        });
        Button tutor = findViewById(R.id.btnTutor);
        tutor.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TutorActivity.class);
            startActivity(intent);
        });
    }
}