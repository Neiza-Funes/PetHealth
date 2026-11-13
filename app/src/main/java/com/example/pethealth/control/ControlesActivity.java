package com.example.pethealth.control;

import com.example.pethealth.R;
import com.example.pethealth.adapter.ControlAdapter;
import com.example.pethealth.database.DatabaseHelper;
import com.example.pethealth.mascota.DetalleMascotaActivity;
import com.example.pethealth.model.Control;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlesActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerControles;
    private TextView txtSinProximosControles;
    private ControlAdapter controlAdapter;
    private final List<Control> listaControles = new ArrayList<>();

    // ExecutorService para consultas asíncronas a la base de datos (Exigencia del profesor)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);

        databaseHelper = new DatabaseHelper(this);
        recyclerControles = findViewById(R.id.recyclerControles);
        txtSinProximosControles = findViewById(R.id.txtSinProximosControles);

        // Configurar RecyclerView
        recyclerControles.setLayoutManager(new LinearLayoutManager(this));

        controlAdapter = new ControlAdapter(listaControles, new ControlAdapter.OnControlListener() {
            @Override
            public void onVerMascota(long mascotaId) {
                abrirDetalleMascota(mascotaId);
            }

            @Override
            public void onEditar(long controlId) {
                abrirEdicionControl(controlId);
            }

            @Override
            public void onEliminar(Control control) {
                confirmarEliminacionControl(control);
            }
        }, databaseHelper, this);

        recyclerControles.setAdapter(controlAdapter);

        Button btnVolver = findViewById(R.id.btnVolverInicioControles);
        Button btnRegistrarControl = findViewById(R.id.btnRegistrarControl);

        btnVolver.setOnClickListener(v -> finish());
        btnRegistrarControl.setOnClickListener(v -> {
            Intent intent = new Intent(ControlesActivity.this, RegistrarControlActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarControlesAsincrono();
    }

    private void cargarControlesAsincrono() {
        // Ejecución en segundo plano para no congelar el hilo principal
        executorService.execute(() -> {
            List<Control> controles = databaseHelper.obtenerProximosControles();

            mainHandler.post(() -> {
                listaControles.clear();
                listaControles.addAll(controles);
                controlAdapter.notifyDataSetChanged();

                boolean vacio = controles.isEmpty();
                txtSinProximosControles.setVisibility(vacio ? View.VISIBLE : View.GONE);
                recyclerControles.setVisibility(vacio ? View.GONE : View.VISIBLE);
            });
        });
    }

    private void abrirDetalleMascota(long mascotaId) {
        Intent intent = new Intent(ControlesActivity.this, DetalleMascotaActivity.class);
        intent.putExtra(DetalleMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    private void abrirEdicionControl(long controlId) {
        Intent intent = new Intent(ControlesActivity.this, EditarControlActivity.class);
        intent.putExtra(EditarControlActivity.EXTRA_CONTROL_ID, controlId);
        startActivity(intent);
    }

    private void confirmarEliminacionControl(Control control) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_control)
                .setMessage(R.string.confirmar_eliminar_control)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (dialog, which) -> eliminarControl(control.getId()))
                .show();
    }

    private void eliminarControl(long controlId) {
        executorService.execute(() -> {
            boolean eliminado = databaseHelper.eliminarControl(controlId);

            mainHandler.post(() -> {
                if (!eliminado) {
                    Toast.makeText(this, R.string.toast_error_eliminar_control, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, R.string.toast_control_eliminado, Toast.LENGTH_SHORT).show();
                cargarControlesAsincrono();
            });
        });
    }

    @Override
    protected void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
