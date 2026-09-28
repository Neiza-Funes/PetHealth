package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MisMascotasActivity extends Activity {

    private LinearLayout cardMascotaNueva;
    private TextView txtNombreMascotaNueva;
    private TextView txtEspecieMascotaNueva;
    private TextView txtEdadMascotaNueva;
    private TextView txtRazaMascotaNueva;
    private Button btnVerMascotaNueva;

    private String nombreNueva;
    private String especieNueva;
    private String edadNueva;
    private String razaNueva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_mascotas);

        Button btnVerLuna = findViewById(R.id.btnVerLuna);
        Button btnVerMilo = findViewById(R.id.btnVerMilo);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascotaLista);

        cardMascotaNueva = findViewById(R.id.cardMascotaNueva);
        txtNombreMascotaNueva = findViewById(R.id.txtNombreMascotaNueva);
        txtEspecieMascotaNueva = findViewById(R.id.txtEspecieMascotaNueva);
        txtEdadMascotaNueva = findViewById(R.id.txtEdadMascotaNueva);
        txtRazaMascotaNueva = findViewById(R.id.txtRazaMascotaNueva);
        btnVerMascotaNueva = findViewById(R.id.btnVerMascotaNueva);

        btnVerLuna.setOnClickListener(v -> abrirDetalle(
                getString(R.string.mascota_luna),
                getString(R.string.luna_especie_valor),
                getString(R.string.luna_edad_valor),
                ""
        ));

        btnVerMilo.setOnClickListener(v -> abrirDetalle(
                getString(R.string.mascota_milo),
                getString(R.string.milo_especie_valor),
                getString(R.string.milo_edad_valor),
                ""
        ));

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MisMascotasActivity.this, RegistrarMascotaActivity.class);
            startActivity(intent);
        });

        leerMascotaRecibida();

        btnVerMascotaNueva.setOnClickListener(v -> {
            if (nombreNueva != null && especieNueva != null && edadNueva != null) {
                abrirDetalle(nombreNueva, especieNueva, edadNueva, razaNueva);
            }
        });
    }

    private void leerMascotaRecibida() {
        Intent intent = getIntent();
        nombreNueva = intent.getStringExtra("nombreMascota");
        especieNueva = intent.getStringExtra("especieMascota");
        edadNueva = intent.getStringExtra("edadMascota");
        razaNueva = intent.getStringExtra("razaMascota");

        if (nombreNueva != null && especieNueva != null && edadNueva != null) {
            cardMascotaNueva.setVisibility(View.VISIBLE);
            txtNombreMascotaNueva.setText(nombreNueva);
            txtEspecieMascotaNueva.setText(getString(R.string.formato_especie, especieNueva));
            txtEdadMascotaNueva.setText(getString(R.string.formato_edad, edadNueva));

            if (razaNueva != null && !razaNueva.isEmpty()) {
                txtRazaMascotaNueva.setText(getString(R.string.formato_raza, razaNueva));
                txtRazaMascotaNueva.setVisibility(View.VISIBLE);
            } else {
                txtRazaMascotaNueva.setVisibility(View.GONE);
            }
        }
    }

    private void abrirDetalle(String nombre, String especie, String edad, String raza) {
        Intent intent = new Intent(MisMascotasActivity.this, DetalleMascotaActivity.class);
        intent.putExtra("nombreMascota", nombre);
        intent.putExtra("especieMascota", especie);
        intent.putExtra("edadMascota", edad);
        intent.putExtra("razaMascota", raza == null ? "" : raza);
        startActivity(intent);
    }
}
