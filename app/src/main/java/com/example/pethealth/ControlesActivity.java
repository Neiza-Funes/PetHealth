package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ControlesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);

        Button btnRegistrarControl = findViewById(R.id.btnRegistrarControl);
        LinearLayout cardControlNuevo = findViewById(R.id.cardControlNuevo);
        TextView txtControlMascotaNueva = findViewById(R.id.txtControlMascotaNueva);
        TextView txtControlTipoNuevo = findViewById(R.id.txtControlTipoNuevo);
        TextView txtControlFechaNueva = findViewById(R.id.txtControlFechaNueva);
        TextView txtControlObservacionNueva = findViewById(R.id.txtControlObservacionNueva);

        btnRegistrarControl.setOnClickListener(v -> {
            Intent intent = new Intent(ControlesActivity.this, RegistrarControlActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        if (intent.hasExtra("mascotaControl")) {
            String mascota = intent.getStringExtra("mascotaControl");
            String tipo = intent.getStringExtra("tipoControl");
            String fecha = intent.getStringExtra("fechaControl");
            String observacion = intent.getStringExtra("observacionControl");

            if (mascota != null && tipo != null && fecha != null) {
                txtControlMascotaNueva.setText(mascota);
                txtControlTipoNuevo.setText(getString(R.string.formato_tipo_control, tipo));
                txtControlFechaNueva.setText(getString(R.string.formato_fecha_control, fecha));

                if (observacion != null && !observacion.isEmpty()) {
                    txtControlObservacionNueva.setText(
                            getString(R.string.formato_observacion_control, observacion));
                    txtControlObservacionNueva.setVisibility(View.VISIBLE);
                } else {
                    txtControlObservacionNueva.setVisibility(View.GONE);
                }

                cardControlNuevo.setVisibility(View.VISIBLE);
            }
        }
    }
}
