package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MisMascotasActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_mascotas);

        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascotaLista);
        LinearLayout cardMascotaNueva = findViewById(R.id.cardMascotaNueva);
        TextView txtNombreMascotaNueva = findViewById(R.id.txtNombreMascotaNueva);
        TextView txtEspecieMascotaNueva = findViewById(R.id.txtEspecieMascotaNueva);
        TextView txtEdadMascotaNueva = findViewById(R.id.txtEdadMascotaNueva);
        TextView txtRazaMascotaNueva = findViewById(R.id.txtRazaMascotaNueva);

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MisMascotasActivity.this, RegistrarMascotaActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombreMascota");
        String especie = intent.getStringExtra("especieMascota");
        String edad = intent.getStringExtra("edadMascota");
        String raza = intent.getStringExtra("razaMascota");

        if (nombre != null && especie != null && edad != null) {
            cardMascotaNueva.setVisibility(View.VISIBLE);
            txtNombreMascotaNueva.setText(nombre);
            txtEspecieMascotaNueva.setText(getString(R.string.formato_especie, especie));
            txtEdadMascotaNueva.setText(getString(R.string.formato_edad, edad));

            if (raza != null && !raza.isEmpty()) {
                txtRazaMascotaNueva.setText(getString(R.string.formato_raza, raza));
                txtRazaMascotaNueva.setVisibility(View.VISIBLE);
            } else {
                txtRazaMascotaNueva.setVisibility(View.GONE);
            }
        }
    }
}
