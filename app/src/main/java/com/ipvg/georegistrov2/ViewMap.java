package com.ipvg.georegistrov2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_map);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        Button btnVolver = findViewById(R.id.btnVolverMap);
        btnVolver.setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        cargarMarcadoresDesdeFirestore();
    }

    private void cargarMarcadoresDesdeFirestore() {
        db.collection("registros")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LatLng ultimaUbicacion = null;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RecordEstructure registro = document.toObject(RecordEstructure.class);

                            LatLng punto = new LatLng(registro.getLatitud(), registro.getLongitud());
                            ultimaUbicacion = punto;

                            // Crear marcador
                            mMap.addMarker(new MarkerOptions()
                                    .position(punto)
                                    .title(registro.getTitulo())
                                    .snippet(registro.getCategoria() + " | " + registro.getFechaHora()));
                        }

                        if (ultimaUbicacion != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ultimaUbicacion, 12f));
                        } else {
                            Toast.makeText(this, "No hay registros guardados para mostrar en el mapa", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error al los cargar registros en el mapa", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}