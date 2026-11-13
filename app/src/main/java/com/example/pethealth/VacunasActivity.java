package com.example.pethealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacunasActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout contenedorVacunas;
    private TextView txtVacunasVacioLista;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas);

        databaseHelper = new DatabaseHelper(this);
        contenedorVacunas = findViewById(R.id.contenedorVacunas);
        txtVacunasVacioLista = findViewById(R.id.txtVacunasVacioLista);

        Button btnVolver = findViewById(R.id.btnVolverInicioVacunas);
        Button btnRegistrarVacuna = findViewById(R.id.btnRegistrarVacuna);

        btnVolver.setOnClickListener(v -> finish());
        btnRegistrarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(VacunasActivity.this, RegistrarVacunaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVacunasAsincrono();
    }

    private void cargarVacunasAsincrono() {
        executorService.execute(() -> {
            List<Vacuna> vacunas = databaseHelper.obtenerVacunas();
            List<VacunaVista> vacunasVista = new ArrayList<>();

            for (Vacuna vacuna : vacunas) {
                Mascota mascota = databaseHelper.obtenerMascotaPorId(vacuna.getMascotaId());
                if (mascota != null) {
                    vacunasVista.add(new VacunaVista(vacuna, mascota.getNombre()));
                }
            }

            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                mostrarVacunas(vacunasVista);
            });
        });
    }

    private void mostrarVacunas(List<VacunaVista> vacunasVista) {
        contenedorVacunas.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (VacunaVista vacunaVista : vacunasVista) {
            Vacuna vacuna = vacunaVista.vacuna;
            View tarjeta = inflater.inflate(R.layout.item_vacuna, contenedorVacunas, false);
            TextView txtMascota = tarjeta.findViewById(R.id.txtItemVacunaMascota);
            TextView txtNombre = tarjeta.findViewById(R.id.txtItemVacunaNombre);
            TextView txtFecha = tarjeta.findViewById(R.id.txtItemVacunaFecha);
            TextView txtEstado = tarjeta.findViewById(R.id.txtItemVacunaEstado);
            Button btnVerMascota = tarjeta.findViewById(R.id.btnItemVacunaVerMascota);
            Button btnEditar = tarjeta.findViewById(R.id.btnItemVacunaEditar);
            Button btnEliminar = tarjeta.findViewById(R.id.btnItemVacunaEliminar);

            txtMascota.setText(vacunaVista.mascotaNombre);
            txtNombre.setText(getString(R.string.formato_vacuna, vacuna.getNombre()));
            txtFecha.setText(getString(R.string.formato_fecha_vacuna, vacuna.getFecha()));
            txtEstado.setText(getString(R.string.formato_estado_vacuna, vacuna.getEstado()));

            btnVerMascota.setOnClickListener(v -> abrirDetalleMascota(vacuna.getMascotaId()));
            btnEditar.setOnClickListener(v -> abrirEdicionVacuna(vacuna.getId()));
            btnEliminar.setOnClickListener(v -> confirmarEliminacionVacuna(vacuna));

            contenedorVacunas.addView(tarjeta);
        }

        boolean vacio = vacunasVista.isEmpty();
        txtVacunasVacioLista.setVisibility(vacio ? View.VISIBLE : View.GONE);
        contenedorVacunas.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    private void abrirDetalleMascota(long mascotaId) {
        Intent intent = new Intent(VacunasActivity.this, DetalleMascotaActivity.class);
        intent.putExtra(DetalleMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    private void abrirEdicionVacuna(long vacunaId) {
        Intent intent = new Intent(VacunasActivity.this, EditarVacunaActivity.class);
        intent.putExtra(EditarVacunaActivity.EXTRA_VACUNA_ID, vacunaId);
        startActivity(intent);
    }

    private void confirmarEliminacionVacuna(Vacuna vacuna) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_vacuna)
                .setMessage(R.string.confirmar_eliminar_vacuna)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (dialog, which) -> eliminarVacuna(vacuna.getId()))
                .show();
    }

    private void eliminarVacuna(long vacunaId) {
        executorService.execute(() -> {
            boolean eliminada = databaseHelper.eliminarVacuna(vacunaId);
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (!eliminada) {
                    Toast.makeText(this, R.string.toast_error_eliminar_vacuna, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, R.string.toast_vacuna_eliminada, Toast.LENGTH_SHORT).show();
                cargarVacunasAsincrono();
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

    private static class VacunaVista {
        private final Vacuna vacuna;
        private final String mascotaNombre;

        private VacunaVista(Vacuna vacuna, String mascotaNombre) {
            this.vacuna = vacuna;
            this.mascotaNombre = mascotaNombre;
        }
    }
}
