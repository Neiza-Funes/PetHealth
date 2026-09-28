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

public class VacunasActivity extends Activity {

    private LinearLayout contenedorVacunas;
    private LinearLayout cardVacunaNueva;
    private String mascotaVacunaNueva;
    private String nombreVacunaNueva;
    private String fechaVacunaNueva;
    private String estadoVacunaNueva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas);

        Button btnVolver = findViewById(R.id.btnVolverInicioVacunas);
        Button btnVerLuna = findViewById(R.id.btnVerLunaVacuna);
        Button btnVerMilo = findViewById(R.id.btnVerMiloVacuna);
        Button btnRegistrarVacuna = findViewById(R.id.btnRegistrarVacuna);
        Button btnVerVacunaNueva = findViewById(R.id.btnVerVacunaNueva);

        contenedorVacunas = findViewById(R.id.contenedorVacunas);
        cardVacunaNueva = findViewById(R.id.cardVacunaNueva);

        TextView txtNuevaMascota = findViewById(R.id.txtNuevaMascota);
        TextView txtNuevaVacuna = findViewById(R.id.txtNuevaVacuna);
        TextView txtNuevaFecha = findViewById(R.id.txtNuevaFecha);
        TextView txtNuevoEstado = findViewById(R.id.txtNuevoEstado);

        btnVolver.setOnClickListener(v -> finish());

        btnVerLuna.setOnClickListener(v -> abrirDetalleMascota(
                getString(R.string.mascota_luna),
                getString(R.string.luna_especie_valor),
                getString(R.string.luna_edad_valor),
                "",
                getString(R.string.luna_vacuna_valor),
                getString(R.string.luna_fecha_vacuna_valor),
                getString(R.string.luna_estado_vacuna_valor)
        ));

        btnVerMilo.setOnClickListener(v -> abrirDetalleMascota(
                getString(R.string.mascota_milo),
                getString(R.string.milo_especie_valor),
                getString(R.string.milo_edad_valor),
                "",
                getString(R.string.milo_vacuna_valor),
                getString(R.string.milo_fecha_vacuna_valor),
                getString(R.string.milo_estado_vacuna_valor)
        ));

        btnRegistrarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(VacunasActivity.this, RegistrarVacunaActivity.class);
            startActivity(intent);
        });

        leerVacunaRecibida(txtNuevaMascota, txtNuevaVacuna, txtNuevaFecha, txtNuevoEstado);

        btnVerVacunaNueva.setOnClickListener(v -> {
            if (mascotaVacunaNueva != null && nombreVacunaNueva != null
                    && fechaVacunaNueva != null && estadoVacunaNueva != null) {

                String especie = "";
                String edad = "";

                if (mascotaVacunaNueva.equalsIgnoreCase(getString(R.string.mascota_luna))) {
                    especie = getString(R.string.luna_especie_valor);
                    edad = getString(R.string.luna_edad_valor);
                } else if (mascotaVacunaNueva.equalsIgnoreCase(getString(R.string.mascota_milo))) {
                    especie = getString(R.string.milo_especie_valor);
                    edad = getString(R.string.milo_edad_valor);
                }

                abrirDetalleMascota(
                        mascotaVacunaNueva,
                        especie,
                        edad,
                        "",
                        nombreVacunaNueva,
                        fechaVacunaNueva,
                        estadoVacunaNueva
                );
            }
        });
    }

    private void leerVacunaRecibida(
            TextView txtMascota,
            TextView txtVacuna,
            TextView txtFecha,
            TextView txtEstado) {

        Intent intent = getIntent();
        mascotaVacunaNueva = intent.getStringExtra("mascota");
        nombreVacunaNueva = intent.getStringExtra("vacuna");
        fechaVacunaNueva = intent.getStringExtra("fecha");
        estadoVacunaNueva = intent.getStringExtra("estado");

        if (mascotaVacunaNueva != null && nombreVacunaNueva != null
                && fechaVacunaNueva != null && estadoVacunaNueva != null) {

            txtMascota.setText(mascotaVacunaNueva);
            txtVacuna.setText(getString(R.string.formato_vacuna, nombreVacunaNueva));
            txtFecha.setText(getString(R.string.formato_fecha_vacuna, fechaVacunaNueva));
            txtEstado.setText(getString(R.string.formato_estado_vacuna, estadoVacunaNueva));
            cardVacunaNueva.setVisibility(View.VISIBLE);

            ordenarVacunaNueva(fechaVacunaNueva);
        }
    }

    private void ordenarVacunaNueva(String fechaNueva) {
        Date nueva = convertirFecha(fechaNueva);
        Date luna = convertirFecha(getString(R.string.luna_fecha_vacuna_valor));
        Date milo = convertirFecha(getString(R.string.milo_fecha_vacuna_valor));

        if (nueva == null || luna == null || milo == null) {
            return;
        }

        contenedorVacunas.removeView(cardVacunaNueva);

        if (!nueva.after(luna)) {
            contenedorVacunas.addView(cardVacunaNueva, 0);
        } else if (!nueva.after(milo)) {
            contenedorVacunas.addView(cardVacunaNueva, 1);
        } else {
            contenedorVacunas.addView(cardVacunaNueva);
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
            String vacuna,
            String fechaVacuna,
            String estadoVacuna) {

        Intent intent = new Intent(VacunasActivity.this, DetalleMascotaActivity.class);
        intent.putExtra("nombreMascota", nombre);
        intent.putExtra("especieMascota", especie);
        intent.putExtra("edadMascota", edad);
        intent.putExtra("razaMascota", raza);
        intent.putExtra("vacunaSeleccionada", vacuna);
        intent.putExtra("fechaVacunaSeleccionada", fechaVacuna);
        intent.putExtra("estadoVacunaSeleccionada", estadoVacuna);
        startActivity(intent);
    }
}
