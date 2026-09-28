package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MisMascotasActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout contenedorMascotas;
    private TextView txtSinMascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_mascotas);

        databaseHelper = new DatabaseHelper(this);
        contenedorMascotas = findViewById(R.id.contenedorMascotas);
        txtSinMascotas = findViewById(R.id.txtSinMascotas);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascotaLista);

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MisMascotasActivity.this, RegistrarMascotaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMascotas();
    }

    private void cargarMascotas() {
        contenedorMascotas.removeAllViews();

        List<Mascota> mascotas = databaseHelper.obtenerMascotas();
        txtSinMascotas.setVisibility(mascotas.isEmpty() ? View.VISIBLE : View.GONE);

        for (Mascota mascota : mascotas) {
            View tarjeta = getLayoutInflater().inflate(
                    R.layout.item_mascota,
                    contenedorMascotas,
                    false
            );

            TextView txtNombre = tarjeta.findViewById(R.id.txtItemNombreMascota);
            TextView txtEspecie = tarjeta.findViewById(R.id.txtItemEspecieMascota);
            TextView txtEdad = tarjeta.findViewById(R.id.txtItemEdadMascota);
            TextView txtRaza = tarjeta.findViewById(R.id.txtItemRazaMascota);
            Button btnVerDetalles = tarjeta.findViewById(R.id.btnItemVerMascota);

            txtNombre.setText(mascota.getNombre());
            txtEspecie.setText(getString(R.string.formato_especie, mascota.getEspecie()));
            txtEdad.setText(getString(R.string.formato_edad, String.valueOf(mascota.getEdad())));

            if (!mascota.getRaza().isEmpty()) {
                txtRaza.setText(getString(R.string.formato_raza, mascota.getRaza()));
                txtRaza.setVisibility(View.VISIBLE);
            } else {
                txtRaza.setVisibility(View.GONE);
            }

            btnVerDetalles.setOnClickListener(v -> abrirDetalle(mascota.getId()));
            contenedorMascotas.addView(tarjeta);
        }
    }

    private void abrirDetalle(long mascotaId) {
        Intent intent = new Intent(MisMascotasActivity.this, DetalleMascotaActivity.class);
        intent.putExtra(DetalleMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
