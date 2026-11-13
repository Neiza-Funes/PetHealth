package com.example.pethealth.desparasitacion;

import com.example.pethealth.R;
import com.example.pethealth.database.DatabaseHelper;
import com.example.pethealth.model.Desparasitacion;
import com.example.pethealth.model.Mascota;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistrarDesparasitacionActivity extends Activity {

    private EditText edtMascota;
    private EditText edtProducto;
    private EditText edtFecha;
    private EditText edtProximaFecha;
    private EditText edtObservacion;
    private Button btnGuardar;

    private DatabaseHelper databaseHelper;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_desparasitacion);

        databaseHelper = new DatabaseHelper(this);

        edtMascota = findViewById(R.id.edtMascotaDesparasitacion);
        edtProducto = findViewById(R.id.edtProductoDesparasitacion);
        edtFecha = findViewById(R.id.edtFechaDesparasitacion);
        edtProximaFecha = findViewById(R.id.edtProximaDesparasitacion);
        edtObservacion = findViewById(R.id.edtObservacionDesparasitacion);
        btnGuardar = findViewById(R.id.btnGuardarDesparasitacion);

        btnGuardar.setOnClickListener(v -> guardarDesparasitacion());
    }

    private void guardarDesparasitacion() {
        String mascotaNombre = edtMascota.getText().toString().trim();
        String producto = edtProducto.getText().toString().trim();
        String fecha = edtFecha.getText().toString().trim();
        String proximaFecha = edtProximaFecha.getText().toString().trim();
        String observacion = edtObservacion.getText().toString().trim();

        if (mascotaNombre.isEmpty()) {
            edtMascota.setError(getString(R.string.error_mascota_desparasitacion));
            edtMascota.requestFocus();
            return;
        }

        if (producto.isEmpty()) {
            edtProducto.setError(getString(R.string.error_producto_desparasitacion));
            edtProducto.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            edtFecha.setError(getString(R.string.error_fecha_desparasitacion));
            edtFecha.requestFocus();
            return;
        }

        if (!fechaValida(fecha)) {
            edtFecha.setError(getString(R.string.error_fecha_invalida));
            edtFecha.requestFocus();
            return;
        }

        if (proximaFecha.isEmpty()) {
            edtProximaFecha.setError(getString(R.string.error_proxima_desparasitacion));
            edtProximaFecha.requestFocus();
            return;
        }

        if (!fechaValida(proximaFecha)) {
            edtProximaFecha.setError(getString(R.string.error_fecha_invalida));
            edtProximaFecha.requestFocus();
            return;
        }

        btnGuardar.setEnabled(false);
        executorService.execute(() -> {
            Mascota mascota = buscarMascotaPorNombre(mascotaNombre);
            if (mascota == null) {
                mainHandler.post(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    btnGuardar.setEnabled(true);
                    edtMascota.setError(getString(R.string.toast_mascota_no_encontrada));
                    edtMascota.requestFocus();
                });
                return;
            }

            Desparasitacion desparasitacion = new Desparasitacion(
                    mascota.getId(),
                    producto,
                    fecha,
                    proximaFecha,
                    observacion
            );

            long id = databaseHelper.insertarDesparasitacion(desparasitacion);
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                btnGuardar.setEnabled(true);
                if (id == -1) {
                    Toast.makeText(this, R.string.toast_error_registrar_desparasitacion, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, R.string.toast_desparasitacion_registrada, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private Mascota buscarMascotaPorNombre(String nombreBuscado) {
        List<Mascota> mascotas = databaseHelper.obtenerMascotas();
        for (Mascota mascota : mascotas) {
            if (mascota.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return mascota;
            }
        }
        return null;
    }

    private boolean fechaValida(String fecha) {
        if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        formato.setLenient(false);
        try {
            formato.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
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
