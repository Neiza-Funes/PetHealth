package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ControlesActivity extends Activity {

    private LinearLayout contenedorControles;
    private LinearLayout cardControlNuevo;
    private String mascotaControlNueva;
    private String tipoControlNuevo;
    private String fechaControlNueva;
    private String observacionControlNueva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);

        Button btnVolver = findViewById(R.id.btnVolverInicioControles);
        Button btnVerLuna = findViewById(R.id.btnVerLunaControl);
        Button btnVerMilo = findViewById(R.id.btnVerMiloControl);
        Button btnRegistrarControl = findViewById(R.id.btnRegistrarControl);
        Button btnVerControlNuevo = findViewById(R.id.btnVerControlNuevo);

        contenedorControles = findViewById(R.id.contenedorControles);
        cardControlNuevo = findViewById(R.id.cardControlNuevo);

        TextView txtControlMascotaNueva = findViewById(R.id.txtControlMascotaNueva);
        TextView txtControlTipoNuevo = findViewById(R.id.txtControlTipoNuevo);
        TextView txtControlFechaNueva = findViewById(R.id.txtControlFechaNueva);
        TextView txtControlObservacionNueva = findViewById(R.id.txtControlObservacionNueva);
        TextView txtControlEstadoNuevo = findViewById(R.id.txtControlEstadoNuevo);

        btnVolver.setOnClickListener(v -> finish());

        btnVerLuna.setOnClickListener(v -> abrirDetalleMascota(
                getString(R.string.mascota_luna),
                getString(R.string.luna_especie_valor),
                getString(R.string.luna_edad_valor),
                "",
                null,
                null
        ));

        btnVerMilo.setOnClickListener(v -> abrirDetalleMascota(
                getString(R.string.mascota_milo),
                getString(R.string.milo_especie_valor),
                getString(R.string.milo_edad_valor),
                "",
                null,
                null
        ));

        btnRegistrarControl.setOnClickListener(v -> {
            Intent intent = new Intent(ControlesActivity.this, RegistrarControlActivity.class);
            startActivity(intent);
        });

        leerControlRecibido(
                txtControlMascotaNueva,
                txtControlTipoNuevo,
                txtControlFechaNueva,
                txtControlObservacionNueva,
                txtControlEstadoNuevo
        );

        btnVerControlNuevo.setOnClickListener(v -> {
            if (mascotaControlNueva != null && tipoControlNuevo != null && fechaControlNueva != null) {
                String especie = "";
                String edad = "";

                if (mascotaControlNueva.equalsIgnoreCase(getString(R.string.mascota_luna))) {
                    especie = getString(R.string.luna_especie_valor);
                    edad = getString(R.string.luna_edad_valor);
                } else if (mascotaControlNueva.equalsIgnoreCase(getString(R.string.mascota_milo))) {
                    especie = getString(R.string.milo_especie_valor);
                    edad = getString(R.string.milo_edad_valor);
                }

                abrirDetalleMascota(
                        mascotaControlNueva,
                        especie,
                        edad,
                        "",
                        tipoControlNuevo,
                        fechaControlNueva
                );
            }
        });
    }

    private void leerControlRecibido(
            TextView txtMascota,
            TextView txtTipo,
            TextView txtFecha,
            TextView txtObservacion,
            TextView txtEstado) {

        Intent intent = getIntent();
        mascotaControlNueva = intent.getStringExtra("mascotaControl");
        tipoControlNuevo = intent.getStringExtra("tipoControl");
        fechaControlNueva = intent.getStringExtra("fechaControl");
        observacionControlNueva = intent.getStringExtra("observacionControl");

        if (mascotaControlNueva != null && tipoControlNuevo != null && fechaControlNueva != null) {
            txtMascota.setText(mascotaControlNueva);
            txtTipo.setText(getString(R.string.formato_tipo_control, tipoControlNuevo));
            txtFecha.setText(getString(R.string.formato_fecha_control, fechaControlNueva));
            txtEstado.setText(R.string.control_estado_pendiente);

            if (observacionControlNueva != null && !observacionControlNueva.isEmpty()) {
                txtObservacion.setText(
                        getString(R.string.formato_observacion_control, observacionControlNueva));
                txtObservacion.setVisibility(View.VISIBLE);
            } else {
                txtObservacion.setVisibility(View.GONE);
            }

            cardControlNuevo.setVisibility(View.VISIBLE);
            ordenarControlNuevo(fechaControlNueva);
        }
    }

    private void ordenarControlNuevo(String fechaNueva) {
        Date nueva = convertirFecha(fechaNueva);
        Date luna = convertirFecha(getString(R.string.luna_fecha_control_valor));
        Date milo = convertirFecha(getString(R.string.milo_fecha_control_valor));

        if (nueva == null || luna == null || milo == null) {
            return;
        }

        contenedorControles.removeView(cardControlNuevo);

        if (!nueva.after(luna)) {
            contenedorControles.addView(cardControlNuevo, 0);
        } else if (!nueva.after(milo)) {
            contenedorControles.addView(cardControlNuevo, 1);
        } else {
            contenedorControles.addView(cardControlNuevo);
        }
    }

    private Date convertirFecha(String fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        formato.setLenient(false);
        try {
            return formato.parse(fecha);
        } catch (ParseException e) {
            return null;
        }
    }

    private void abrirDetalleMascota(
            String nombre,
            String especie,
            String edad,
            String raza,
            String tipoControlSeleccionado,
            String fechaControlSeleccionado) {

        Intent intent = new Intent(ControlesActivity.this, DetalleMascotaActivity.class);
        intent.putExtra("nombreMascota", nombre);
        intent.putExtra("especieMascota", especie);
        intent.putExtra("edadMascota", edad);
        intent.putExtra("razaMascota", raza);

        if (tipoControlSeleccionado != null && fechaControlSeleccionado != null) {
            intent.putExtra("tipoControlSeleccionado", tipoControlSeleccionado);
            intent.putExtra("fechaControlSeleccionado", fechaControlSeleccionado);
        }

        startActivity(intent);
    }
}
