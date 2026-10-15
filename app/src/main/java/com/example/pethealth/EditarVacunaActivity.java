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

public class EditarVacunaActivity extends Activity {

    public static final String EXTRA_VACUNA_ID = "vacunaId";

    private Spinner spinnerMascota;
    private EditText edtNombre;
    private EditText edtFecha;
    private Spinner spinnerEstado;

    private DatabaseHelper databaseHelper;
    private List<Mascota> mascotas = new ArrayList<>();
    private long vacunaId = -1;
    private Vacuna vacunaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_vacuna);

        databaseHelper = new DatabaseHelper(this);
        vacunaId = getIntent().getLongExtra(EXTRA_VACUNA_ID, -1);

        spinnerMascota = findViewById(R.id.spinnerEditarMascotaVacuna);
        edtNombre = findViewById(R.id.edtEditarNombreVacuna);
        edtFecha = findViewById(R.id.edtEditarFechaVacuna);
        spinnerEstado = findViewById(R.id.spinnerEditarEstadoVacuna);
        Button btnGuardar = findViewById(R.id.btnGuardarCambiosVacuna);
        Button btnVolver = findViewById(R.id.btnVolverEditarVacuna);

        btnVolver.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarCambios());

        cargarVacuna();
    }

    private void cargarVacuna() {
        if (vacunaId == -1) {
            mostrarErrorYSalir();
            return;
        }

        vacunaActual = databaseHelper.obtenerVacunaPorId(vacunaId);
        if (vacunaActual == null) {
            mostrarErrorYSalir();
            return;
        }

        cargarMascotas(vacunaActual.getMascotaId());
        cargarEstados(vacunaActual.getEstado());
        edtNombre.setText(vacunaActual.getNombre());
        edtFecha.setText(vacunaActual.getFecha());
    }

    private void cargarMascotas(long mascotaIdActual) {
        mascotas = databaseHelper.obtenerMascotas();
        List<String> nombres = new ArrayList<>();
        int seleccion = -1;

        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
            nombres.add(mascota.getNombre());
            if (mascota.getId() == mascotaIdActual) {
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
    }

    private void cargarEstados(String estadoActual) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.estados_vacuna,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        for (int i = 0; i < adapter.getCount(); i++) {
            CharSequence item = adapter.getItem(i);
            if (item != null && item.toString().equalsIgnoreCase(estadoActual)) {
                spinnerEstado.setSelection(i);
                break;
            }
        }
    }

    private void guardarCambios() {
        if (vacunaActual == null || mascotas.isEmpty()) {
            mostrarErrorYSalir();
            return;
        }

        int posicion = spinnerMascota.getSelectedItemPosition();
        if (posicion < 0 || posicion >= mascotas.size()) {
            Toast.makeText(this, R.string.error_seleccionar_mascota_vacuna, Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = edtNombre.getText().toString().trim();
        String fecha = edtFecha.getText().toString().trim();
        String estado = spinnerEstado.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            edtNombre.setError(getString(R.string.error_nombre_vacuna));
            edtNombre.requestFocus();
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

        Mascota mascota = mascotas.get(posicion);
        boolean actualizada = databaseHelper.actualizarVacuna(
                vacunaId,
                mascota.getId(),
                nombre,
                fecha,
                estado
        );

        if (!actualizada) {
            Toast.makeText(this, R.string.toast_error_actualizar_vacuna, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_vacuna_actualizada, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, R.string.toast_vacuna_no_encontrada, Toast.LENGTH_SHORT).show();
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
