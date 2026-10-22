package com.ipvg.georegistrov2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateRecord extends AppCompatActivity {

    private EditText Titulo;
    private EditText Descripcion;
    private Spinner spinnerCategoria;
    private EditText FechaHora;
    private EditText Latitud;
    private EditText Longitud;
    private Button btnGuardar;
    private Button btnVolver;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_record);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Titulo = findViewById(R.id.etTitulo);
        Descripcion = findViewById(R.id.etDescripcion);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        FechaHora = findViewById(R.id.etFechaHora);
        Latitud = findViewById(R.id.etLatitud);
        Longitud = findViewById(R.id.etLongitud);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);
        progressBar = findViewById(R.id.progressBar5);

        // Llenar automáticamente la Fecha y Hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        FechaHora.setText(sdf.format(new Date()));

        String[] categorias = {"Seleccionar categoría", "Geología", "Medio Ambiente", "Infraestructura", "Muestra de Campo", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adapter);

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (ValidarCampos()) {
                progressBar.setVisibility(View.VISIBLE);
                // Guardar los datos en FireStore
                Map<String, Object> registro = new HashMap<>();

                registro.put("titulo", Titulo.getText().toString().trim());
                registro.put("descripcion", Descripcion.getText().toString().trim());
                registro.put("categoria", spinnerCategoria.getSelectedItem().toString());
                registro.put("fechaHora", FechaHora.getText().toString().trim());
                registro.put("latitud", Double.parseDouble(Latitud.getText().toString().trim()));
                registro.put("longitud", Double.parseDouble(Longitud.getText().toString().trim()));

                db.collection("registros")
                                .add(registro)
                                .addOnSuccessListener(documentReference -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(CreateRecord.this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(f -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(CreateRecord.this, "Error al guardar en FireStore" + f.getMessage(), Toast.LENGTH_SHORT).show();
                                });

            } else {
                Toast.makeText(CreateRecord.this, "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            }
        });

        // PERMISOS DE GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Permiso preciso concedido
                        obtenerUbicacionActual();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Permiso aproximado concedido
                        obtenerUbicacionActual();
                    } else {
                        // Permiso denegado por el usuario
                        Toast.makeText(this, "Permiso de ubicación denegado. No se pueden registrar coordenadas.", Toast.LENGTH_LONG).show();
                    }
                }
                );
        // Verificar los permisos solo cuando se abra el formulario
        verificarYPedirPermisos();
        }
    private void verificarYPedirPermisos() {
        // Verificar si los permisos ya fueron concedidos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }
    private void obtenerUbicacionActual() {
        // Primero se comprueba si se el permiso existe para acceder a la ubicacion del usuario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitud = location.getLatitude();
                        double longitud = location.getLongitude();

                        // Mostrar coordenadas en los campos de texto
                        Latitud.setText(String.valueOf(latitud));
                        Longitud.setText(String.valueOf(longitud));

                        Toast.makeText(this, "Ubicación obtenida con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "El GPS está desactivado, activelo vuelva a abrir la ventana de registro.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(this, "Error al obtener ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    // Validaciones basicas para cada campo
    private boolean ValidarCampos() {
        if (Titulo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe incluir un título en el registro", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Descripcion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe incluir una descripción", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (FechaHora.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe incluir la fecha y hora", Toast.LENGTH_SHORT).show();
            return false;
        }
        String fechaIngresada = FechaHora.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date fechaValida = sdf.parse(fechaIngresada);
        } catch (ParseException ex){
            Toast.makeText(this, "La fecha debe tener el formato: dd/mm/yyyy HH:mm", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Latitud.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe incluir la latitud", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Longitud.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe incluir la longitud", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validar el rango de valores de las coordenadas
        try {
            double lat = Double.parseDouble(Latitud.getText().toString());
            double lon = Double.parseDouble(Longitud.getText().toString());

            if (lat < -90.0 || lat > 90.0) {
                Toast.makeText(this, "La latitud debe estar entre -90 y 90", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (lon < -180.0 || lon > 180.0) {
                Toast.makeText(this, "La longitud debe estar entre -180 y 180", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Las coordenadas deben ser números válidos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}