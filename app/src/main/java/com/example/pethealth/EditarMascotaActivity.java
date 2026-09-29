package com.example.pethealth;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditarMascotaActivity extends Activity {

    public static final String EXTRA_MASCOTA_ID = "mascotaId";

    private EditText edtNombre;
    private EditText edtEdad;
    private EditText edtRaza;
    private Spinner spinnerEspecie;
    private DatabaseHelper databaseHelper;
    private long mascotaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_mascota);

        databaseHelper = new DatabaseHelper(this);
        mascotaId = getIntent().getLongExtra(EXTRA_MASCOTA_ID, -1);

        Button btnVolver = findViewById(R.id.btnVolverEditarMascota);
        edtNombre = findViewById(R.id.edtNombreMascotaEditar);
        edtEdad = findViewById(R.id.edtEdadMascotaEditar);
        edtRaza = findViewById(R.id.edtRazaMascotaEditar);
        spinnerEspecie = findViewById(R.id.spinnerEspecieEditar);
        Button btnGuardar = findViewById(R.id.btnGuardarCambiosMascota);

        configurarSpinner();

        btnVolver.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarCambios());

        cargarMascota();
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.especies_mascota,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(adapter);
    }

    private void cargarMascota() {
        if (mascotaId == -1) {
            mostrarMascotaNoEncontrada();
            return;
        }

        Mascota mascota = databaseHelper.obtenerMascotaPorId(mascotaId);
        if (mascota == null) {
            mostrarMascotaNoEncontrada();
            return;
        }

        edtNombre.setText(mascota.getNombre());
        edtEdad.setText(String.valueOf(mascota.getEdad()));
        edtRaza.setText(mascota.getRaza());
        seleccionarEspecie(mascota.getEspecie());
    }

    private void seleccionarEspecie(String especie) {
        if (especie == null) {
            return;
        }

        for (int i = 0; i < spinnerEspecie.getCount(); i++) {
            Object item = spinnerEspecie.getItemAtPosition(i);
            if (item != null && especie.equalsIgnoreCase(item.toString())) {
                spinnerEspecie.setSelection(i);
                return;
            }
        }
    }

    private void guardarCambios() {
        String nombre = edtNombre.getText().toString().trim();
        String edadTexto = edtEdad.getText().toString().trim();
        String raza = edtRaza.getText().toString().trim();
        String especie = spinnerEspecie.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            edtNombre.setError(getString(R.string.error_nombre_mascota));
            edtNombre.requestFocus();
            return;
        }

        if (edadTexto.isEmpty()) {
            edtEdad.setError(getString(R.string.error_edad_mascota));
            edtEdad.requestFocus();
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException e) {
            edtEdad.setError(getString(R.string.error_edad_invalida));
            edtEdad.requestFocus();
            return;
        }

        if (edad < 0) {
            edtEdad.setError(getString(R.string.error_edad_invalida));
            edtEdad.requestFocus();
            return;
        }

        boolean actualizada = databaseHelper.actualizarMascota(
                mascotaId,
                nombre,
                especie,
                edad,
                raza
        );

        if (!actualizada) {
            Toast.makeText(this, R.string.toast_error_actualizar_mascota, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_mascota_actualizada, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void mostrarMascotaNoEncontrada() {
        Toast.makeText(this, R.string.toast_mascota_no_encontrada, Toast.LENGTH_SHORT).show();
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
