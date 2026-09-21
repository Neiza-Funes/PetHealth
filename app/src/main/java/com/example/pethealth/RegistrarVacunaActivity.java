package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistrarVacunaActivity extends Activity {

    private EditText edtMascota;
    private EditText edtVacuna;
    private EditText edtFecha;
    private Spinner spEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_vacuna);

        edtMascota = findViewById(R.id.edtMascotaVacuna);
        edtVacuna = findViewById(R.id.edtNombreVacuna);
        edtFecha = findViewById(R.id.edtFechaVacuna);
        spEstado = findViewById(R.id.spEstadoVacuna);
        Button btnGuardarVacuna = findViewById(R.id.btnGuardarVacuna);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estados_vacuna,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapter);

        btnGuardarVacuna.setOnClickListener(v -> guardarVacuna());
    }

    private void guardarVacuna() {
        String mascota = edtMascota.getText().toString().trim();
        String vacuna = edtVacuna.getText().toString().trim();
        String fecha = edtFecha.getText().toString().trim();
        String estado = spEstado.getSelectedItem().toString();

        if (mascota.isEmpty()) {
            edtMascota.setError(getString(R.string.error_mascota_vacuna));
            edtMascota.requestFocus();
            return;
        }

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

        Toast.makeText(this, R.string.toast_vacuna_registrada, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegistrarVacunaActivity.this, VacunasActivity.class);
        intent.putExtra("mascota", mascota);
        intent.putExtra("vacuna", vacuna);
        intent.putExtra("fecha", fecha);
        intent.putExtra("estado", estado);
        startActivity(intent);
        finish();
    }
}
