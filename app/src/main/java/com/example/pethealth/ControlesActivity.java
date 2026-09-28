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

public class ControlesActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout contenedorControles;
    private TextView txtSinProximosControles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);

        databaseHelper = new DatabaseHelper(this);
        contenedorControles = findViewById(R.id.contenedorControles);
        txtSinProximosControles = findViewById(R.id.txtSinProximosControles);

        Button btnVolver = findViewById(R.id.btnVolverInicioControles);
        Button btnRegistrarControl = findViewById(R.id.btnRegistrarControl);

        btnVolver.setOnClickListener(v -> finish());
        btnRegistrarControl.setOnClickListener(v -> {
            Intent intent = new Intent(ControlesActivity.this, RegistrarControlActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarControles();
    }

    private void cargarControles() {
        contenedorControles.removeAllViews();
        List<Control> controles = databaseHelper.obtenerProximosControles();

        int controlesMostrados = 0;
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Control control : controles) {
            Mascota mascota = databaseHelper.obtenerMascotaPorId(control.getMascotaId());
            if (mascota == null) {
                continue;
            }

            View tarjeta = inflater.inflate(R.layout.item_control, contenedorControles, false);
            TextView txtMascota = tarjeta.findViewById(R.id.txtItemControlMascota);
            TextView txtTipo = tarjeta.findViewById(R.id.txtItemControlTipo);
            TextView txtFecha = tarjeta.findViewById(R.id.txtItemControlFecha);
            TextView txtEstado = tarjeta.findViewById(R.id.txtItemControlEstado);
            TextView txtObservacion = tarjeta.findViewById(R.id.txtItemControlObservacion);
            Button btnVerMascota = tarjeta.findViewById(R.id.btnItemControlVerMascota);

            txtMascota.setText(mascota.getNombre());
            txtTipo.setText(getString(R.string.formato_tipo_control, control.getTipo()));
            txtFecha.setText(getString(R.string.formato_fecha_control, control.getFecha()));
            txtEstado.setText(getString(R.string.formato_estado_control, control.getEstado()));

            if (control.getObservacion().isEmpty()) {
                txtObservacion.setVisibility(View.GONE);
            } else {
                txtObservacion.setText(
                        getString(R.string.formato_observacion_control, control.getObservacion()));
                txtObservacion.setVisibility(View.VISIBLE);
            }

            btnVerMascota.setOnClickListener(v -> abrirDetalleMascota(control.getMascotaId()));
            contenedorControles.addView(tarjeta);
            controlesMostrados++;
        }

        boolean vacio = controlesMostrados == 0;
        txtSinProximosControles.setVisibility(vacio ? View.VISIBLE : View.GONE);
        contenedorControles.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    private void abrirDetalleMascota(long mascotaId) {
        Intent intent = new Intent(ControlesActivity.this, DetalleMascotaActivity.class);
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
