package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class InicioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Button btnMisMascotas = findViewById(R.id.btnMisMascotas);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascota);
        Button btnProximosControles = findViewById(R.id.btnProximosControles);
        Button btnVacunas = findViewById(R.id.btnVacunas);
        Button btnDesparasitacion = findViewById(R.id.btnDesparasitacion);

        btnMisMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistrarMascotaActivity.class);
            startActivity(intent);
        });

        btnProximosControles.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, ControlesActivity.class);
            startActivity(intent);
        });

        btnVacunas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, VacunasActivity.class);
            startActivity(intent);
        });

        btnDesparasitacion.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, DesparasitacionActivity.class);
            startActivity(intent);
        });
    }

}
