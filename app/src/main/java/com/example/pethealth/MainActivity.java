package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        sessionManager.migrarSesionAntiguaSiEsNecesario(databaseHelper);

        if (sessionManager.haySesionActiva() && sessionManager.obtenerUsuarioId() > 0) {
            databaseHelper.close();
            abrirInicioYLimpiaPila();
            return;
        }
        databaseHelper.close();

        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getColor(R.color.rosado_claro));

        Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void abrirInicioYLimpiaPila() {
        Intent intent = new Intent(MainActivity.this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
