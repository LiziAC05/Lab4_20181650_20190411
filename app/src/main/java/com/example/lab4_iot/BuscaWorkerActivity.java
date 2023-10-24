package com.example.lab4_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityBuscaWorkerBinding;
import com.example.lab4_iot.databinding.ActivityDescargaBinding;
import com.example.lab4_iot.entity.Employee;
import com.example.lab4_iot.services.EmployeeService;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuscaWorkerActivity extends AppCompatActivity {
    ActivityBuscaWorkerBinding binding;
    EmployeeService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuscaWorkerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tu inicialización de Retrofit aquí
        initRetrofit();

        binding.btnDescargaInfo.setOnClickListener(v -> {
            // Obtener el valor del EditText
            String codigoStr = binding.editIngresaCodW.getText().toString();

            // Comprobar si el EditText no está vacío
            if (!codigoStr.isEmpty()) {
                // Convertir la cadena a un entero
                Log.d("codigo",codigoStr);
                Integer codigo = Integer.parseInt(codigoStr);

                // Llamar al método para buscar empleados por ID
                fetchEmployeeById(codigo);
            } else {
                Toast.makeText(BuscaWorkerActivity.this, "Por favor, ingrese un código", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.142:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EmployeeService.class);
    }


    private void fetchEmployeeById(int employeeId) {
        Call<List<Employee>> call = service.buscarEmployees(employeeId);
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful()) {
                    List<Employee> employees = response.body();
                    if (!employees.isEmpty()) {
                        // Utiliza employees.get(0) para obtener el primer objeto Employee (si lo hay)
                        Employee employee = employees.get(0);
                        Log.d("EmployeeInfo", "ID: " + employee.id + ", Name: " + employee.first_name + " " + employee.last_name);
                        Toast.makeText(BuscaWorkerActivity.this, "Descargando Información de empleado", Toast.LENGTH_SHORT).show();
                    } else {
                        // No se encontró al empleado
                        Toast.makeText(BuscaWorkerActivity.this, "Empleado no encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BuscaWorkerActivity.this, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.d("msg", "Hay un problema");
            }
        });
    }


}