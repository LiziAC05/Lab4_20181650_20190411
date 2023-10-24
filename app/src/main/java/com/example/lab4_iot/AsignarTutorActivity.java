package com.example.lab4_iot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityAsignarTutorBinding;
import com.example.lab4_iot.entity.Employee;
import com.example.lab4_iot.services.EmployeeService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AsignarTutorActivity extends AppCompatActivity {
    ActivityAsignarTutorBinding binding;
    EmployeeService service;

    String canalTrabajador = "tutor";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAsignarTutorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initRetrofit();

        binding.btnAsignarTutorV.setOnClickListener(v -> {
            int employeeId = Integer.parseInt(binding.edtCodigoEmpleado.getText().toString());
            int managerId = Integer.parseInt(binding.edtIDEmpleado.getText().toString());

            // Primero verificar si ambos IDs existen en la lista de empleados
            Call<List<Employee>> employeeCall = service.buscarEmployees(employeeId);
            Call<List<Employee>> managerCall = service.buscarEmployees(managerId);

            employeeCall.enqueue(new Callback<List<Employee>>() {
                @Override
                public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                    if (response.isSuccessful() && !response.body().isEmpty()) {
                        // Employee exists, now check for manager
                        managerCall.enqueue(new Callback<List<Employee>>() {
                            @Override
                            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                                if (response.isSuccessful() && !response.body().isEmpty()) {
                                    // Both IDs exist, proceed to check if employee is assigned to manager
                                    Call<List<Employee>> assignmentCall = service.getEmployeesByManager(managerId);
                                    assignmentCall.enqueue(new Callback<List<Employee>>() {
                                        @Override
                                        public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                                            boolean isEmployeeAssigned = false;
                                            for (Employee emp : response.body()) {
                                                if (emp.getId() == employeeId) {
                                                    isEmployeeAssigned = true;
                                                    break;
                                                }
                                            }

                                            if (isEmployeeAssigned) {
                                                // Update meeting status as both conditions are met
                                                Call<ResponseBody> updateCall = service.updateEmployeeMeetingStatus(employeeId, managerId);
                                                updateCall.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        sendNotification("Actualización Exitosa", "El estado de la reunión se ha actualizado");
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Toast.makeText(AsignarTutorActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                sendNotification("Asignación de Tutor", "El empleado no está asignado a este manager");

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<Employee>> call, Throwable t) {
                                            sendNotification("Red Error", "Se genero un error de red a la conexion");
                                        }
                                    });
                                } else {
                                    sendNotification("Manager Error", "No se econtro al Manager Id");
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Employee>> call, Throwable t) {
                                sendNotification("Error de red", "Ocurrio un error al conectarce al web service");
                            }
                        });
                    } else {
                        sendNotification("Error de ID", "No ingreso Id Válidos");
                    }
                }

                @Override
                public void onFailure(Call<List<Employee>> call, Throwable t) {
                    Toast.makeText(AsignarTutorActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void initRetrofit() {
        // Interceptor de Logging para visualizar las peticiones y respuestas
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)  // Configura un tiempo de espera de conexión
                .readTimeout(15, TimeUnit.SECONDS);    // Configura un tiempo de espera de lectura

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigIp.BASE_URL )
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        service = retrofit.create(EmployeeService.class);
    }


    public void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalTrabajador)
                .setSmallIcon(R.drawable.tutor) // Reemplaza esto con tu propio ícono
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());
    }

    public void crearCanalTrabajador() {
        NotificationChannel channel = new NotificationChannel(canalTrabajador,
                "Canal del Trabajador",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Canal para notificaciones con prioridad alta -- Trabajador");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}