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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String password = edtPassword.getText().toString().trim();

        if (correo.isEmpty()) {
            edtCorreo.setError(getString(R.string.error_correo_vacio));
            edtCorreo.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtCorreo.setError(getString(R.string.error_correo_invalido));
            edtCorreo.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError(getString(R.string.error_password_vacio));
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError(getString(R.string.error_password_corta));
            edtPassword.requestFocus();
            return;
        }

        Toast.makeText(this, R.string.toast_login_ok, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
        startActivity(intent);
    }
}
