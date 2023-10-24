package com.example.lab4_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityBuscaWorkerBinding;
import com.example.lab4_iot.databinding.ActivityDescargaBinding;
import com.example.lab4_iot.entity.Employee;
import com.example.lab4_iot.services.EmployeeService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

        initRetrofit();

        binding.btnDescargaInfo.setOnClickListener(v -> {
            String codigoStr = binding.editIngresaCodW.getText().toString();
            if (!codigoStr.isEmpty()) {
                Log.d("codigo", codigoStr);
                Integer codigo = Integer.parseInt(codigoStr);
                fetchEmployeeById(codigo);
            } else {
                Toast.makeText(BuscaWorkerActivity.this, "Por favor, ingrese un código", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigIp.BASE_URL)
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
                        // Guardar en un archivo .txt
                        saveEmployeeInfoToFile(employees, employeeId);
                        saveEmployeeInfoToMediaStore(employees, employeeId);
                    } else {
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

    private void saveEmployeeInfoToFile(List<Employee> employees, int employeeId) {
        try {
            String filename = "info_empleado(" + employeeId + ").txt";
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(directory, filename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Employee employee : employees) {
                bw.write("ID: " + employee.id + ", Name: " + employee.first_name + " " + employee.last_name);
                bw.newLine();
            }

            bw.close();

            Toast.makeText(this, "Información de empleado guardada en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la información del empleado", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEmployeeInfoToMediaStore(List<Employee> employees, int employeeId) {
        try {
            String filename = "info_empleado(" + employeeId + ").txt";
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
            Uri externalContentUri = MediaStore.Files.getContentUri("external");
            Uri fileUri = getContentResolver().insert(externalContentUri, values);

            if (fileUri != null) {
                OutputStream os = getContentResolver().openOutputStream(fileUri);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                for (Employee employee : employees) {
                    bw.write("ID: " + employee.id + ", Name: " + employee.first_name + " " + employee.last_name);
                    bw.newLine();
                }
                bw.close();
                if (os != null) {
                    os.close();
                }
                Toast.makeText(this, "Información de empleado también guardada en: " + fileUri.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar archivo en MediaStore", Toast.LENGTH_SHORT).show();
        }
    }

}
