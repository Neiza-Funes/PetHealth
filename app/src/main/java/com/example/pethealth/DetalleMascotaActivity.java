package com.example.pethealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleMascotaActivity extends Activity {

    public static final String EXTRA_MASCOTA_ID = "mascotaId";

    private DatabaseHelper databaseHelper;
    private long mascotaId = -1;

    private TextView txtNombre;
    private TextView txtEspecie;
    private TextView txtEdad;
    private TextView txtRaza;

    private TextView txtHistorial1;
    private TextView txtHistorial2;
    private TextView txtHistorialVacio;

    private TextView txtControlTipo;
    private TextView txtControlFecha;
    private TextView txtControlEstado;
    private TextView txtControlesVacio;

    private TextView txtVacunaNombre;
    private TextView txtVacunaFecha;
    private TextView txtVacunaEstado;
    private TextView txtVacunasVacio;

    private Button btnEditar;
    private Button btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        databaseHelper = new DatabaseHelper(this);
        mascotaId = getIntent().getLongExtra(EXTRA_MASCOTA_ID, -1);

        Button btnVolver = findViewById(R.id.btnVolverMisMascotas);
        btnEditar = findViewById(R.id.btnEditarMascota);
        btnEliminar = findViewById(R.id.btnEliminarMascota);

        txtNombre = findViewById(R.id.txtDetalleNombre);
        txtEspecie = findViewById(R.id.txtDetalleEspecie);
        txtEdad = findViewById(R.id.txtDetalleEdad);
        txtRaza = findViewById(R.id.txtDetalleRaza);

        txtHistorial1 = findViewById(R.id.txtHistorial1);
        txtHistorial2 = findViewById(R.id.txtHistorial2);
        txtHistorialVacio = findViewById(R.id.txtHistorialVacio);

        txtControlTipo = findViewById(R.id.txtDetalleControlTipo);
        txtControlFecha = findViewById(R.id.txtDetalleControlFecha);
        txtControlEstado = findViewById(R.id.txtDetalleControlEstado);
        txtControlesVacio = findViewById(R.id.txtControlesVacio);

        txtVacunaNombre = findViewById(R.id.txtDetalleVacunaNombre);
        txtVacunaFecha = findViewById(R.id.txtDetalleVacunaFecha);
        txtVacunaEstado = findViewById(R.id.txtDetalleVacunaEstado);
        txtVacunasVacio = findViewById(R.id.txtVacunasVacio);

        btnVolver.setOnClickListener(v -> finish());
        btnEditar.setOnClickListener(v -> abrirEdicion());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());

        // Si se llega desde los módulos antiguos de controles o vacunas no existe ID SQLite.
        // En ese caso se mantiene la visualización anterior, pero editar/eliminar se oculta.
        boolean tieneIdSQLite = mascotaId != -1;
        btnEditar.setVisibility(tieneIdSQLite ? View.VISIBLE : View.GONE);
        btnEliminar.setVisibility(tieneIdSQLite ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDetalle();
    }

    private void cargarDetalle() {
        String nombre;
        String especie;
        String edad;
        String raza;

        if (mascotaId != -1) {
            Mascota mascota = databaseHelper.obtenerMascotaPorId(mascotaId);
            if (mascota == null) {
                Toast.makeText(this, R.string.toast_mascota_no_encontrada, Toast.LENGTH_SHORT).show();
                volverAMisMascotas();
                return;
            }

            nombre = mascota.getNombre();
            especie = mascota.getEspecie();
            edad = String.valueOf(mascota.getEdad());
            raza = mascota.getRaza();
        } else {
            // Compatibilidad con Próximos controles y Vacunas de avances anteriores.
            nombre = getIntent().getStringExtra("nombreMascota");
            especie = getIntent().getStringExtra("especieMascota");
            edad = getIntent().getStringExtra("edadMascota");
            raza = getIntent().getStringExtra("razaMascota");
        }

        if (nombre == null) nombre = "";
        if (especie == null) especie = "";
        if (edad == null) edad = "";
        if (raza == null) raza = "";

        mostrarDatosPrincipales(nombre, especie, edad, raza);
        limpiarInformacionSalud();
        mostrarInformacionSalud(mascotaId, nombre);
        aplicarInformacionRecibida();
    }

    private void mostrarDatosPrincipales(String nombre, String especie, String edad, String raza) {
        txtNombre.setText(nombre);

        if (!especie.isEmpty()) {
            txtEspecie.setText(getString(R.string.detalle_formato_especie, especie));
            txtEspecie.setVisibility(View.VISIBLE);
        } else {
            txtEspecie.setVisibility(View.GONE);
        }

        if (!edad.isEmpty()) {
            txtEdad.setText(getString(R.string.detalle_formato_edad, edad));
            txtEdad.setVisibility(View.VISIBLE);
        } else {
            txtEdad.setVisibility(View.GONE);
        }

        if (!raza.isEmpty()) {
            txtRaza.setText(getString(R.string.detalle_formato_raza, raza));
            txtRaza.setVisibility(View.VISIBLE);
        } else {
            txtRaza.setVisibility(View.GONE);
        }
    }

    private void limpiarInformacionSalud() {
        txtHistorial1.setVisibility(View.GONE);
        txtHistorial2.setVisibility(View.GONE);
        txtHistorialVacio.setVisibility(View.GONE);

        txtControlTipo.setVisibility(View.GONE);
        txtControlFecha.setVisibility(View.GONE);
        txtControlEstado.setVisibility(View.GONE);
        txtControlesVacio.setVisibility(View.GONE);

        txtVacunaNombre.setVisibility(View.GONE);
        txtVacunaFecha.setVisibility(View.GONE);
        txtVacunaEstado.setVisibility(View.GONE);
        txtVacunasVacio.setVisibility(View.GONE);
    }

    private void mostrarInformacionSalud(long id, String nombre) {
        boolean esLuna = id == 1 || (id == -1 && nombre.equals(getString(R.string.mascota_luna)));
        boolean esMilo = id == 2 || (id == -1 && nombre.equals(getString(R.string.mascota_milo)));

        if (esLuna) {
            txtHistorial1.setText(R.string.luna_historial_vacuna);
            txtHistorial2.setText(R.string.luna_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);
            txtHistorial2.setVisibility(View.VISIBLE);

            txtControlTipo.setText(R.string.luna_tipo_control);
            txtControlFecha.setText(R.string.luna_fecha_control);
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl();

            txtVacunaNombre.setText(R.string.luna_vacuna);
            txtVacunaFecha.setText(R.string.luna_fecha_vacuna);
            txtVacunaEstado.setText(R.string.luna_estado_vacuna);
            mostrarVacuna();

        } else if (esMilo) {
            txtHistorial1.setText(R.string.milo_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);

            txtControlTipo.setText(R.string.milo_tipo_control);
            txtControlFecha.setText(R.string.milo_fecha_control);
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl();

            txtVacunaNombre.setText(R.string.milo_vacuna);
            txtVacunaFecha.setText(R.string.milo_fecha_vacuna);
            txtVacunaEstado.setText(R.string.milo_estado_vacuna);
            mostrarVacuna();

        } else {
            txtHistorialVacio.setVisibility(View.VISIBLE);
            txtControlesVacio.setVisibility(View.VISIBLE);
            txtVacunasVacio.setVisibility(View.VISIBLE);
        }
    }

    private void aplicarInformacionRecibida() {
        String tipoControlSeleccionado = getIntent().getStringExtra("tipoControlSeleccionado");
        String fechaControlSeleccionado = getIntent().getStringExtra("fechaControlSeleccionado");
        String vacunaSeleccionada = getIntent().getStringExtra("vacunaSeleccionada");
        String fechaVacunaSeleccionada = getIntent().getStringExtra("fechaVacunaSeleccionada");
        String estadoVacunaSeleccionada = getIntent().getStringExtra("estadoVacunaSeleccionada");

        if (tipoControlSeleccionado != null && fechaControlSeleccionado != null) {
            txtControlTipo.setText(getString(R.string.formato_tipo_control, tipoControlSeleccionado));
            txtControlFecha.setText(getString(R.string.formato_fecha_control, fechaControlSeleccionado));
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl();
        }

        if (vacunaSeleccionada != null && fechaVacunaSeleccionada != null
                && estadoVacunaSeleccionada != null) {
            txtVacunaNombre.setText(getString(R.string.formato_vacuna, vacunaSeleccionada));
            txtVacunaFecha.setText(getString(R.string.formato_fecha_vacuna, fechaVacunaSeleccionada));
            txtVacunaEstado.setText(getString(R.string.formato_estado_vacuna, estadoVacunaSeleccionada));
            mostrarVacuna();
        }
    }

    private void mostrarControl() {
        txtControlTipo.setVisibility(View.VISIBLE);
        txtControlFecha.setVisibility(View.VISIBLE);
        txtControlEstado.setVisibility(View.VISIBLE);
        txtControlesVacio.setVisibility(View.GONE);
    }

    private void mostrarVacuna() {
        txtVacunaNombre.setVisibility(View.VISIBLE);
        txtVacunaFecha.setVisibility(View.VISIBLE);
        txtVacunaEstado.setVisibility(View.VISIBLE);
        txtVacunasVacio.setVisibility(View.GONE);
    }

    private void abrirEdicion() {
        if (mascotaId == -1) {
            return;
        }

        Intent intent = new Intent(DetalleMascotaActivity.this, EditarMascotaActivity.class);
        intent.putExtra(EditarMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    private void confirmarEliminacion() {
        if (mascotaId == -1) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_mascota)
                .setMessage(R.string.confirmar_eliminar_mascota)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (dialog, which) -> eliminarMascota())
                .show();
    }

    private void eliminarMascota() {
        boolean eliminada = databaseHelper.eliminarMascota(mascotaId);
        if (!eliminada) {
            Toast.makeText(this, R.string.toast_error_eliminar_mascota, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_mascota_eliminada, Toast.LENGTH_SHORT).show();
        volverAMisMascotas();
    }

    private void volverAMisMascotas() {
        Intent intent = new Intent(DetalleMascotaActivity.this, MisMascotasActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
