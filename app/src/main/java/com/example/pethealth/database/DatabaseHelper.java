package com.example.pethealth.database;

import com.example.pethealth.auth.SessionManager;
import com.example.pethealth.database.PetHealthContract.ControlEntry;
import com.example.pethealth.database.PetHealthContract.DesparasitacionEntry;
import com.example.pethealth.database.PetHealthContract.MascotaEntry;
import com.example.pethealth.database.PetHealthContract.UsuarioEntry;
import com.example.pethealth.database.PetHealthContract.VacunaEntry;
import com.example.pethealth.model.Control;
import com.example.pethealth.model.Desparasitacion;
import com.example.pethealth.model.Mascota;
import com.example.pethealth.model.Usuario;
import com.example.pethealth.model.Vacuna;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Administra la persistencia SQLite de PetHealth y concentra las operaciones principales
 * sobre usuarios, mascotas, controles, vacunas y desparasitaciones.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "pethealth.db";
    public static final int VERSION_BD = 6;

    public static final String NOMBRE_USUARIO_NEI = "Nei";
    public static final String CORREO_USUARIO_NEI = "nfunes@gmail.com";
    private static final String HASH_PASSWORD_USUARIO_NEI = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

    private static final String ESTADO_PENDIENTE = "Pendiente";
    private static final String ESTADO_APLICADA = "Aplicada";

    private final Context appContext;

    /**
     * Crea el helper utilizando la base de datos privada de la aplicación.
     *
     * @param context contexto de Android
     */
    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
        appContext = context.getApplicationContext();
    }

    @Override
    /** Crea el esquema inicial de PetHealth y sus datos académicos de ejemplo. */
    public void onCreate(SQLiteDatabase db) {
        crearTablaUsuarios(db);
        long neiId = asegurarUsuarioPredeterminado(db);

        crearTablaMascotas(db);

        long lunaId = insertarMascotaInicial(db, neiId, "Luna", "Perro", 3, "");
        long miloId = insertarMascotaInicial(db, neiId, "Milo", "Gato", 2, "");

        crearTablaControles(db);
        insertarControlInicial(db, lunaId, "Revisión general", "05/10/2026", ESTADO_PENDIENTE, "");
        insertarControlInicial(db, miloId, "Control preventivo", "12/10/2026", ESTADO_PENDIENTE, "");

        crearTablaVacunas(db);
        insertarVacunaInicial(db, lunaId, "Antirrábica", "15/09/2026", ESTADO_APLICADA);
        insertarVacunaInicial(db, miloId, "Triple felina", "28/09/2026", ESTADO_PENDIENTE);

        crearTablaDesparasitaciones(db);
    }

    @Override
    /**
     * Aplica migraciones progresivas sin eliminar las tablas existentes.
     *
     * @param db base de datos abierta
     * @param oldVersion versión instalada
     * @param newVersion nueva versión solicitada
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            crearTablaControles(db);
            insertarControlInicialSiCorresponde(
                    db, 1, "Revisión general", "05/10/2026", ESTADO_PENDIENTE, "");
            insertarControlInicialSiCorresponde(
                    db, 2, "Control preventivo", "12/10/2026", ESTADO_PENDIENTE, "");
        }

        if (oldVersion < 3) {
            crearTablaVacunas(db);
            insertarVacunaInicialSiCorresponde(
                    db, 1, "Antirrábica", "15/09/2026", ESTADO_APLICADA);
            insertarVacunaInicialSiCorresponde(
                    db, 2, "Triple felina", "28/09/2026", ESTADO_PENDIENTE);
        }

        if (oldVersion < 4) {
            asegurarColumnaFotoMascota(db);
        }

        if (oldVersion < 5) {
            asegurarEstructuraUsuariosYPropiedad(db);
        }

        if (oldVersion < 6) {
            crearTablaDesparasitaciones(db);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        asegurarColumnaFotoMascota(db);

        // Mantiene disponible la estructura de usuarios y la propiedad de mascotas.
        asegurarEstructuraUsuariosYPropiedad(db);
    }

    private void crearTablaUsuarios(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UsuarioEntry.TABLE_NAME + " (" +
                UsuarioEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UsuarioEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                UsuarioEntry.COLUMN_CORREO + " TEXT NOT NULL UNIQUE, " +
                UsuarioEntry.COLUMN_PASSWORD_HASH + " TEXT NOT NULL" +
                ")");
    }

    private void asegurarEstructuraUsuariosYPropiedad(SQLiteDatabase db) {
        crearTablaUsuarios(db);
        long neiId = asegurarUsuarioPredeterminado(db);

        if (!existeColumna(db, MascotaEntry.TABLE_NAME, MascotaEntry.COLUMN_USUARIO_ID)) {
            db.execSQL("ALTER TABLE " + MascotaEntry.TABLE_NAME +
                    " ADD COLUMN " + MascotaEntry.COLUMN_USUARIO_ID + " INTEGER");
        }

        if (neiId > 0) {
            ContentValues values = new ContentValues();
            values.put(MascotaEntry.COLUMN_USUARIO_ID, neiId);
            db.update(
                    MascotaEntry.TABLE_NAME,
                    values,
                    MascotaEntry.COLUMN_USUARIO_ID + " IS NULL OR " + MascotaEntry.COLUMN_USUARIO_ID + " <= 0",
                    null
            );
        }
    }

    private long asegurarUsuarioPredeterminado(SQLiteDatabase db) {
        String correo = normalizarCorreo(CORREO_USUARIO_NEI);
        Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry.COLUMN_ID},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_ID));
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(UsuarioEntry.COLUMN_NOMBRE, NOMBRE_USUARIO_NEI);
        values.put(UsuarioEntry.COLUMN_CORREO, correo);
        values.put(UsuarioEntry.COLUMN_PASSWORD_HASH, HASH_PASSWORD_USUARIO_NEI);
        return db.insert(UsuarioEntry.TABLE_NAME, null, values);
    }

    private void asegurarColumnaFotoMascota(SQLiteDatabase db) {
        if (!existeTabla(db, MascotaEntry.TABLE_NAME)) {
            return;
        }

        if (!existeColumna(db, MascotaEntry.TABLE_NAME, MascotaEntry.COLUMN_FOTO_PATH)) {
            db.execSQL("ALTER TABLE " + MascotaEntry.TABLE_NAME +
                    " ADD COLUMN " + MascotaEntry.COLUMN_FOTO_PATH + " TEXT");
        }
    }

    private boolean existeTabla(SQLiteDatabase db, String tabla) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tabla}
        );
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    private boolean existeColumna(SQLiteDatabase db, String tabla, String columna) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tabla + ")", null);

        try {
            int indiceNombre = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                if (indiceNombre >= 0 && columna.equals(cursor.getString(indiceNombre))) {
                    return true;
                }
            }
        } finally {
            cursor.close();
        }

        return false;
    }

    private void crearTablaMascotas(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + MascotaEntry.TABLE_NAME + " (" +
                MascotaEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MascotaEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL, " +
                MascotaEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                MascotaEntry.COLUMN_ESPECIE + " TEXT NOT NULL, " +
                MascotaEntry.COLUMN_EDAD + " INTEGER NOT NULL, " +
                MascotaEntry.COLUMN_RAZA + " TEXT, " +
                MascotaEntry.COLUMN_FOTO_PATH + " TEXT" +
                ")";

        db.execSQL(crearTabla);
    }

    private void crearTablaControles(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE IF NOT EXISTS " + ControlEntry.TABLE_NAME + " (" +
                ControlEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ControlEntry.COLUMN_MASCOTA_ID + " INTEGER NOT NULL, " +
                ControlEntry.COLUMN_TIPO + " TEXT NOT NULL, " +
                ControlEntry.COLUMN_FECHA + " TEXT NOT NULL, " +
                ControlEntry.COLUMN_ESTADO + " TEXT NOT NULL, " +
                ControlEntry.COLUMN_OBSERVACION + " TEXT" +
                ")";

        db.execSQL(crearTabla);
    }

    private void crearTablaVacunas(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE IF NOT EXISTS " + VacunaEntry.TABLE_NAME + " (" +
                VacunaEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VacunaEntry.COLUMN_MASCOTA_ID + " INTEGER NOT NULL, " +
                VacunaEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                VacunaEntry.COLUMN_FECHA + " TEXT NOT NULL, " +
                VacunaEntry.COLUMN_ESTADO + " TEXT NOT NULL" +
                ")";

        db.execSQL(crearTabla);
    }

    private void crearTablaDesparasitaciones(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE IF NOT EXISTS " + DesparasitacionEntry.TABLE_NAME + " (" +
                DesparasitacionEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DesparasitacionEntry.COLUMN_MASCOTA_ID + " INTEGER NOT NULL, " +
                DesparasitacionEntry.COLUMN_PRODUCTO + " TEXT NOT NULL, " +
                DesparasitacionEntry.COLUMN_FECHA + " TEXT NOT NULL, " +
                DesparasitacionEntry.COLUMN_PROXIMA_FECHA + " TEXT NOT NULL, " +
                DesparasitacionEntry.COLUMN_OBSERVACION + " TEXT" +
                ")";

        db.execSQL(crearTabla);
    }

    private long insertarMascotaInicial(
            SQLiteDatabase db,
            long usuarioId,
            String nombre,
            String especie,
            int edad,
            String raza) {

        if (usuarioId <= 0) {
            return -1;
        }

        ContentValues values = crearValoresMascota(usuarioId, nombre, especie, edad, raza);
        return db.insert(MascotaEntry.TABLE_NAME, null, values);
    }

    private void insertarControlInicial(
            SQLiteDatabase db,
            long mascotaId,
            String tipo,
            String fecha,
            String estado,
            String observacion) {

        if (mascotaId == -1) {
            return;
        }

        db.insert(
                ControlEntry.TABLE_NAME,
                null,
                crearValoresControl(mascotaId, tipo, fecha, estado, observacion)
        );
    }

    private void insertarControlInicialSiCorresponde(
            SQLiteDatabase db,
            long mascotaId,
            String tipo,
            String fecha,
            String estado,
            String observacion) {

        if (!existeMascotaSinFiltro(db, mascotaId) || existeControlInicial(db, mascotaId, tipo, fecha)) {
            return;
        }

        insertarControlInicial(db, mascotaId, tipo, fecha, estado, observacion);
    }

    private void insertarVacunaInicial(
            SQLiteDatabase db,
            long mascotaId,
            String nombre,
            String fecha,
            String estado) {

        if (mascotaId == -1) {
            return;
        }

        db.insert(
                VacunaEntry.TABLE_NAME,
                null,
                crearValoresVacuna(mascotaId, nombre, fecha, estado)
        );
    }

    private void insertarVacunaInicialSiCorresponde(
            SQLiteDatabase db,
            long mascotaId,
            String nombre,
            String fecha,
            String estado) {

        if (!existeMascotaSinFiltro(db, mascotaId) || existeVacunaInicial(db, mascotaId, nombre, fecha)) {
            return;
        }

        insertarVacunaInicial(db, mascotaId, nombre, fecha, estado);
    }

    private boolean existeMascotaSinFiltro(SQLiteDatabase db, long mascotaId) {
        Cursor cursor = db.query(
                MascotaEntry.TABLE_NAME,
                new String[]{MascotaEntry.COLUMN_ID},
                MascotaEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mascotaId)},
                null,
                null,
                null,
                "1"
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    private boolean existeControlInicial(SQLiteDatabase db, long mascotaId, String tipo, String fecha) {
        Cursor cursor = db.query(
                ControlEntry.TABLE_NAME,
                new String[]{ControlEntry.COLUMN_ID},
                ControlEntry.COLUMN_MASCOTA_ID + " = ? AND " +
                        ControlEntry.COLUMN_TIPO + " = ? AND " +
                        ControlEntry.COLUMN_FECHA + " = ?",
                new String[]{String.valueOf(mascotaId), tipo, fecha},
                null,
                null,
                null,
                "1"
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    private boolean existeVacunaInicial(SQLiteDatabase db, long mascotaId, String nombre, String fecha) {
        Cursor cursor = db.query(
                VacunaEntry.TABLE_NAME,
                new String[]{VacunaEntry.COLUMN_ID},
                VacunaEntry.COLUMN_MASCOTA_ID + " = ? AND " +
                        VacunaEntry.COLUMN_NOMBRE + " = ? AND " +
                        VacunaEntry.COLUMN_FECHA + " = ?",
                new String[]{String.valueOf(mascotaId), nombre, fecha},
                null,
                null,
                null,
                "1"
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    /**
     * Registra un usuario utilizando su contraseña previamente transformada a hash.
     *
     * @return identificador insertado o -1 si SQLite rechaza la operación
     */
    public long insertarUsuario(String nombre, String correo, String passwordHash) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UsuarioEntry.COLUMN_NOMBRE, nombre == null ? "" : nombre.trim());
        values.put(UsuarioEntry.COLUMN_CORREO, normalizarCorreo(correo));
        values.put(UsuarioEntry.COLUMN_PASSWORD_HASH, passwordHash == null ? "" : passwordHash);
        return db.insert(UsuarioEntry.TABLE_NAME, null, values);
    }

    public boolean existeUsuarioPorCorreo(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry.COLUMN_ID},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{normalizarCorreo(correo)},
                null,
                null,
                null,
                "1"
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    /**
     * Busca un usuario cuyas credenciales coincidan con correo y hash.
     *
     * @return usuario encontrado o null cuando las credenciales no coinciden
     */
    public Usuario validarUsuario(String correo, String passwordHash) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                null,
                UsuarioEntry.COLUMN_CORREO + " = ? AND " + UsuarioEntry.COLUMN_PASSWORD_HASH + " = ?",
                new String[]{normalizarCorreo(correo), passwordHash == null ? "" : passwordHash},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                return crearUsuarioDesdeCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public Usuario obtenerUsuarioPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                null,
                UsuarioEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                return crearUsuarioDesdeCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                null,
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{normalizarCorreo(correo)},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                return crearUsuarioDesdeCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public long insertarMascota(Mascota mascota) {
        return insertarMascota(mascota, obtenerUsuarioSesionId());
    }

    /**
     * Guarda una mascota asociada al usuario indicado.
     *
     * @param mascota datos de la mascota
     * @param usuarioId propietario de la mascota
     * @return identificador insertado o -1 si no puede registrarse
     */
    public long insertarMascota(Mascota mascota, long usuarioId) {
        if (mascota == null || usuarioId <= 0 || obtenerUsuarioPorId(usuarioId) == null) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = crearValoresMascota(
                usuarioId,
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getEdad(),
                mascota.getRaza()
        );

        long id = db.insert(MascotaEntry.TABLE_NAME, null, values);
        if (id != -1) {
            mascota.setId(id);
            mascota.setUsuarioId(usuarioId);
        }
        return id;
    }

    public List<Mascota> obtenerMascotas() {
        return obtenerMascotas(obtenerUsuarioSesionId());
    }

    /**
     * Obtiene las mascotas pertenecientes al usuario indicado.
     *
     * @param usuarioId usuario propietario
     * @return lista de mascotas asociadas
     */
    public List<Mascota> obtenerMascotas(long usuarioId) {
        List<Mascota> mascotas = new ArrayList<>();
        if (usuarioId <= 0) {
            return mascotas;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                MascotaEntry.TABLE_NAME,
                null,
                MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioId)},
                null,
                null,
                MascotaEntry.COLUMN_ID + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                mascotas.add(crearMascotaDesdeCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return mascotas;
    }

    public Mascota obtenerMascotaPorId(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                MascotaEntry.TABLE_NAME,
                null,
                MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                return crearMascotaDesdeCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public boolean actualizarMascota(long id, String nombre, String especie, int edad, String raza) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = crearValoresMascotaSinUsuario(nombre, especie, edad, raza);

        int filasActualizadas = db.update(
                MascotaEntry.TABLE_NAME,
                values,
                MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );

        return filasActualizadas > 0;
    }

    public boolean actualizarFotoMascota(long mascotaId, String fotoPath) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MascotaEntry.COLUMN_FOTO_PATH, fotoPath == null ? "" : fotoPath);

        int filasActualizadas = db.update(
                MascotaEntry.TABLE_NAME,
                values,
                MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(mascotaId), String.valueOf(usuarioId)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarFotoMascota(long mascotaId) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(MascotaEntry.COLUMN_FOTO_PATH);

        int filasActualizadas = db.update(
                MascotaEntry.TABLE_NAME,
                values,
                MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(mascotaId), String.valueOf(usuarioId)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarMascota(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        String fotoPath = obtenerFotoPath(db, id, usuarioId);
        if (!mascotaPerteneceAUsuario(db, id, usuarioId)) {
            return false;
        }

        boolean mascotaEliminada = false;
        db.beginTransaction();
        try {
            db.delete(
                    ControlEntry.TABLE_NAME,
                    ControlEntry.COLUMN_MASCOTA_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            db.delete(
                    VacunaEntry.TABLE_NAME,
                    VacunaEntry.COLUMN_MASCOTA_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            db.delete(
                    DesparasitacionEntry.TABLE_NAME,
                    DesparasitacionEntry.COLUMN_MASCOTA_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            int filasEliminadas = db.delete(
                    MascotaEntry.TABLE_NAME,
                    MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                    new String[]{String.valueOf(id), String.valueOf(usuarioId)}
            );

            mascotaEliminada = filasEliminadas > 0;
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (mascotaEliminada && fotoPath != null && !fotoPath.isEmpty()) {
            File archivoFoto = new File(fotoPath);
            if (archivoFoto.exists()) {
                archivoFoto.delete();
            }
        }

        return mascotaEliminada;
    }

    private String obtenerFotoPath(SQLiteDatabase db, long mascotaId, long usuarioId) {
        Cursor cursor = db.query(
                MascotaEntry.TABLE_NAME,
                new String[]{MascotaEntry.COLUMN_FOTO_PATH},
                MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(mascotaId), String.valueOf(usuarioId)},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_FOTO_PATH));
                return fotoPath == null ? "" : fotoPath;
            }
        } finally {
            cursor.close();
        }

        return "";
    }

    /** Guarda un control médico para una mascota perteneciente a la sesión actual. */
    public long insertarControl(Control control) {
        if (control == null) {
            return -1;
        }

        long usuarioId = obtenerUsuarioSesionId();
        SQLiteDatabase db = getWritableDatabase();
        if (!mascotaPerteneceAUsuario(db, control.getMascotaId(), usuarioId)) {
            return -1;
        }

        ContentValues values = crearValoresControl(
                control.getMascotaId(),
                control.getTipo(),
                control.getFecha(),
                control.getEstado(),
                control.getObservacion()
        );

        long id = db.insert(ControlEntry.TABLE_NAME, null, values);
        if (id != -1) {
            control.setId(id);
        }
        return id;
    }

    public List<Control> obtenerControles() {
        return consultarControles(null, null);
    }

    public List<Control> obtenerProximosControles() {
        return consultarControles(
                "c." + ControlEntry.COLUMN_ESTADO + " = ?",
                new String[]{ESTADO_PENDIENTE}
        );
    }

    public List<Control> obtenerControlesPorMascota(long mascotaId) {
        return consultarControles(
                "c." + ControlEntry.COLUMN_MASCOTA_ID + " = ?",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    public Control obtenerControlPorId(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.* FROM " + ControlEntry.TABLE_NAME + " c " +
                        "INNER JOIN " + MascotaEntry.TABLE_NAME + " m ON m." + MascotaEntry.COLUMN_ID + " = c." + ControlEntry.COLUMN_MASCOTA_ID + " " +
                        "WHERE c." + ControlEntry.COLUMN_ID + " = ? AND m." + MascotaEntry.COLUMN_USUARIO_ID + " = ? LIMIT 1",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );

        try {
            if (cursor.moveToFirst()) {
                return crearControlDesdeCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public boolean actualizarControl(
            long id,
            long mascotaId,
            String tipo,
            String fecha,
            String estado,
            String observacion) {

        long usuarioId = obtenerUsuarioSesionId();
        SQLiteDatabase db = getWritableDatabase();
        if (!mascotaPerteneceAUsuario(db, mascotaId, usuarioId)) {
            return false;
        }

        ContentValues values = crearValoresControl(mascotaId, tipo, fecha, estado, observacion);
        int filasActualizadas = db.update(
                ControlEntry.TABLE_NAME,
                values,
                ControlEntry.COLUMN_ID + " = ? AND " + ControlEntry.COLUMN_MASCOTA_ID +
                        " IN (SELECT " + MascotaEntry.COLUMN_ID + " FROM " + MascotaEntry.TABLE_NAME +
                        " WHERE " + MascotaEntry.COLUMN_USUARIO_ID + " = ?)",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarControl(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        int filasEliminadas = db.delete(
                ControlEntry.TABLE_NAME,
                ControlEntry.COLUMN_ID + " = ? AND " + ControlEntry.COLUMN_MASCOTA_ID +
                        " IN (SELECT " + MascotaEntry.COLUMN_ID + " FROM " + MascotaEntry.TABLE_NAME +
                        " WHERE " + MascotaEntry.COLUMN_USUARIO_ID + " = ?)",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );
        return filasEliminadas > 0;
    }

    private List<Control> consultarControles(String condicionExtra, String[] argumentosExtra) {
        List<Control> controles = new ArrayList<>();
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return controles;
        }

        List<String> argumentos = new ArrayList<>();
        argumentos.add(String.valueOf(usuarioId));
        if (argumentosExtra != null) {
            for (String argumento : argumentosExtra) {
                argumentos.add(argumento);
            }
        }

        String where = "m." + MascotaEntry.COLUMN_USUARIO_ID + " = ?";
        if (condicionExtra != null && !condicionExtra.trim().isEmpty()) {
            where += " AND " + condicionExtra;
        }

        SQLiteDatabase db = getReadableDatabase();
        String ordenFecha = crearOrdenFecha("c." + ControlEntry.COLUMN_FECHA) + ", c." + ControlEntry.COLUMN_ID + " ASC";
        Cursor cursor = db.rawQuery(
                "SELECT c.* FROM " + ControlEntry.TABLE_NAME + " c " +
                        "INNER JOIN " + MascotaEntry.TABLE_NAME + " m ON m." + MascotaEntry.COLUMN_ID + " = c." + ControlEntry.COLUMN_MASCOTA_ID + " " +
                        "WHERE " + where + " ORDER BY " + ordenFecha,
                argumentos.toArray(new String[0])
        );

        try {
            while (cursor.moveToNext()) {
                controles.add(crearControlDesdeCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return controles;
    }

    /** Guarda una vacuna para una mascota perteneciente a la sesión actual. */
    public long insertarVacuna(Vacuna vacuna) {
        if (vacuna == null) {
            return -1;
        }

        long usuarioId = obtenerUsuarioSesionId();
        SQLiteDatabase db = getWritableDatabase();
        if (!mascotaPerteneceAUsuario(db, vacuna.getMascotaId(), usuarioId)) {
            return -1;
        }

        ContentValues values = crearValoresVacuna(
                vacuna.getMascotaId(),
                vacuna.getNombre(),
                vacuna.getFecha(),
                vacuna.getEstado()
        );

        long id = db.insert(VacunaEntry.TABLE_NAME, null, values);
        if (id != -1) {
            vacuna.setId(id);
        }
        return id;
    }

    public List<Vacuna> obtenerVacunas() {
        return consultarVacunas(null, null);
    }

    public List<Vacuna> obtenerVacunasPorMascota(long mascotaId) {
        return consultarVacunas(
                "v." + VacunaEntry.COLUMN_MASCOTA_ID + " = ?",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    public Vacuna obtenerVacunaPorId(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT v.* FROM " + VacunaEntry.TABLE_NAME + " v " +
                        "INNER JOIN " + MascotaEntry.TABLE_NAME + " m ON m." + MascotaEntry.COLUMN_ID + " = v." + VacunaEntry.COLUMN_MASCOTA_ID + " " +
                        "WHERE v." + VacunaEntry.COLUMN_ID + " = ? AND m." + MascotaEntry.COLUMN_USUARIO_ID + " = ? LIMIT 1",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );

        try {
            if (cursor.moveToFirst()) {
                return crearVacunaDesdeCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public boolean actualizarVacuna(long id, long mascotaId, String nombre, String fecha, String estado) {
        long usuarioId = obtenerUsuarioSesionId();
        SQLiteDatabase db = getWritableDatabase();
        if (!mascotaPerteneceAUsuario(db, mascotaId, usuarioId)) {
            return false;
        }

        ContentValues values = crearValoresVacuna(mascotaId, nombre, fecha, estado);
        int filasActualizadas = db.update(
                VacunaEntry.TABLE_NAME,
                values,
                VacunaEntry.COLUMN_ID + " = ? AND " + VacunaEntry.COLUMN_MASCOTA_ID +
                        " IN (SELECT " + MascotaEntry.COLUMN_ID + " FROM " + MascotaEntry.TABLE_NAME +
                        " WHERE " + MascotaEntry.COLUMN_USUARIO_ID + " = ?)",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarVacuna(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        int filasEliminadas = db.delete(
                VacunaEntry.TABLE_NAME,
                VacunaEntry.COLUMN_ID + " = ? AND " + VacunaEntry.COLUMN_MASCOTA_ID +
                        " IN (SELECT " + MascotaEntry.COLUMN_ID + " FROM " + MascotaEntry.TABLE_NAME +
                        " WHERE " + MascotaEntry.COLUMN_USUARIO_ID + " = ?)",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );
        return filasEliminadas > 0;
    }

    private List<Vacuna> consultarVacunas(String condicionExtra, String[] argumentosExtra) {
        List<Vacuna> vacunas = new ArrayList<>();
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return vacunas;
        }

        List<String> argumentos = new ArrayList<>();
        argumentos.add(String.valueOf(usuarioId));
        if (argumentosExtra != null) {
            for (String argumento : argumentosExtra) {
                argumentos.add(argumento);
            }
        }

        String where = "m." + MascotaEntry.COLUMN_USUARIO_ID + " = ?";
        if (condicionExtra != null && !condicionExtra.trim().isEmpty()) {
            where += " AND " + condicionExtra;
        }

        SQLiteDatabase db = getReadableDatabase();
        String ordenFecha = crearOrdenFecha("v." + VacunaEntry.COLUMN_FECHA) + ", v." + VacunaEntry.COLUMN_ID + " ASC";
        Cursor cursor = db.rawQuery(
                "SELECT v.* FROM " + VacunaEntry.TABLE_NAME + " v " +
                        "INNER JOIN " + MascotaEntry.TABLE_NAME + " m ON m." + MascotaEntry.COLUMN_ID + " = v." + VacunaEntry.COLUMN_MASCOTA_ID + " " +
                        "WHERE " + where + " ORDER BY " + ordenFecha,
                argumentos.toArray(new String[0])
        );

        try {
            while (cursor.moveToNext()) {
                vacunas.add(crearVacunaDesdeCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return vacunas;
    }

    /** Guarda un registro de desparasitación asociado a una mascota válida. */
    public long insertarDesparasitacion(Desparasitacion desparasitacion) {
        if (desparasitacion == null) {
            return -1;
        }

        long usuarioId = obtenerUsuarioSesionId();
        SQLiteDatabase db = getWritableDatabase();
        if (!mascotaPerteneceAUsuario(db, desparasitacion.getMascotaId(), usuarioId)) {
            return -1;
        }

        ContentValues values = crearValoresDesparasitacion(
                desparasitacion.getMascotaId(),
                desparasitacion.getProducto(),
                desparasitacion.getFecha(),
                desparasitacion.getProximaFecha(),
                desparasitacion.getObservacion()
        );

        long id = db.insert(DesparasitacionEntry.TABLE_NAME, null, values);
        if (id != -1) {
            desparasitacion.setId(id);
        }
        return id;
    }

    public List<Desparasitacion> obtenerDesparasitaciones() {
        return consultarDesparasitaciones(null, null);
    }

    /**
     * Obtiene las desparasitaciones de una mascota visible para la sesión activa.
     *
     * @param mascotaId identificador de la mascota
     * @return registros de desparasitación asociados
     */
    public List<Desparasitacion> obtenerDesparasitacionesPorMascota(long mascotaId) {
        return consultarDesparasitaciones(
                "d." + DesparasitacionEntry.COLUMN_MASCOTA_ID + " = ?",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    public boolean eliminarDesparasitacion(long id) {
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        int filasEliminadas = db.delete(
                DesparasitacionEntry.TABLE_NAME,
                DesparasitacionEntry.COLUMN_ID + " = ? AND " + DesparasitacionEntry.COLUMN_MASCOTA_ID +
                        " IN (SELECT " + MascotaEntry.COLUMN_ID + " FROM " + MascotaEntry.TABLE_NAME +
                        " WHERE " + MascotaEntry.COLUMN_USUARIO_ID + " = ?)",
                new String[]{String.valueOf(id), String.valueOf(usuarioId)}
        );
        return filasEliminadas > 0;
    }

    private List<Desparasitacion> consultarDesparasitaciones(String condicionExtra, String[] argumentosExtra) {
        List<Desparasitacion> desparasitaciones = new ArrayList<>();
        long usuarioId = obtenerUsuarioSesionId();
        if (usuarioId <= 0) {
            return desparasitaciones;
        }

        List<String> argumentos = new ArrayList<>();
        argumentos.add(String.valueOf(usuarioId));
        if (argumentosExtra != null) {
            for (String argumento : argumentosExtra) {
                argumentos.add(argumento);
            }
        }

        String where = "m." + MascotaEntry.COLUMN_USUARIO_ID + " = ?";
        if (condicionExtra != null && !condicionExtra.trim().isEmpty()) {
            where += " AND " + condicionExtra;
        }

        SQLiteDatabase db = getReadableDatabase();
        String ordenFecha = crearOrdenFecha("d." + DesparasitacionEntry.COLUMN_FECHA) +
                ", d." + DesparasitacionEntry.COLUMN_ID + " ASC";
        Cursor cursor = db.rawQuery(
                "SELECT d.*, m." + MascotaEntry.COLUMN_NOMBRE + " AS mascota_nombre FROM " +
                        DesparasitacionEntry.TABLE_NAME + " d " +
                        "INNER JOIN " + MascotaEntry.TABLE_NAME + " m ON m." + MascotaEntry.COLUMN_ID +
                        " = d." + DesparasitacionEntry.COLUMN_MASCOTA_ID + " " +
                        "WHERE " + where + " ORDER BY " + ordenFecha,
                argumentos.toArray(new String[0])
        );

        try {
            while (cursor.moveToNext()) {
                desparasitaciones.add(crearDesparasitacionDesdeCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return desparasitaciones;
    }

    private boolean mascotaPerteneceAUsuario(SQLiteDatabase db, long mascotaId, long usuarioId) {
        if (mascotaId <= 0 || usuarioId <= 0) {
            return false;
        }

        Cursor cursor = db.query(
                MascotaEntry.TABLE_NAME,
                new String[]{MascotaEntry.COLUMN_ID},
                MascotaEntry.COLUMN_ID + " = ? AND " + MascotaEntry.COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(mascotaId), String.valueOf(usuarioId)},
                null,
                null,
                null,
                "1"
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    private long obtenerUsuarioSesionId() {
        return new SessionManager(appContext).obtenerUsuarioId();
    }

    private String crearOrdenFecha(String columnaFecha) {
        return "substr(" + columnaFecha + ",7,4) || " +
                "substr(" + columnaFecha + ",4,2) || " +
                "substr(" + columnaFecha + ",1,2) ASC";
    }

    private ContentValues crearValoresMascota(long usuarioId, String nombre, String especie, int edad, String raza) {
        ContentValues values = crearValoresMascotaSinUsuario(nombre, especie, edad, raza);
        values.put(MascotaEntry.COLUMN_USUARIO_ID, usuarioId);
        return values;
    }

    private ContentValues crearValoresMascotaSinUsuario(String nombre, String especie, int edad, String raza) {
        ContentValues values = new ContentValues();
        values.put(MascotaEntry.COLUMN_NOMBRE, nombre);
        values.put(MascotaEntry.COLUMN_ESPECIE, especie);
        values.put(MascotaEntry.COLUMN_EDAD, edad);
        values.put(MascotaEntry.COLUMN_RAZA, raza == null ? "" : raza);
        return values;
    }

    private ContentValues crearValoresControl(long mascotaId, String tipo, String fecha, String estado, String observacion) {
        ContentValues values = new ContentValues();
        values.put(ControlEntry.COLUMN_MASCOTA_ID, mascotaId);
        values.put(ControlEntry.COLUMN_TIPO, tipo);
        values.put(ControlEntry.COLUMN_FECHA, fecha);
        values.put(ControlEntry.COLUMN_ESTADO, estado);
        values.put(ControlEntry.COLUMN_OBSERVACION, observacion == null ? "" : observacion);
        return values;
    }

    private ContentValues crearValoresVacuna(long mascotaId, String nombre, String fecha, String estado) {
        ContentValues values = new ContentValues();
        values.put(VacunaEntry.COLUMN_MASCOTA_ID, mascotaId);
        values.put(VacunaEntry.COLUMN_NOMBRE, nombre);
        values.put(VacunaEntry.COLUMN_FECHA, fecha);
        values.put(VacunaEntry.COLUMN_ESTADO, estado);
        return values;
    }

    private ContentValues crearValoresDesparasitacion(long mascotaId, String producto, String fecha,
                                                        String proximaFecha, String observacion) {
        ContentValues values = new ContentValues();
        values.put(DesparasitacionEntry.COLUMN_MASCOTA_ID, mascotaId);
        values.put(DesparasitacionEntry.COLUMN_PRODUCTO, producto);
        values.put(DesparasitacionEntry.COLUMN_FECHA, fecha);
        values.put(DesparasitacionEntry.COLUMN_PROXIMA_FECHA, proximaFecha);
        values.put(DesparasitacionEntry.COLUMN_OBSERVACION, observacion == null ? "" : observacion);
        return values;
    }

    private Usuario crearUsuarioDesdeCursor(Cursor cursor) {
        return new Usuario(
                cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_CORREO)),
                cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_PASSWORD_HASH))
        );
    }

    private Mascota crearMascotaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_ID));
        long usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_USUARIO_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_NOMBRE));
        String especie = cursor.getString(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_ESPECIE));
        int edad = cursor.getInt(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_EDAD));
        String raza = cursor.getString(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_RAZA));
        String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(MascotaEntry.COLUMN_FOTO_PATH));

        return new Mascota(
                id,
                usuarioId,
                nombre,
                especie,
                edad,
                raza == null ? "" : raza,
                fotoPath == null ? "" : fotoPath
        );
    }

    private Control crearControlDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(ControlEntry.COLUMN_ID));
        long mascotaId = cursor.getLong(cursor.getColumnIndexOrThrow(ControlEntry.COLUMN_MASCOTA_ID));
        String tipo = cursor.getString(cursor.getColumnIndexOrThrow(ControlEntry.COLUMN_TIPO));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(ControlEntry.COLUMN_FECHA));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(ControlEntry.COLUMN_ESTADO));
        String observacion = cursor.getString(cursor.getColumnIndexOrThrow(ControlEntry.COLUMN_OBSERVACION));

        return new Control(id, mascotaId, tipo, fecha, estado, observacion == null ? "" : observacion);
    }

    private Vacuna crearVacunaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(VacunaEntry.COLUMN_ID));
        long mascotaId = cursor.getLong(cursor.getColumnIndexOrThrow(VacunaEntry.COLUMN_MASCOTA_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(VacunaEntry.COLUMN_NOMBRE));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(VacunaEntry.COLUMN_FECHA));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(VacunaEntry.COLUMN_ESTADO));

        return new Vacuna(id, mascotaId, nombre, fecha, estado);
    }

    private Desparasitacion crearDesparasitacionDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DesparasitacionEntry.COLUMN_ID));
        long mascotaId = cursor.getLong(cursor.getColumnIndexOrThrow(DesparasitacionEntry.COLUMN_MASCOTA_ID));
        String mascotaNombre = "";
        int indiceMascotaNombre = cursor.getColumnIndex("mascota_nombre");
        if (indiceMascotaNombre >= 0) {
            mascotaNombre = cursor.getString(indiceMascotaNombre);
        }
        String producto = cursor.getString(cursor.getColumnIndexOrThrow(DesparasitacionEntry.COLUMN_PRODUCTO));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DesparasitacionEntry.COLUMN_FECHA));
        String proximaFecha = cursor.getString(cursor.getColumnIndexOrThrow(DesparasitacionEntry.COLUMN_PROXIMA_FECHA));
        String observacion = cursor.getString(cursor.getColumnIndexOrThrow(DesparasitacionEntry.COLUMN_OBSERVACION));

        return new Desparasitacion(
                id,
                mascotaId,
                mascotaNombre,
                producto,
                fecha,
                proximaFecha,
                observacion == null ? "" : observacion
        );
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
