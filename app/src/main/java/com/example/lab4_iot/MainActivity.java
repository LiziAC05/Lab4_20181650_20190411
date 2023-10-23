package com.example.lab4_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.number.IntegerWidth;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    String canalTutor = "tutor";
    String canalTrabajador = "trabajador";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button worker = findViewById(R.id.btnTrabajador);
        crearCanalTrabajador();
        worker.setOnClickListener(view -> {
            notificarImportanceHighTrabajador();
            Intent intent = new Intent(MainActivity.this, TrabajadorActivity.class);
            startActivity(intent);
        });
        crearCanalTutor();
        Button tutor = findViewById(R.id.btnTutor);
        tutor.setOnClickListener(view -> {
            notificarImportanceHighTutor();
            Intent intent = new Intent(MainActivity.this, TutorActivity.class);
            startActivity(intent);
        });
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