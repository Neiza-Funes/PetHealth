package com.example.pethealth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditarControlActivity extends Activity {

    public static final String EXTRA_CONTROL_ID = "controlId";

    private Spinner spinnerMascota;
    private EditText edtTipo;
    private EditText edtFecha;
    private EditText edtEstado;
    private EditText edtObservacion;

    private DatabaseHelper databaseHelper;
    private List<Mascota> mascotas = new ArrayList<>();
    private long controlId = -1;
    private Control controlActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_control);

        databaseHelper = new DatabaseHelper(this);
        controlId = getIntent().getLongExtra(EXTRA_CONTROL_ID, -1);

        spinnerMascota = findViewById(R.id.spinnerEditarMascotaControl);
        edtTipo = findViewById(R.id.edtEditarTipoControl);
        edtFecha = findViewById(R.id.edtEditarFechaControl);
        edtEstado = findViewById(R.id.edtEditarEstadoControl);
        edtObservacion = findViewById(R.id.edtEditarObservacionControl);
        Button btnGuardar = findViewById(R.id.btnGuardarCambiosControl);
        Button btnVolver = findViewById(R.id.btnVolverEditarControl);

        btnVolver.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarCambios());

        cargarControl();
    }

    private void cargarControl() {
        if (controlId == -1) {
            mostrarErrorYSalir();
            return;
        }

        controlActual = databaseHelper.obtenerControlPorId(controlId);
        if (controlActual == null) {
            mostrarErrorYSalir();
            return;
        }

        mascotas = databaseHelper.obtenerMascotas();
        List<String> nombres = new ArrayList<>();
        int seleccion = -1;

        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
            nombres.add(mascota.getNombre());
            if (mascota.getId() == controlActual.getMascotaId()) {
                seleccion = i;
            }
        }

        if (mascotas.isEmpty() || seleccion == -1) {
            mostrarErrorYSalir();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMascota.setAdapter(adapter);
        spinnerMascota.setSelection(seleccion);

        edtTipo.setText(controlActual.getTipo());
        edtFecha.setText(controlActual.getFecha());
        edtEstado.setText(controlActual.getEstado());
        edtObservacion.setText(controlActual.getObservacion());
    }

    private void guardarCambios() {
        if (controlActual == null || mascotas.isEmpty()) {
            mostrarErrorYSalir();
            return;
        }

        int posicion = spinnerMascota.getSelectedItemPosition();
        if (posicion < 0 || posicion >= mascotas.size()) {
            Toast.makeText(this, R.string.error_seleccionar_mascota_control, Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = edtTipo.getText().toString().trim();
        String fecha = edtFecha.getText().toString().trim();
        String estado = edtEstado.getText().toString().trim();
        String observacion = edtObservacion.getText().toString().trim();

        if (tipo.isEmpty()) {
            edtTipo.setError(getString(R.string.error_tipo_control));
            edtTipo.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            edtFecha.setError(getString(R.string.error_fecha_control));
            edtFecha.requestFocus();
            return;
        }

        if (!fechaValida(fecha)) {
            edtFecha.setError(getString(R.string.error_fecha_invalida));
            edtFecha.requestFocus();
            return;
        }

        if (estado.isEmpty()) {
            edtEstado.setError(getString(R.string.error_estado_control));
            edtEstado.requestFocus();
            return;
        }

        Mascota mascota = mascotas.get(posicion);
        boolean actualizado = databaseHelper.actualizarControl(
                controlId,
                mascota.getId(),
                tipo,
                fecha,
                estado,
                observacion
        );

        if (!actualizado) {
            Toast.makeText(this, R.string.toast_error_actualizar_control, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_control_actualizado, Toast.LENGTH_SHORT).show();
        finish();
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

    private void mostrarErrorYSalir() {
        Toast.makeText(this, R.string.toast_control_no_encontrado, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
