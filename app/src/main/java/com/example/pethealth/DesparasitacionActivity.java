package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DesparasitacionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desparasitacion);

        Button btnRegistrarDesparasitacion = findViewById(R.id.btnRegistrarDesparasitacion);
        LinearLayout cardDesparasitacionNueva = findViewById(R.id.cardDesparasitacionNueva);
        TextView txtDesparasitacionMascotaNueva = findViewById(R.id.txtDesparasitacionMascotaNueva);
        TextView txtDesparasitacionProductoNuevo = findViewById(R.id.txtDesparasitacionProductoNuevo);
        TextView txtDesparasitacionFechaNueva = findViewById(R.id.txtDesparasitacionFechaNueva);
        TextView txtDesparasitacionProximaNueva = findViewById(R.id.txtDesparasitacionProximaNueva);
        TextView txtDesparasitacionObservacionNueva = findViewById(R.id.txtDesparasitacionObservacionNueva);

        btnRegistrarDesparasitacion.setOnClickListener(v -> {
            Intent intent = new Intent(DesparasitacionActivity.this, RegistrarDesparasitacionActivity.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        if (intent.hasExtra("mascotaDesparasitacion")) {
            String mascota = intent.getStringExtra("mascotaDesparasitacion");
            String producto = intent.getStringExtra("productoDesparasitacion");
            String fecha = intent.getStringExtra("fechaDesparasitacion");
            String proximaFecha = intent.getStringExtra("proximaDesparasitacion");
            String observacion = intent.getStringExtra("observacionDesparasitacion");

            if (mascota != null && producto != null && fecha != null && proximaFecha != null) {
                txtDesparasitacionMascotaNueva.setText(mascota);
                txtDesparasitacionProductoNuevo.setText(
                        getString(R.string.formato_producto_desparasitacion, producto));
                txtDesparasitacionFechaNueva.setText(
                        getString(R.string.formato_fecha_desparasitacion, fecha));
                txtDesparasitacionProximaNueva.setText(
                        getString(R.string.formato_proxima_desparasitacion, proximaFecha));

                if (observacion != null && !observacion.isEmpty()) {
                    txtDesparasitacionObservacionNueva.setText(
                            getString(R.string.formato_observacion_desparasitacion, observacion));
                    txtDesparasitacionObservacionNueva.setVisibility(View.VISIBLE);
                } else {
                    txtDesparasitacionObservacionNueva.setVisibility(View.GONE);
                }

                cardDesparasitacionNueva.setVisibility(View.VISIBLE);
            }
        }
    }
}
