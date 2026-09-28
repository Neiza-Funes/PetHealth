package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistrarMascotaActivity extends Activity {

    private EditText edtNombreMascota;
    private EditText edtEdadMascota;
    private EditText edtRazaMascota;
    private Spinner spinnerEspecie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_mascota);

        edtNombreMascota = findViewById(R.id.edtNombreMascota);
        edtEdadMascota = findViewById(R.id.edtEdadMascota);
        edtRazaMascota = findViewById(R.id.edtRazaMascota);
        spinnerEspecie = findViewById(R.id.spinnerEspecie);
        Button btnGuardarMascota = findViewById(R.id.btnGuardarMascota);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.especies_mascota,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(adapter);

        btnGuardarMascota.setOnClickListener(v -> validarYGuardar());
    }

    private void validarYGuardar() {
        String nombre = edtNombreMascota.getText().toString().trim();
        String edadTexto = edtEdadMascota.getText().toString().trim();
        String raza = edtRazaMascota.getText().toString().trim();
        String especie = spinnerEspecie.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            edtNombreMascota.setError(getString(R.string.error_nombre_mascota));
            edtNombreMascota.requestFocus();
            return;
        }

        if (edadTexto.isEmpty()) {
            edtEdadMascota.setError(getString(R.string.error_edad_mascota));
            edtEdadMascota.requestFocus();
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException e) {
            edtEdadMascota.setError(getString(R.string.error_edad_invalida));
            edtEdadMascota.requestFocus();
            return;
        }

        if (edad < 0) {
            edtEdadMascota.setError(getString(R.string.error_edad_invalida));
            edtEdadMascota.requestFocus();
            return;
        }

        Toast.makeText(this, R.string.toast_mascota_registrada, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegistrarMascotaActivity.this, MisMascotasActivity.class);
        intent.putExtra("nombreMascota", nombre);
        intent.putExtra("especieMascota", especie);
        intent.putExtra("edadMascota", String.valueOf(edad));
        intent.putExtra("razaMascota", raza);
        startActivity(intent);
        finish();
    }
}
