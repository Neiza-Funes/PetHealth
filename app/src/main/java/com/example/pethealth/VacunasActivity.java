package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VacunasActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas);

        Button btnRegistrarVacuna = findViewById(R.id.btnRegistrarVacuna);
        LinearLayout cardVacunaNueva = findViewById(R.id.cardVacunaNueva);
        TextView txtNuevaMascota = findViewById(R.id.txtNuevaMascota);
        TextView txtNuevaVacuna = findViewById(R.id.txtNuevaVacuna);
        TextView txtNuevaFecha = findViewById(R.id.txtNuevaFecha);
        TextView txtNuevoEstado = findViewById(R.id.txtNuevoEstado);

        btnRegistrarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(VacunasActivity.this, RegistrarVacunaActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        if (intent.hasExtra("mascota")) {
            String mascota = intent.getStringExtra("mascota");
            String vacuna = intent.getStringExtra("vacuna");
            String fecha = intent.getStringExtra("fecha");
            String estado = intent.getStringExtra("estado");

            txtNuevaMascota.setText(mascota);
            txtNuevaVacuna.setText(getString(R.string.formato_vacuna, vacuna));
            txtNuevaFecha.setText(getString(R.string.formato_fecha_vacuna, fecha));
            txtNuevoEstado.setText(getString(R.string.formato_estado_vacuna, estado));
            cardVacunaNueva.setVisibility(View.VISIBLE);
        }
    }
}
