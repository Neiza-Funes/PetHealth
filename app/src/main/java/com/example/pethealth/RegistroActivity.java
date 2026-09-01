package com.example.pethealth;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroActivity extends Activity {

    private EditText edtNombre;
    private EditText edtCorreo;
    private EditText edtTelefono;
    private EditText edtPassword;
    private EditText edtConfirmarPassword;
    private TextView txtMostrarPassword;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNombre = findViewById(R.id.edtNombre);
        edtCorreo = findViewById(R.id.edtCorreoRegistro);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtPassword = findViewById(R.id.edtPasswordRegistro);
        edtConfirmarPassword = findViewById(R.id.edtConfirmarPassword);
        txtMostrarPassword = findViewById(R.id.txtMostrarPasswordRegistro);
        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        TextView txtIrLogin = findViewById(R.id.txtIrLogin);

        txtMostrarPassword.setOnClickListener(v -> cambiarVisibilidadPassword());
        btnCrearCuenta.setOnClickListener(v -> validarRegistro());
        txtIrLogin.setOnClickListener(v -> finish());
    }

    private void cambiarVisibilidadPassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            edtPassword.setTransformationMethod(null);
            edtConfirmarPassword.setTransformationMethod(null);
            txtMostrarPassword.setText(R.string.ocultar_password);
        } else {
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edtConfirmarPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            txtMostrarPassword.setText(R.string.mostrar_password);
        }

        edtPassword.setSelection(edtPassword.getText().length());
        edtConfirmarPassword.setSelection(edtConfirmarPassword.getText().length());
    }

    private void validarRegistro() {
        String nombre = edtNombre.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmarPassword = edtConfirmarPassword.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombre.setError(getString(R.string.error_nombre_vacio));
            edtNombre.requestFocus();
            return;
        }

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

        if (telefono.isEmpty()) {
            edtTelefono.setError(getString(R.string.error_telefono_vacio));
            edtTelefono.requestFocus();
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

        if (confirmarPassword.isEmpty()) {
            edtConfirmarPassword.setError(getString(R.string.error_confirmar_password));
            edtConfirmarPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmarPassword)) {
            edtConfirmarPassword.setError(getString(R.string.error_password_no_coincide));
            edtConfirmarPassword.requestFocus();
            return;
        }

        Toast.makeText(this, R.string.toast_registro_ok, Toast.LENGTH_SHORT).show();
        finish();
    }
}
