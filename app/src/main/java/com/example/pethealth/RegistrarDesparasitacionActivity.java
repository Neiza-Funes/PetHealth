package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RegistrarDesparasitacionActivity extends Activity {

    private EditText edtMascota;
    private EditText edtProducto;
    private EditText edtFecha;
    private EditText edtProximaFecha;
    private EditText edtObservacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_desparasitacion);

        edtMascota = findViewById(R.id.edtMascotaDesparasitacion);
        edtProducto = findViewById(R.id.edtProductoDesparasitacion);
        edtFecha = findViewById(R.id.edtFechaDesparasitacion);
        edtProximaFecha = findViewById(R.id.edtProximaDesparasitacion);
        edtObservacion = findViewById(R.id.edtObservacionDesparasitacion);
        Button btnGuardar = findViewById(R.id.btnGuardarDesparasitacion);

        btnGuardar.setOnClickListener(v -> guardarDesparasitacion());
    }

    private void guardarDesparasitacion() {
        String mascota = edtMascota.getText().toString().trim();
        String producto = edtProducto.getText().toString().trim();
        String fecha = edtFecha.getText().toString().trim();
        String proximaFecha = edtProximaFecha.getText().toString().trim();
        String observacion = edtObservacion.getText().toString().trim();

        if (mascota.isEmpty()) {
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

        Toast.makeText(this, R.string.toast_desparasitacion_registrada, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegistrarDesparasitacionActivity.this, DesparasitacionActivity.class);
        intent.putExtra("mascotaDesparasitacion", mascota);
        intent.putExtra("productoDesparasitacion", producto);
        intent.putExtra("fechaDesparasitacion", fecha);
        intent.putExtra("proximaDesparasitacion", proximaFecha);
        intent.putExtra("observacionDesparasitacion", observacion);
        startActivity(intent);
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
}
