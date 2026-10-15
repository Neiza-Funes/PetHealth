package com.example.pethealth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Maneja únicamente la sesión local de PetHealth mediante SharedPreferences.
 * No guarda contraseñas ni modifica la base de datos SQLite.
 */
public class SessionManager {

    private static final String PREFS_SESION = "pethealth_sesion";
    private static final String KEY_SESION_INICIADA = "sesion_iniciada";
    private static final String KEY_CORREO_USUARIO = "correo_usuario";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_SESION, Context.MODE_PRIVATE);
    }

    public void iniciarSesion(String correo) {
        preferences.edit()
                .putBoolean(KEY_SESION_INICIADA, true)
                .putString(KEY_CORREO_USUARIO, correo == null ? "" : correo.trim())
                .apply();
    }

    public boolean haySesionActiva() {
        return preferences.getBoolean(KEY_SESION_INICIADA, false);
    }

    public String obtenerCorreoUsuario() {
        return preferences.getString(KEY_CORREO_USUARIO, "");
    }

    public void cerrarSesion() {
        preferences.edit()
                .remove(KEY_SESION_INICIADA)
                .remove(KEY_CORREO_USUARIO)
                .apply();
    }
}
