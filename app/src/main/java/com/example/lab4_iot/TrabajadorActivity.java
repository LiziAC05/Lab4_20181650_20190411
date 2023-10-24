package com.example.lab4_iot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab4_iot.databinding.ActivityDescargaBinding;
import com.example.lab4_iot.databinding.ActivityTrabajadorBinding;
import com.example.lab4_iot.services.EmployeeService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrabajadorActivity extends AppCompatActivity {

    ActivityTrabajadorBinding binding;
    EmployeeService service;

    int codigoTrabajador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrabajadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initRetrofit();
        Intent intent = getIntent();
        boolean tieneMeetingDate = intent.getBooleanExtra("tieneMeetingDate", false);
        codigoTrabajador = intent.getIntExtra("codigoTrabajador", -1);

        // Aquí es donde decides si mostrar u ocultar el botón de feedback
        if (tieneMeetingDate) {
            binding.btnFeedBack.setVisibility(View.VISIBLE);
            binding.btnFeedBack.setOnClickListener(v -> {
                showFeedbackDialog();
            });
        } else {
            binding.btnFeedBack.setVisibility(View.GONE);  // Ocultar el botón si no tiene reunión
        }

        // No tiene una fecha de reunión programada
        binding.btnDownloadSche.setOnClickListener(view -> {
            handleDownloadSchedule(tieneMeetingDate);
        });
    }


    private void showFeedbackDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.feedback_dialog, null);
        final EditText feedbackEditText = dialogView.findViewById(R.id.feedbackEditText);
        final TextView charCountTextView = dialogView.findViewById(R.id.wordCountTextView);

        // Escuchar cambios en el texto y actualizar el conteo de palabras
        feedbackEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int charCount = editable.length();
                if (charCount >= 250) { // Cambiado de > a >=
                    Toast.makeText(TrabajadorActivity.this, "Máximo 250 caracteres permitidos", Toast.LENGTH_SHORT).show();
                    feedbackEditText.setEnabled(false); // Deshabilitar EditText
                }
                charCountTextView.setText("Caracteres: " + charCount + "/250");
            }
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String feedback = feedbackEditText.getText().toString();
            handleFeedback(feedback);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> feedbackEditText.setEnabled(true)); // Habilitar de nuevo el EditText al cancelar
        builder.show();
    }

    private void handleFeedback(String feedback) {
        if (codigoTrabajador == -1) {
            Toast.makeText(this, "Error al obtener el código del trabajador", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("codigoTrabajador", String.valueOf(codigoTrabajador));
        Log.d("feddback",feedback);
        Call<ResponseBody> call = service.updateEmployeeFeedback(codigoTrabajador, feedback);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Toast.makeText(TrabajadorActivity.this, responseBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(TrabajadorActivity.this, "Error al enviar feedback", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TrabajadorActivity.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
                .client(httpClient.build())  // Agrega el cliente personalizado a Retrofit
                .build();
        service = retrofit.create(EmployeeService.class);
    }




    private void handleDownloadSchedule(boolean tieneMeetingDate) {
        if (!tieneMeetingDate) {
            Toast.makeText(this, "No cuenta con tutorías pendientes", Toast.LENGTH_SHORT).show();
        } else {
            // Lógica para iniciar la descarga
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://i.pinimg.com/564x/4e/8e/a5/4e8ea537c896aa277e6449bdca6c45da.jpg");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);

            Toast.makeText(this, "Descargando...", Toast.LENGTH_SHORT).show();
        }
    }
}