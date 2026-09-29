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

public class RegistrarVacunaActivity extends Activity {

    private Spinner spinnerMascotaVacuna;
    private EditText edtVacuna;
    private EditText edtFecha;
    private Spinner spEstado;
    private Button btnGuardarVacuna;

    private DatabaseHelper databaseHelper;
    private List<Mascota> mascotas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_vacuna);

        databaseHelper = new DatabaseHelper(this);

        spinnerMascotaVacuna = findViewById(R.id.spinnerMascotaVacuna);
        edtVacuna = findViewById(R.id.edtNombreVacuna);
        edtFecha = findViewById(R.id.edtFechaVacuna);
        spEstado = findViewById(R.id.spEstadoVacuna);
        btnGuardarVacuna = findViewById(R.id.btnGuardarVacuna);

        cargarMascotas();
        cargarEstados();
        btnGuardarVacuna.setOnClickListener(v -> guardarVacuna());
    }

    private void cargarMascotas() {
        mascotas = databaseHelper.obtenerMascotas();
        List<String> nombres = new ArrayList<>();

        for (Mascota mascota : mascotas) {
            nombres.add(mascota.getNombre());
        }

        if (nombres.isEmpty()) {
            nombres.add(getString(R.string.sin_mascotas_para_vacuna));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMascotaVacuna.setAdapter(adapter);

        boolean hayMascotas = !mascotas.isEmpty();
        spinnerMascotaVacuna.setEnabled(hayMascotas);
        btnGuardarVacuna.setEnabled(hayMascotas);
    }

    private void cargarEstados() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estados_vacuna,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapter);
    }

    private void guardarVacuna() {
        if (mascotas.isEmpty()) {
            Toast.makeText(this, R.string.sin_mascotas_para_vacuna, Toast.LENGTH_SHORT).show();
            return;
        }

        int posicion = spinnerMascotaVacuna.getSelectedItemPosition();
        if (posicion < 0 || posicion >= mascotas.size()) {
            Toast.makeText(this, R.string.error_seleccionar_mascota_vacuna, Toast.LENGTH_SHORT).show();
            return;
        }

        Mascota mascota = mascotas.get(posicion);
        String vacuna = edtVacuna.getText().toString().trim();
        String fecha = edtFecha.getText().toString().trim();
        String estado = spEstado.getSelectedItem().toString();

        if (vacuna.isEmpty()) {
            edtVacuna.setError(getString(R.string.error_nombre_vacuna));
            edtVacuna.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            edtFecha.setError(getString(R.string.error_fecha_vacuna));
            edtFecha.requestFocus();
            return;
        }

        if (!fechaValida(fecha)) {
            edtFecha.setError(getString(R.string.error_fecha_invalida));
            edtFecha.requestFocus();
            return;
        }

        Vacuna nuevaVacuna = new Vacuna(
                mascota.getId(),
                vacuna,
                fecha,
                estado
        );

        long id = databaseHelper.insertarVacuna(nuevaVacuna);
        if (id == -1) {
            Toast.makeText(this, R.string.toast_error_registrar_vacuna, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_vacuna_registrada, Toast.LENGTH_SHORT).show();
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
