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

public class RegistrarControlActivity extends Activity {

    private Spinner spinnerMascotaControl;
    private EditText edtTipoControl;
    private EditText edtFechaControl;
    private EditText edtObservacionControl;
    private Button btnGuardarControl;

    private DatabaseHelper databaseHelper;
    private List<Mascota> mascotas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_control);

        databaseHelper = new DatabaseHelper(this);

        spinnerMascotaControl = findViewById(R.id.spinnerMascotaControl);
        edtTipoControl = findViewById(R.id.edtTipoControl);
        edtFechaControl = findViewById(R.id.edtFechaControl);
        edtObservacionControl = findViewById(R.id.edtObservacionControl);
        btnGuardarControl = findViewById(R.id.btnGuardarControl);

        cargarMascotas();
        btnGuardarControl.setOnClickListener(v -> validarYGuardarControl());
    }

    private void cargarMascotas() {
        mascotas = databaseHelper.obtenerMascotas();
        List<String> nombres = new ArrayList<>();

        for (Mascota mascota : mascotas) {
            nombres.add(mascota.getNombre());
        }

        if (nombres.isEmpty()) {
            nombres.add(getString(R.string.sin_mascotas_para_control));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMascotaControl.setAdapter(adapter);

        boolean hayMascotas = !mascotas.isEmpty();
        spinnerMascotaControl.setEnabled(hayMascotas);
        btnGuardarControl.setEnabled(hayMascotas);
    }

    private void validarYGuardarControl() {
        if (mascotas.isEmpty()) {
            Toast.makeText(this, R.string.sin_mascotas_para_control, Toast.LENGTH_SHORT).show();
            return;
        }

        int posicion = spinnerMascotaControl.getSelectedItemPosition();
        if (posicion < 0 || posicion >= mascotas.size()) {
            Toast.makeText(this, R.string.error_seleccionar_mascota_control, Toast.LENGTH_SHORT).show();
            return;
        }

        Mascota mascota = mascotas.get(posicion);
        String tipo = edtTipoControl.getText().toString().trim();
        String fecha = edtFechaControl.getText().toString().trim();
        String observacion = edtObservacionControl.getText().toString().trim();

        if (tipo.isEmpty()) {
            edtTipoControl.setError(getString(R.string.error_tipo_control));
            edtTipoControl.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            edtFechaControl.setError(getString(R.string.error_fecha_control));
            edtFechaControl.requestFocus();
            return;
        }

        if (!fechaValida(fecha)) {
            edtFechaControl.setError(getString(R.string.error_fecha_invalida));
            edtFechaControl.requestFocus();
            return;
        }

        Control control = new Control(
                mascota.getId(),
                tipo,
                fecha,
                "Pendiente",
                observacion
        );

        long id = databaseHelper.insertarControl(control);
        if (id == -1) {
            Toast.makeText(this, R.string.toast_error_registrar_control, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_control_registrado, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
