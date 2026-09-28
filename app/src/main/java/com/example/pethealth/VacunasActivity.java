package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class VacunasActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout contenedorVacunas;
    private TextView txtVacunasVacioLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas);

        databaseHelper = new DatabaseHelper(this);
        contenedorVacunas = findViewById(R.id.contenedorVacunas);
        txtVacunasVacioLista = findViewById(R.id.txtVacunasVacioLista);

        Button btnVolver = findViewById(R.id.btnVolverInicioVacunas);
        Button btnRegistrarVacuna = findViewById(R.id.btnRegistrarVacuna);

        btnVolver.setOnClickListener(v -> finish());
        btnRegistrarVacuna.setOnClickListener(v -> {
            Intent intent = new Intent(VacunasActivity.this, RegistrarVacunaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVacunas();
    }

    private void cargarVacunas() {
        contenedorVacunas.removeAllViews();
        List<Vacuna> vacunas = databaseHelper.obtenerVacunas();

        int vacunasMostradas = 0;
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Vacuna vacuna : vacunas) {
            Mascota mascota = databaseHelper.obtenerMascotaPorId(vacuna.getMascotaId());
            if (mascota == null) {
                continue;
            }

            View tarjeta = inflater.inflate(R.layout.item_vacuna, contenedorVacunas, false);
            TextView txtMascota = tarjeta.findViewById(R.id.txtItemVacunaMascota);
            TextView txtNombre = tarjeta.findViewById(R.id.txtItemVacunaNombre);
            TextView txtFecha = tarjeta.findViewById(R.id.txtItemVacunaFecha);
            TextView txtEstado = tarjeta.findViewById(R.id.txtItemVacunaEstado);
            Button btnVerMascota = tarjeta.findViewById(R.id.btnItemVacunaVerMascota);

            txtMascota.setText(mascota.getNombre());
            txtNombre.setText(getString(R.string.formato_vacuna, vacuna.getNombre()));
            txtFecha.setText(getString(R.string.formato_fecha_vacuna, vacuna.getFecha()));
            txtEstado.setText(getString(R.string.formato_estado_vacuna, vacuna.getEstado()));

            btnVerMascota.setOnClickListener(v -> abrirDetalleMascota(vacuna.getMascotaId()));
            contenedorVacunas.addView(tarjeta);
            vacunasMostradas++;
        }

        boolean vacio = vacunasMostradas == 0;
        txtVacunasVacioLista.setVisibility(vacio ? View.VISIBLE : View.GONE);
        contenedorVacunas.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    private void abrirDetalleMascota(long mascotaId) {
        Intent intent = new Intent(VacunasActivity.this, DetalleMascotaActivity.class);
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
