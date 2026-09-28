package com.example.pethealth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleMascotaActivity extends Activity {

    public static final String EXTRA_MASCOTA_ID = "mascotaId";

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        databaseHelper = new DatabaseHelper(this);

        Button btnVolver = findViewById(R.id.btnVolverMisMascotas);
        TextView txtNombre = findViewById(R.id.txtDetalleNombre);
        TextView txtEspecie = findViewById(R.id.txtDetalleEspecie);
        TextView txtEdad = findViewById(R.id.txtDetalleEdad);
        TextView txtRaza = findViewById(R.id.txtDetalleRaza);

        TextView txtHistorial1 = findViewById(R.id.txtHistorial1);
        TextView txtHistorial2 = findViewById(R.id.txtHistorial2);
        TextView txtHistorialVacio = findViewById(R.id.txtHistorialVacio);

        TextView txtControlTipo = findViewById(R.id.txtDetalleControlTipo);
        TextView txtControlFecha = findViewById(R.id.txtDetalleControlFecha);
        TextView txtControlEstado = findViewById(R.id.txtDetalleControlEstado);
        TextView txtControlesVacio = findViewById(R.id.txtControlesVacio);

        TextView txtVacunaNombre = findViewById(R.id.txtDetalleVacunaNombre);
        TextView txtVacunaFecha = findViewById(R.id.txtDetalleVacunaFecha);
        TextView txtVacunaEstado = findViewById(R.id.txtDetalleVacunaEstado);
        TextView txtVacunasVacio = findViewById(R.id.txtVacunasVacio);

        String nombre;
        String especie;
        String edad;
        String raza;

        long mascotaId = getIntent().getLongExtra(EXTRA_MASCOTA_ID, -1);
        if (mascotaId != -1) {
            Mascota mascota = databaseHelper.obtenerMascotaPorId(mascotaId);
            if (mascota == null) {
                Toast.makeText(this, R.string.toast_mascota_no_encontrada, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            nombre = mascota.getNombre();
            especie = mascota.getEspecie();
            edad = String.valueOf(mascota.getEdad());
            raza = mascota.getRaza();
        } else {
            // Compatibilidad con Controles y Vacunas de los avances anteriores.
            nombre = getIntent().getStringExtra("nombreMascota");
            especie = getIntent().getStringExtra("especieMascota");
            edad = getIntent().getStringExtra("edadMascota");
            raza = getIntent().getStringExtra("razaMascota");
        }

        String tipoControlSeleccionado = getIntent().getStringExtra("tipoControlSeleccionado");
        String fechaControlSeleccionado = getIntent().getStringExtra("fechaControlSeleccionado");
        String vacunaSeleccionada = getIntent().getStringExtra("vacunaSeleccionada");
        String fechaVacunaSeleccionada = getIntent().getStringExtra("fechaVacunaSeleccionada");
        String estadoVacunaSeleccionada = getIntent().getStringExtra("estadoVacunaSeleccionada");

        if (nombre == null) nombre = "";
        if (especie == null) especie = "";
        if (edad == null) edad = "";
        if (raza == null) raza = "";

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

        mostrarInformacionSalud(
                nombre,
                txtHistorial1,
                txtHistorial2,
                txtHistorialVacio,
                txtControlTipo,
                txtControlFecha,
                txtControlEstado,
                txtControlesVacio,
                txtVacunaNombre,
                txtVacunaFecha,
                txtVacunaEstado,
                txtVacunasVacio
        );

        if (tipoControlSeleccionado != null && fechaControlSeleccionado != null) {
            txtControlTipo.setText(getString(R.string.formato_tipo_control, tipoControlSeleccionado));
            txtControlFecha.setText(getString(R.string.formato_fecha_control, fechaControlSeleccionado));
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl(txtControlTipo, txtControlFecha, txtControlEstado, txtControlesVacio);
        }

        if (vacunaSeleccionada != null && fechaVacunaSeleccionada != null
                && estadoVacunaSeleccionada != null) {
            txtVacunaNombre.setText(getString(R.string.formato_vacuna, vacunaSeleccionada));
            txtVacunaFecha.setText(getString(R.string.formato_fecha_vacuna, fechaVacunaSeleccionada));
            txtVacunaEstado.setText(getString(R.string.formato_estado_vacuna, estadoVacunaSeleccionada));
            mostrarVacuna(txtVacunaNombre, txtVacunaFecha, txtVacunaEstado, txtVacunasVacio);
        }

        btnVolver.setOnClickListener(v -> finish());
    }

    private void mostrarInformacionSalud(
            String nombre,
            TextView txtHistorial1,
            TextView txtHistorial2,
            TextView txtHistorialVacio,
            TextView txtControlTipo,
            TextView txtControlFecha,
            TextView txtControlEstado,
            TextView txtControlesVacio,
            TextView txtVacunaNombre,
            TextView txtVacunaFecha,
            TextView txtVacunaEstado,
            TextView txtVacunasVacio) {

        if (nombre.equals(getString(R.string.mascota_luna))) {
            txtHistorial1.setText(R.string.luna_historial_vacuna);
            txtHistorial2.setText(R.string.luna_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);
            txtHistorial2.setVisibility(View.VISIBLE);
            txtHistorialVacio.setVisibility(View.GONE);

            txtControlTipo.setText(R.string.luna_tipo_control);
            txtControlFecha.setText(R.string.luna_fecha_control);
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl(txtControlTipo, txtControlFecha, txtControlEstado, txtControlesVacio);

            txtVacunaNombre.setText(R.string.luna_vacuna);
            txtVacunaFecha.setText(R.string.luna_fecha_vacuna);
            txtVacunaEstado.setText(R.string.luna_estado_vacuna);
            mostrarVacuna(txtVacunaNombre, txtVacunaFecha, txtVacunaEstado, txtVacunasVacio);

        } else if (nombre.equals(getString(R.string.mascota_milo))) {
            txtHistorial1.setText(R.string.milo_historial_desparasitacion);
            txtHistorial2.setVisibility(View.GONE);
            txtHistorial1.setVisibility(View.VISIBLE);
            txtHistorialVacio.setVisibility(View.GONE);

            txtControlTipo.setText(R.string.milo_tipo_control);
            txtControlFecha.setText(R.string.milo_fecha_control);
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl(txtControlTipo, txtControlFecha, txtControlEstado, txtControlesVacio);

            txtVacunaNombre.setText(R.string.milo_vacuna);
            txtVacunaFecha.setText(R.string.milo_fecha_vacuna);
            txtVacunaEstado.setText(R.string.milo_estado_vacuna);
            mostrarVacuna(txtVacunaNombre, txtVacunaFecha, txtVacunaEstado, txtVacunasVacio);

        } else {
            txtHistorial1.setVisibility(View.GONE);
            txtHistorial2.setVisibility(View.GONE);
            txtHistorialVacio.setVisibility(View.VISIBLE);

            txtControlTipo.setVisibility(View.GONE);
            txtControlFecha.setVisibility(View.GONE);
            txtControlEstado.setVisibility(View.GONE);
            txtControlesVacio.setVisibility(View.VISIBLE);

            txtVacunaNombre.setVisibility(View.GONE);
            txtVacunaFecha.setVisibility(View.GONE);
            txtVacunaEstado.setVisibility(View.GONE);
            txtVacunasVacio.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarControl(TextView tipo, TextView fecha, TextView estado, TextView vacio) {
        tipo.setVisibility(View.VISIBLE);
        fecha.setVisibility(View.VISIBLE);
        estado.setVisibility(View.VISIBLE);
        vacio.setVisibility(View.GONE);
    }

    private void mostrarVacuna(TextView nombre, TextView fecha, TextView estado, TextView vacio) {
        nombre.setVisibility(View.VISIBLE);
        fecha.setVisibility(View.VISIBLE);
        estado.setVisibility(View.VISIBLE);
        vacio.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
