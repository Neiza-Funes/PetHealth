package com.example.pethealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

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
    private final List<Desparasitacion> listaDesparasitaciones = new ArrayList<>();

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
        desparasitacionAdapter = new DesparasitacionAdapter(
                listaDesparasitaciones,
                new DesparasitacionAdapter.OnDesparasitacionListener() {
                    @Override
                    public void onVerMascota(long mascotaId) {
                        abrirDetalleMascota(mascotaId);
                    }

                    @Override
                    public void onEliminar(Desparasitacion desparasitacion) {
                        confirmarEliminacion(desparasitacion);
                    }
                },
                this
        );
        recyclerDesparasitacion.setAdapter(desparasitacionAdapter);

        btnRegistrar.setOnClickListener(v ->
                startActivity(new Intent(DesparasitacionActivity.this, RegistrarDesparasitacionActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDesparasitacionesAsincrono();
    }

    private void cargarDesparasitacionesAsincrono() {
        executorService.execute(() -> {
            List<Desparasitacion> desparasitaciones = databaseHelper.obtenerDesparasitaciones();

            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                listaDesparasitaciones.clear();
                listaDesparasitaciones.addAll(desparasitaciones);
                desparasitacionAdapter.notifyDataSetChanged();
            });
        });
    }

    private void abrirDetalleMascota(long mascotaId) {
        Intent intent = new Intent(DesparasitacionActivity.this, DetalleMascotaActivity.class);
        intent.putExtra(DetalleMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    private void confirmarEliminacion(Desparasitacion desparasitacion) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_desparasitacion)
                .setMessage(R.string.confirmar_eliminar_desparasitacion)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (dialog, which) ->
                        eliminarDesparasitacion(desparasitacion.getId()))
                .show();
    }

    private void eliminarDesparasitacion(long id) {
        executorService.execute(() -> {
            boolean eliminada = databaseHelper.eliminarDesparasitacion(id);

            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (!eliminada) {
                    Toast.makeText(this, R.string.toast_error_eliminar_desparasitacion, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, R.string.toast_desparasitacion_eliminada, Toast.LENGTH_SHORT).show();
                cargarDesparasitacionesAsincrono();
            });
        });
    }

    @Override
    protected void onDestroy() {
        executorService.shutdown();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
