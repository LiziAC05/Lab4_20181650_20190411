package com.example.lab4_iot;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.number.IntegerWidth;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityMainBinding;
import com.example.lab4_iot.entity.Employee;
import com.example.lab4_iot.services.EmployeeService;
import com.example.lab4_iot.services.FetchEmployeeCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String canalTutor = "tutor";
    String canalTrabajador = "trabajador";
    EmployeeService service;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        crearCanalTrabajador();
        initRetrofit();
        binding.btnTrabajador.setOnClickListener(view -> {
            // Crea el AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Ingrese su código de trabajador");

            // Configura el input
            final EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER); // Para ingresar sólo números
            builder.setView(input);

            // Configura los botones
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                String codigoStr = input.getText().toString();
                if (!codigoStr.isEmpty()) {
                    // Si se ingresó un código, realiza la consulta a la API
                    int codigo = Integer.parseInt(codigoStr);
                    fetchEmployeeById(codigo, new FetchEmployeeCallback() {
                        @Override
                        public void onEmployeeFound(Employee employee) {
                            // El empleado se encontró, muestra la notificación
                            notificarImportanceHighTrabajador();

                            // Navega a la siguiente actividad
                            Intent intent = new Intent(MainActivity.this, TrabajadorActivity.class);
                            intent.putExtra("codigoTrabajador", codigo); // Pasar el código como extra

                            boolean tieneMeetingDate = (employee.getMeeting_date() != null);
                            intent.putExtra("tieneMeetingDate", tieneMeetingDate);  // Pasar el estado de la reunión como extra

                            startActivity(intent);
                        }

                        @Override
                        public void onEmployeeNotFound() {
                            // El empleado no se encontró, muestra un mensaje
                            Toast.makeText(MainActivity.this, "Empleado no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Si no se ingresó un código, muestra un mensaje
                    Toast.makeText(MainActivity.this, "Por favor, ingrese un código válido", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            builder.show(); // Muestra el AlertDialog
        });


        crearCanalTutor();
        binding.btnTutor.setOnClickListener(view -> {
            notificarImportanceHighTutor();
            Intent intent = new Intent(MainActivity.this, TutorActivity.class);
            startActivity(intent);
        });




    }


    private void fetchEmployeeById(int employeeId, FetchEmployeeCallback callback) {
        Call<List<Employee>> call = service.buscarEmployees(employeeId);
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful()) {
                    List<Employee> employees = response.body();
                    if (employees != null && !employees.isEmpty()) {
                        Employee employee = employees.get(0);  // Tomamos el primer empleado

                        // Verifica si tiene un meeting_date
                        if (employee.getMeeting_date() != null) {
                            // Mostrar la notificación del meeting_date
                            notifyMeetingDate(employee.getMeeting_date());
                        }


                        callback.onEmployeeFound(employee);
                    } else {
                        callback.onEmployeeNotFound();
                    }
                } else {
                    callback.onEmployeeNotFound();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                callback.onEmployeeNotFound();
            }
        });
    }






    public void notifyMeetingDate(Timestamp meetingDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = dateFormat.format(meetingDate);

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalTrabajador)
                .setSmallIcon(R.drawable.tutor)
                .setContentTitle("Tienes una reunión programada")
                .setContentText("Tu próxima reunión es el " + strDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Notification notification = builder.build();

        // Lanzar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(2, notification);  // Utiliza un id diferente, por ejemplo, 2
        }
    }




    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigIp.BASE_URL )
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EmployeeService.class);
    }







    public void crearCanalTutor() {

        NotificationChannel channel = new NotificationChannel(canalTutor,
                "Canal del Tutor",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Canal para notificaciones con prioridad alta -- Tutor");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }
    public void crearCanalTrabajador() {

        NotificationChannel channel = new NotificationChannel(canalTrabajador,
                "Canal del Trabajador",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Canal para notificaciones con prioridad alta -- Trabajador");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }
    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }

    public void notificarImportanceHighTutor(){

        //Crear notificación
        Intent intent = new Intent(this, TutorActivity.class);
        intent.putExtra("pid",4616);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalTutor)
                .setSmallIcon(R.drawable.tutor)
                .setContentText("Está entrando en modo Tutor")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        //Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, notification);
        }

    }

    public void notificarImportanceHighTrabajador(){

        //Crear notificación
        Intent intent = new Intent(this, TrabajadorActivity.class);
        intent.putExtra("pid",4616);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalTrabajador)
                .setSmallIcon(R.drawable.tutor)
                .setContentText("Está entrando en modo Empleado")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        //Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, notification);
        }

    }
}