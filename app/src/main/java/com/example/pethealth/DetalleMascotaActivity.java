package com.example.pethealth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetalleMascotaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

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

        String nombre = getIntent().getStringExtra("nombreMascota");
        String especie = getIntent().getStringExtra("especieMascota");
        String edad = getIntent().getStringExtra("edadMascota");
        String raza = getIntent().getStringExtra("razaMascota");

        if (nombre == null) nombre = "";
        if (especie == null) especie = "";
        if (edad == null) edad = "";
        if (raza == null) raza = "";

        txtNombre.setText(nombre);
        txtEspecie.setText(getString(R.string.detalle_formato_especie, especie));
        txtEdad.setText(getString(R.string.detalle_formato_edad, edad));

        if (!raza.isEmpty()) {
            txtRaza.setText(getString(R.string.detalle_formato_raza, raza));
            txtRaza.setVisibility(View.VISIBLE);
        } else {
            txtRaza.setVisibility(View.GONE);
        }

        mostrarInformacionSalud(nombre, txtHistorial1, txtHistorial2, txtHistorialVacio,
                txtControlTipo, txtControlFecha, txtControlEstado, txtControlesVacio);

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
            TextView txtControlesVacio) {

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

        } else if (nombre.equals(getString(R.string.mascota_milo))) {
            txtHistorial1.setText(R.string.milo_historial_desparasitacion);
            txtHistorial2.setVisibility(View.GONE);
            txtHistorial1.setVisibility(View.VISIBLE);
            txtHistorialVacio.setVisibility(View.GONE);

            txtControlTipo.setText(R.string.milo_tipo_control);
            txtControlFecha.setText(R.string.milo_fecha_control);
            txtControlEstado.setText(R.string.control_estado_pendiente);
            mostrarControl(txtControlTipo, txtControlFecha, txtControlEstado, txtControlesVacio);

        } else {
            txtHistorial1.setVisibility(View.GONE);
            txtHistorial2.setVisibility(View.GONE);
            txtHistorialVacio.setVisibility(View.VISIBLE);

            txtControlTipo.setVisibility(View.GONE);
            txtControlFecha.setVisibility(View.GONE);
            txtControlEstado.setVisibility(View.GONE);
            txtControlesVacio.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarControl(TextView tipo, TextView fecha, TextView estado, TextView vacio) {
        tipo.setVisibility(View.VISIBLE);
        fecha.setVisibility(View.VISIBLE);
        estado.setVisibility(View.VISIBLE);
        vacio.setVisibility(View.GONE);
    }
}
