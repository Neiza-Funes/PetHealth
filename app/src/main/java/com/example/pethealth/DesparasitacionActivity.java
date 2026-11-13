package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DesparasitacionActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerDesparasitacion;
    private DesparasitacionAdapter desparasitacionAdapter;
    private List<Control> listaDesparasitaciones; // O la entidad correspondiente a desparasitación

    // ExecutorService para consultas asíncronas en segundo plano
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desparasitacion);

        databaseHelper = new DatabaseHelper(this);

        recyclerDesparasitacion = findViewById(R.id.recyclerDesparasitacion);
        Button btnRegistrar = findViewById(R.id.btnRegistrarDesparasitacion);

        recyclerDesparasitacion.setLayoutManager(new LinearLayoutManager(this));
        listaDesparasitaciones = new ArrayList<>();

        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(DesparasitacionActivity.this, RegistrarDesparasitacionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDesparasitacionesAsincrono();
    }

    private void cargarDesparasitacionesAsincrono() {
        executorService.execute(() -> {
            // la consulta a base de datos en segundo plano
            // List<Control> desparasitaciones = databaseHelper.obtenerDesparasitaciones();

            mainHandler.post(() -> {
                // listaDesparasitaciones.clear();
                // listaDesparasitaciones.addAll(desparasitaciones);
                // desparasitacionAdapter.notifyDataSetChanged();
            });
        });
    }
}