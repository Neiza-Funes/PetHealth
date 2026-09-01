package com.example.pethealth;

import android.app.Activity;
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

        btnMisMascotas.setOnClickListener(v -> mostrarProximamente());
        btnRegistrarMascota.setOnClickListener(v -> mostrarProximamente());
        btnProximosControles.setOnClickListener(v -> mostrarProximamente());
        btnVacunas.setOnClickListener(v -> mostrarProximamente());
    }

    private void mostrarProximamente() {
        Toast.makeText(this, R.string.toast_proximamente, Toast.LENGTH_SHORT).show();
    }
}
