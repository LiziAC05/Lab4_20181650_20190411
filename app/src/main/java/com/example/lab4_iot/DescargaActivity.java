package com.example.lab4_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest.permission;


import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityDescargaBinding;
import com.example.lab4_iot.entity.Employee;
import com.example.lab4_iot.services.EmployeeService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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
                .baseUrl(AppConfigIp.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EmployeeService.class);
    }
    private void fetchEmployeesByManager(int managerId) {
        Call<List<Employee>> call = service.getEmployeesByManager(managerId);
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful()) {
                    List<Employee> employees = response.body();
                    saveToFile(employees);  // Save using MediaStore
                    saveToFile2(employees); // Save using File API
                    Toast.makeText(DescargaActivity.this, "Archivo descargado y guardado en ambas ubicaciones", Toast.LENGTH_SHORT).show();
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


    private void saveToFile(List<Employee> employees) {
        try {
            // Definir el nombre del archivo
            String filename = "empleados.txt";

            // Configurar los valores para guardar en el MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME , filename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");

            // Android Q y versiones superiores: Guarda en el directorio "Documents" del MediaStore
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

            // Obtener la URI del MediaStore para guardar el archivo
            Uri externalContentUri = MediaStore.Files.getContentUri("external");
            Uri fileUri = getContentResolver().insert(externalContentUri, values);

            if (fileUri != null) {
                // Usar un OutputStream para escribir en la URI
                OutputStream os = getContentResolver().openOutputStream(fileUri);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                for (Employee employee : employees) {
                    bw.write("ID: " + employee.id + ", Name: " + employee.first_name + " " + employee.last_name+" correo:"+employee.email );
                    bw.newLine();
                }

                // Cerrar el BufferedWriter y OutputStream
                bw.close();
                if (os != null) {
                    os.close();
                }

                Toast.makeText(this, "Archivo guardado en: " + fileUri.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFile2(List<Employee> employees) {
        try {
            // Definir el nombre del archivo
            String filename = "empleados.txt";

            // Obtener el directorio donde guardar el archivo
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(directory, filename);

            // Usar un BufferedWriter para escribir en el archivo
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Employee employee : employees) {
                bw.write("ID: " + employee.id + ", Name: " + employee.first_name + " " + employee.last_name);
                bw.newLine();
            }

            // Cerrar el BufferedWriter
            bw.close();

            Toast.makeText(this, "Archivo guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
        }
    }





}
