package com.example.lab4_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest.permission;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityDescargaBinding;
import com.example.lab4_iot.entity.Employee;
import com.example.lab4_iot.services.EmployeeService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DescargaActivity extends AppCompatActivity {
    ActivityDescargaBinding binding;
    EmployeeService service;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDescargaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRetrofit();

        binding.btnDescargaLista.setOnClickListener(v -> {
            // Obtener el valor del EditText
            String codigoStr = binding.editTCodigo.getText().toString();

            // Comprobar si el EditText no está vacío
            if (!codigoStr.isEmpty()) {
                // Convertir la cadena a un entero
                Integer codigo = Integer.parseInt(codigoStr);

                // Mostrar el código en el log para verificación
                Log.d("codigo", String.valueOf(codigo));

                // Llamar al método para buscar empleados por managerId
                fetchEmployeesByManager(codigo);
            } else {
                Toast.makeText(DescargaActivity.this, "Por favor, ingrese un código", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.100.47.60:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EmployeeService.class);
    }

    private boolean hasPermission(String permission) {

        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        Toast.makeText(this, "No tiene permisos", Toast.LENGTH_SHORT).show();
    }

    private void fetchEmployeesByManager(int managerId) {
        Call<List<Employee>> call = service.getEmployeesByManager(managerId);
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful()) {
                    // Obtener la lista de empleados de la respuesta
                    List<Employee> employees = response.body();
                    //Si llega . Bien
                    // Imprimir la lista para verificación
                    for (Employee employee : employees) {
                        Log.d("EmployeeInfo", "ID: " + employee.id + ", Name: " + employee.first_name + " " + employee.last_name);
                    }

                    Toast.makeText(DescargaActivity.this, "Archivo descargado y guardado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DescargaActivity.this, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.d("msg", "Hay un problema");
            }
        });
    }

    private void writeListToTxtFile(List<Employee> employees) {
        try {
            File file = createOrGetFile("employees.txt");
            writeToFile(file, employees);
        } catch (IOException e) {
            Log.e("WriteToFile", "Error al exportar archivo", e);
            Toast.makeText(this, "Error al exportar archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private File createOrGetFile(String fileName) throws IOException {
        File file = new File(getExternalFilesDir(null), fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private void writeToFile(File file, List<Employee> employees) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {

            for (Employee employee : employees) {
                String employeeData = String.format("%s, %s, %s%n", employee.id, employee.first_name, employee.last_name);
                writer.write(employeeData);
            }

            writer.flush();
            Toast.makeText(this, "Archivo exportado correctamente", Toast.LENGTH_SHORT).show();
            Log.d("WriteToFile", "Archivo exportado correctamente");
        }
    }
}
