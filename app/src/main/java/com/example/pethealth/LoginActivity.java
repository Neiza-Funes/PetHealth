package com.example.pethealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText edtCorreo;
    private EditText edtPassword;
    private TextView txtMostrarPassword;
    private boolean passwordVisible = false;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        sessionManager.migrarSesionAntiguaSiEsNecesario(databaseHelper);

        if (sessionManager.haySesionActiva() && sessionManager.obtenerUsuarioId() > 0) {
            abrirInicioYLimpiaPila();
            return;
        }

        setContentView(R.layout.activity_login);

        edtCorreo = findViewById(R.id.edtCorreo);
        edtPassword = findViewById(R.id.edtPassword);
        txtMostrarPassword = findViewById(R.id.txtMostrarPassword);
        TextView txtOlvidePassword = findViewById(R.id.txtOlvidePassword);
        TextView txtIrRegistro = findViewById(R.id.txtIrRegistro);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        txtMostrarPassword.setOnClickListener(v -> cambiarVisibilidadPassword());

        txtOlvidePassword.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this,
                        R.string.toast_recuperar,
                        Toast.LENGTH_SHORT).show());

        txtIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        btnIniciarSesion.setOnClickListener(v -> validarLogin());
    }

    private void cambiarVisibilidadPassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            edtPassword.setTransformationMethod(null);
            txtMostrarPassword.setText(R.string.ocultar_password);
        } else {
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            txtMostrarPassword.setText(R.string.mostrar_password);
        }

        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void validarLogin() {
        String correo = edtCorreo.getText().toString().trim();
        String password = edtPassword.getText().toString();

        edtCorreo.setError(null);
        edtPassword.setError(null);

        if (correo.isEmpty()) {
            edtCorreo.setError(getString(R.string.error_login_correo_vacio));
            edtCorreo.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtCorreo.setError(getString(R.string.error_login_correo_invalido));
            edtCorreo.requestFocus();
            return;
        }

        if (password.trim().isEmpty()) {
            edtPassword.setError(getString(R.string.error_login_password_vacio));
            edtPassword.requestFocus();
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        Usuario usuario = databaseHelper.validarUsuario(correo, passwordHash);

        if (usuario == null) {
            Toast.makeText(this, R.string.error_credenciales_incorrectas, Toast.LENGTH_SHORT).show();
            edtPassword.setText("");
            edtPassword.requestFocus();
            return;
        }

        sessionManager.iniciarSesion(usuario);
        Toast.makeText(this, R.string.toast_login_ok, Toast.LENGTH_SHORT).show();
        abrirInicioYLimpiaPila();
    }

    private void abrirInicioYLimpiaPila() {
        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
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
