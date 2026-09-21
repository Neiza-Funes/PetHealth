package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class InicioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Button btnMisMascotas = findViewById(R.id.btnMisMascotas);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascota);
        Button btnProximosControles = findViewById(R.id.btnProximosControles);
        Button btnVacunas = findViewById(R.id.btnVacunas);

        btnMisMascotas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, MisMascotasActivity.class);
            startActivity(intent);
        });

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistrarMascotaActivity.class);
            startActivity(intent);
        });

        btnProximosControles.setOnClickListener(v -> mostrarProximamente());
        btnVacunas.setOnClickListener(v -> mostrarProximamente());
    }

    private void mostrarProximamente() {
        Toast.makeText(this, R.string.toast_proximamente, Toast.LENGTH_SHORT).show();
    }
}
