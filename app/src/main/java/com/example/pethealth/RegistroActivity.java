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
    private EditText edtPassword;
    private EditText edtConfirmarPassword;
    private TextView txtMostrarPassword;
    private boolean passwordVisible = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        databaseHelper = new DatabaseHelper(this);

        edtNombre = findViewById(R.id.edtNombre);
        edtCorreo = findViewById(R.id.edtCorreoRegistro);
        edtPassword = findViewById(R.id.edtPasswordRegistro);
        edtConfirmarPassword = findViewById(R.id.edtConfirmarPassword);
        txtMostrarPassword = findViewById(R.id.txtMostrarPasswordRegistro);
        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        TextView txtIrLogin = findViewById(R.id.txtIrLogin);

        txtMostrarPassword.setOnClickListener(v -> cambiarVisibilidadPassword());
        btnCrearCuenta.setOnClickListener(v -> registrarUsuario());
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

    private void registrarUsuario() {
        String nombre = edtNombre.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmarPassword = edtConfirmarPassword.getText().toString();

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

        if (password.trim().isEmpty()) {
            edtPassword.setError(getString(R.string.error_password_vacio));
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError(getString(R.string.error_password_corta));
            edtPassword.requestFocus();
            return;
        }

        if (confirmarPassword.trim().isEmpty()) {
            edtConfirmarPassword.setError(getString(R.string.error_confirmar_password));
            edtConfirmarPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmarPassword)) {
            edtConfirmarPassword.setError(getString(R.string.error_password_no_coincide));
            edtConfirmarPassword.requestFocus();
            return;
        }

        if (databaseHelper.existeUsuarioPorCorreo(correo)) {
            edtCorreo.setError(getString(R.string.error_correo_ya_registrado));
            edtCorreo.requestFocus();
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        long usuarioId = databaseHelper.insertarUsuario(nombre, correo, passwordHash);

        if (usuarioId == -1) {
            Toast.makeText(this, R.string.toast_error_registro_usuario, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_registro_ok, Toast.LENGTH_SHORT).show();
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
