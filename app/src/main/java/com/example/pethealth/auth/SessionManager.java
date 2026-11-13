package com.example.pethealth.auth;

import com.example.pethealth.database.DatabaseHelper;
import com.example.pethealth.model.Usuario;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Maneja únicamente los datos mínimos de la sesión local mediante SharedPreferences.
 * Nunca guarda contraseñas ni hashes de contraseña.
 */
public class SessionManager {

    private static final String PREFS_SESION = "pethealth_sesion";
    private static final String KEY_SESION_INICIADA = "sesion_iniciada";
    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String KEY_NOMBRE_USUARIO = "nombre_usuario";
    private static final String KEY_CORREO_USUARIO = "correo_usuario";

    private final SharedPreferences preferences;

    /**
     * Inicializa el acceso a las preferencias privadas de sesión.
     *
     * @param context contexto utilizado para obtener SharedPreferences
     */
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_SESION, Context.MODE_PRIVATE);
    }

    /**
     * Guarda los datos mínimos necesarios para identificar una sesión activa.
     *
     * @param usuarioId identificador del usuario autenticado
     * @param nombre nombre del usuario
     * @param correo correo del usuario
     */
    public void iniciarSesion(long usuarioId, String nombre, String correo) {
        preferences.edit()
                .putBoolean(KEY_SESION_INICIADA, true)
                .putLong(KEY_USUARIO_ID, usuarioId)
                .putString(KEY_NOMBRE_USUARIO, nombre == null ? "" : nombre.trim())
                .putString(KEY_CORREO_USUARIO, correo == null ? "" : correo.trim())
                .apply();
    }

    public void iniciarSesion(Usuario usuario) {
        if (usuario == null) {
            return;
        }
        iniciarSesion(usuario.getId(), usuario.getNombre(), usuario.getCorreo());
    }

    public boolean haySesionActiva() {
        return preferences.getBoolean(KEY_SESION_INICIADA, false);
    }

    public long obtenerUsuarioId() {
        return preferences.getLong(KEY_USUARIO_ID, -1L);
    }

    public String obtenerNombreUsuario() {
        return preferences.getString(KEY_NOMBRE_USUARIO, "");
    }

    public String obtenerCorreoUsuario() {
        return preferences.getString(KEY_CORREO_USUARIO, "");
    }

    /**
     * Convierte una sesión antigua del Avance 20 (que solo tenía correo) a la
     * nueva sesión con usuario_id, sin cerrar al usuario ni perder sus datos.
     */
    /**
     * Completa una sesión antigua que todavía no posee usuario_id.
     *
     * @param databaseHelper acceso a los usuarios almacenados en SQLite
     * @return true si la sesión queda válida; false si debe cerrarse
     */
    public boolean migrarSesionAntiguaSiEsNecesario(DatabaseHelper databaseHelper) {
        if (!haySesionActiva()) {
            return false;
        }

        if (obtenerUsuarioId() > 0) {
            return true;
        }

        String correo = obtenerCorreoUsuario();
        if (correo == null || correo.trim().isEmpty()) {
            cerrarSesion();
            return false;
        }

        Usuario usuario = databaseHelper.obtenerUsuarioPorCorreo(correo);
        if (usuario == null) {
            cerrarSesion();
            return false;
        }

        iniciarSesion(usuario);
        return true;
    }

    /** Elimina de SharedPreferences los datos de la sesión actual. */
    public void cerrarSesion() {
        preferences.edit()
                .remove(KEY_SESION_INICIADA)
                .remove(KEY_USUARIO_ID)
                .remove(KEY_NOMBRE_USUARIO)
                .remove(KEY_CORREO_USUARIO)
                .apply();
    }
}
