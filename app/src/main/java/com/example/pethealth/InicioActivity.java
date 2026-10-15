package com.example.pethealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InicioActivity extends Activity {

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        sessionManager.migrarSesionAntiguaSiEsNecesario(databaseHelper);

        if (!sessionManager.haySesionActiva() || sessionManager.obtenerUsuarioId() <= 0) {
            abrirLoginYLimpiaPila();
            return;
        }

        setContentView(R.layout.activity_inicio);

        TextView txtCorreoSesion = findViewById(R.id.txtCorreoSesion);
        Button btnMisMascotas = findViewById(R.id.btnMisMascotas);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascota);
        Button btnProximosControles = findViewById(R.id.btnProximosControles);
        Button btnVacunas = findViewById(R.id.btnVacunas);
        Button btnDesparasitacion = findViewById(R.id.btnDesparasitacion);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String nombre = sessionManager.obtenerNombreUsuario();
        String correo = sessionManager.obtenerCorreoUsuario();
        if ((nombre == null || nombre.trim().isEmpty()) && (correo == null || correo.trim().isEmpty())) {
            txtCorreoSesion.setVisibility(View.GONE);
        } else {
            txtCorreoSesion.setText(getString(
                    R.string.sesion_iniciada_usuario,
                    nombre == null ? "" : nombre,
                    correo == null ? "" : correo
            ));
            txtCorreoSesion.setVisibility(View.VISIBLE);
        }

        btnMisMascotas.setOnClickListener(v ->
                startActivity(new Intent(InicioActivity.this, MisMascotasActivity.class)));

        btnRegistrarMascota.setOnClickListener(v ->
                startActivity(new Intent(InicioActivity.this, RegistrarMascotaActivity.class)));

        btnProximosControles.setOnClickListener(v ->
                startActivity(new Intent(InicioActivity.this, ControlesActivity.class)));

        btnVacunas.setOnClickListener(v ->
                startActivity(new Intent(InicioActivity.this, VacunasActivity.class)));

        btnDesparasitacion.setOnClickListener(v ->
                startActivity(new Intent(InicioActivity.this, DesparasitacionActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> mostrarConfirmacionCerrarSesion());
    }

    private void mostrarConfirmacionCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cerrar_sesion)
                .setMessage(R.string.confirmar_cerrar_sesion)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.cerrar_sesion, (dialog, which) -> {
                    sessionManager.cerrarSesion();
                    abrirLoginYLimpiaPila();
                })
                .show();
    }

    private void abrirLoginYLimpiaPila() {
        Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
