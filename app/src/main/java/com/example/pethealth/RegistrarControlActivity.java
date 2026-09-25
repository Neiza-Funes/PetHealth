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

public class RegistrarControlActivity extends Activity {

    private EditText edtMascotaControl;
    private EditText edtTipoControl;
    private EditText edtFechaControl;
    private EditText edtObservacionControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_control);

        edtMascotaControl = findViewById(R.id.edtMascotaControl);
        edtTipoControl = findViewById(R.id.edtTipoControl);
        edtFechaControl = findViewById(R.id.edtFechaControl);
        edtObservacionControl = findViewById(R.id.edtObservacionControl);
        Button btnGuardarControl = findViewById(R.id.btnGuardarControl);

        btnGuardarControl.setOnClickListener(v -> validarYGuardarControl());
    }

    private void validarYGuardarControl() {
        String mascota = edtMascotaControl.getText().toString().trim();
        String tipo = edtTipoControl.getText().toString().trim();
        String fecha = edtFechaControl.getText().toString().trim();
        String observacion = edtObservacionControl.getText().toString().trim();

        if (mascota.isEmpty()) {
            edtMascotaControl.setError(getString(R.string.error_mascota_control));
            edtMascotaControl.requestFocus();
            return;
        }

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

        Toast.makeText(this, R.string.toast_control_registrado, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegistrarControlActivity.this, ControlesActivity.class);
        intent.putExtra("mascotaControl", mascota);
        intent.putExtra("tipoControl", tipo);
        intent.putExtra("fechaControl", fecha);
        intent.putExtra("observacionControl", observacion);
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
