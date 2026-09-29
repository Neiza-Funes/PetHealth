package com.example.pethealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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

    private LinearLayout contenedorDetalleControles;
    private TextView txtControlesVacio;

    private LinearLayout contenedorDetalleVacunas;
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

        contenedorDetalleControles = findViewById(R.id.contenedorDetalleControles);
        txtControlesVacio = findViewById(R.id.txtControlesVacio);

        contenedorDetalleVacunas = findViewById(R.id.contenedorDetalleVacunas);
        txtVacunasVacio = findViewById(R.id.txtVacunasVacio);

        btnVolver.setOnClickListener(v -> finish());
        btnEditar.setOnClickListener(v -> abrirEdicion());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());

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
            // Compatibilidad con navegaciones antiguas que aún envíen datos directos.
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
        mostrarHistorial(mascotaId, nombre);
        mostrarControles(mascotaId, nombre);
        mostrarVacunas(mascotaId, nombre);
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

        contenedorDetalleControles.removeAllViews();
        contenedorDetalleControles.setVisibility(View.GONE);
        txtControlesVacio.setVisibility(View.GONE);

        contenedorDetalleVacunas.removeAllViews();
        contenedorDetalleVacunas.setVisibility(View.GONE);
        txtVacunasVacio.setVisibility(View.GONE);
    }

    private void mostrarHistorial(long id, String nombre) {
        boolean esLuna = id == 1 || (id == -1 && nombre.equals(getString(R.string.mascota_luna)));
        boolean esMilo = id == 2 || (id == -1 && nombre.equals(getString(R.string.mascota_milo)));

        if (esLuna) {
            txtHistorial1.setText(R.string.luna_historial_vacuna);
            txtHistorial2.setText(R.string.luna_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);
            txtHistorial2.setVisibility(View.VISIBLE);
        } else if (esMilo) {
            txtHistorial1.setText(R.string.milo_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);
        } else {
            txtHistorialVacio.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarControles(long id, String nombre) {
        if (id != -1) {
            List<Control> controles = databaseHelper.obtenerControlesPorMascota(id);
            if (controles.isEmpty()) {
                txtControlesVacio.setVisibility(View.VISIBLE);
                return;
            }

            for (Control control : controles) {
                agregarControlVisual(
                        control.getTipo(),
                        control.getFecha(),
                        control.getEstado(),
                        control.getObservacion()
                );
            }
            return;
        }

        // Compatibilidad visual con navegaciones antiguas sin ID SQLite.
        if (nombre.equals(getString(R.string.mascota_luna))) {
            agregarControlVisual(
                    getString(R.string.luna_tipo_control_valor),
                    getString(R.string.luna_fecha_control_valor),
                    getString(R.string.estado_pendiente_valor),
                    ""
            );
        } else if (nombre.equals(getString(R.string.mascota_milo))) {
            agregarControlVisual(
                    getString(R.string.milo_tipo_control_valor),
                    getString(R.string.milo_fecha_control_valor),
                    getString(R.string.estado_pendiente_valor),
                    ""
            );
        } else {
            txtControlesVacio.setVisibility(View.VISIBLE);
        }
    }

    private void agregarControlVisual(
            String tipo,
            String fecha,
            String estado,
            String observacion) {

        View item = LayoutInflater.from(this)
                .inflate(R.layout.item_control_detalle, contenedorDetalleControles, false);

        TextView txtTipo = item.findViewById(R.id.txtDetalleItemControlTipo);
        TextView txtFecha = item.findViewById(R.id.txtDetalleItemControlFecha);
        TextView txtEstado = item.findViewById(R.id.txtDetalleItemControlEstado);
        TextView txtObservacion = item.findViewById(R.id.txtDetalleItemControlObservacion);

        txtTipo.setText(getString(R.string.formato_tipo_control, tipo));
        txtFecha.setText(getString(R.string.formato_fecha_control, fecha));
        txtEstado.setText(getString(R.string.formato_estado_control, estado));

        if (observacion == null || observacion.isEmpty()) {
            txtObservacion.setVisibility(View.GONE);
        } else {
            txtObservacion.setText(getString(R.string.formato_observacion_control, observacion));
            txtObservacion.setVisibility(View.VISIBLE);
        }

        contenedorDetalleControles.addView(item);
        contenedorDetalleControles.setVisibility(View.VISIBLE);
        txtControlesVacio.setVisibility(View.GONE);
    }

    private void mostrarVacunas(long id, String nombre) {
        if (id != -1) {
            List<Vacuna> vacunas = databaseHelper.obtenerVacunasPorMascota(id);
            if (vacunas.isEmpty()) {
                txtVacunasVacio.setVisibility(View.VISIBLE);
                return;
            }

            for (Vacuna vacuna : vacunas) {
                agregarVacunaVisual(
                        vacuna.getNombre(),
                        vacuna.getFecha(),
                        vacuna.getEstado()
                );
            }
            return;
        }

        // Compatibilidad visual para una navegación antigua sin ID SQLite.
        if (nombre.equals(getString(R.string.mascota_luna))) {
            agregarVacunaVisual(
                    getString(R.string.luna_vacuna_valor),
                    getString(R.string.luna_fecha_vacuna_valor),
                    getString(R.string.luna_estado_vacuna_valor)
            );
        } else if (nombre.equals(getString(R.string.mascota_milo))) {
            agregarVacunaVisual(
                    getString(R.string.milo_vacuna_valor),
                    getString(R.string.milo_fecha_vacuna_valor),
                    getString(R.string.milo_estado_vacuna_valor)
            );
        } else {
            txtVacunasVacio.setVisibility(View.VISIBLE);
        }
    }

    private void agregarVacunaVisual(String nombre, String fecha, String estado) {
        View item = LayoutInflater.from(this)
                .inflate(R.layout.item_vacuna_detalle, contenedorDetalleVacunas, false);

        TextView txtNombreVacuna = item.findViewById(R.id.txtDetalleItemVacunaNombre);
        TextView txtFechaVacuna = item.findViewById(R.id.txtDetalleItemVacunaFecha);
        TextView txtEstadoVacuna = item.findViewById(R.id.txtDetalleItemVacunaEstado);

        txtNombreVacuna.setText(getString(R.string.formato_vacuna, nombre));
        txtFechaVacuna.setText(getString(R.string.formato_fecha_vacuna, fecha));
        txtEstadoVacuna.setText(getString(R.string.formato_estado_vacuna, estado));

        contenedorDetalleVacunas.addView(item);
        contenedorDetalleVacunas.setVisibility(View.VISIBLE);
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
