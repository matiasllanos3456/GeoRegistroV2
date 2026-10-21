package com.ipvg.georegistrov2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReadRecords extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private List<RecordEstructure> listaDeRegistros;
    private ProgressBar progressBar;
    private Button btnVolver;
    private Button btnActualizar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_records);

        recyclerView = findViewById(R.id.recyclerViewRegistros);
        progressBar = findViewById(R.id.progressBarRead);
        btnVolver = findViewById(R.id.btnVolverRead);
        btnActualizar = findViewById(R.id.btnActualizar);

        btnVolver.setOnClickListener(v -> finish());
        btnActualizar.setOnClickListener(v -> cargarDatos());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaDeRegistros = new ArrayList<>();
        adapter = new RecordAdapter(listaDeRegistros);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cargarDatos();
    }
    private void cargarDatos(){
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        db.collection("registros")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if(task.isSuccessful()) {
                        listaDeRegistros.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RecordEstructure registro = document.toObject(RecordEstructure.class);
                            listaDeRegistros.add(registro);
                        }
                        adapter.notifyDataSetChanged();
                        if(listaDeRegistros.isEmpty()){
                            Toast.makeText(ReadRecords.this, "No hay registros guardados aún", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ReadRecords.this, "Error al intentar cargar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}