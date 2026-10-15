package com.example.pethealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "pethealth.db";
    public static final int VERSION_BD = 5;

    public static final String TABLA_USUARIOS = "usuarios";
    public static final String COL_USUARIO_ID = "id";
    public static final String COL_USUARIO_NOMBRE = "nombre";
    public static final String COL_USUARIO_CORREO = "correo";
    public static final String COL_USUARIO_PASSWORD_HASH = "password_hash";

    public static final String TABLA_MASCOTAS = "mascotas";
    public static final String COL_ID = "id";
    public static final String COL_MASCOTA_USUARIO_ID = "usuario_id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_ESPECIE = "especie";
    public static final String COL_EDAD = "edad";
    public static final String COL_RAZA = "raza";
    public static final String COL_FOTO_PATH = "foto_path";

    public static final String TABLA_CONTROLES = "controles";
    public static final String COL_CONTROL_ID = "id";
    public static final String COL_CONTROL_MASCOTA_ID = "mascota_id";
    public static final String COL_CONTROL_TIPO = "tipo";
    public static final String COL_CONTROL_FECHA = "fecha";
    public static final String COL_CONTROL_ESTADO = "estado";
    public static final String COL_CONTROL_OBSERVACION = "observacion";

    public static final String TABLA_VACUNAS = "vacunas";
    public static final String COL_VACUNA_ID = "id";
    public static final String COL_VACUNA_MASCOTA_ID = "mascota_id";
    public static final String COL_VACUNA_NOMBRE = "nombre";
    public static final String COL_VACUNA_FECHA = "fecha";
    public static final String COL_VACUNA_ESTADO = "estado";

    public static final String NOMBRE_USUARIO_NEI = "Nei";
    public static final String CORREO_USUARIO_NEI = "nfunes@gmail.com";
    private static final String HASH_PASSWORD_USUARIO_NEI = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

    private static final String ESTADO_PENDIENTE = "Pendiente";
    private static final String ESTADO_APLICADA = "Aplicada";

    private final Context appContext;

    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
        appContext = context.getApplicationContext();
    }

    @Override
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
    }

    @Override
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
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Conserva la corrección del Avance 20 para instalaciones antiguas que
        // podían estar marcadas como versión 4 sin tener realmente foto_path.
        asegurarColumnaFotoMascota(db);

        // Protección equivalente para el Avance 21: si una instalación quedó
        // con un esquema incompleto, solo crea/agrega lo faltante y conserva datos.
        asegurarEstructuraUsuariosYPropiedad(db);
    }

    private void crearTablaUsuarios(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_USUARIOS + " (" +
                COL_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USUARIO_NOMBRE + " TEXT NOT NULL, " +
                COL_USUARIO_CORREO + " TEXT NOT NULL UNIQUE, " +
                COL_USUARIO_PASSWORD_HASH + " TEXT NOT NULL" +
                ")");
    }

    private void asegurarEstructuraUsuariosYPropiedad(SQLiteDatabase db) {
        crearTablaUsuarios(db);
        long neiId = asegurarUsuarioPredeterminado(db);

        if (!existeColumna(db, TABLA_MASCOTAS, COL_MASCOTA_USUARIO_ID)) {
            db.execSQL("ALTER TABLE " + TABLA_MASCOTAS +
                    " ADD COLUMN " + COL_MASCOTA_USUARIO_ID + " INTEGER");
        }

        if (neiId > 0) {
            ContentValues values = new ContentValues();
            values.put(COL_MASCOTA_USUARIO_ID, neiId);
            db.update(
                    TABLA_MASCOTAS,
                    values,
                    COL_MASCOTA_USUARIO_ID + " IS NULL OR " + COL_MASCOTA_USUARIO_ID + " <= 0",
                    null
            );
        }
    }

    private long asegurarUsuarioPredeterminado(SQLiteDatabase db) {
        String correo = normalizarCorreo(CORREO_USUARIO_NEI);
        Cursor cursor = db.query(
                TABLA_USUARIOS,
                new String[]{COL_USUARIO_ID},
                COL_USUARIO_CORREO + " = ?",
                new String[]{correo},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(COL_USUARIO_ID));
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(COL_USUARIO_NOMBRE, NOMBRE_USUARIO_NEI);
        values.put(COL_USUARIO_CORREO, correo);
        values.put(COL_USUARIO_PASSWORD_HASH, HASH_PASSWORD_USUARIO_NEI);
        return db.insert(TABLA_USUARIOS, null, values);
    }

    private void asegurarColumnaFotoMascota(SQLiteDatabase db) {
        if (!existeTabla(db, TABLA_MASCOTAS)) {
            return;
        }

        if (!existeColumna(db, TABLA_MASCOTAS, COL_FOTO_PATH)) {
            db.execSQL("ALTER TABLE " + TABLA_MASCOTAS +
                    " ADD COLUMN " + COL_FOTO_PATH + " TEXT");
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
        String crearTabla = "CREATE TABLE " + TABLA_MASCOTAS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MASCOTA_USUARIO_ID + " INTEGER NOT NULL, " +
                COL_NOMBRE + " TEXT NOT NULL, " +
                COL_ESPECIE + " TEXT NOT NULL, " +
                COL_EDAD + " INTEGER NOT NULL, " +
                COL_RAZA + " TEXT, " +
                COL_FOTO_PATH + " TEXT" +
                ")";

        db.execSQL(crearTabla);
    }

    private void crearTablaControles(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE IF NOT EXISTS " + TABLA_CONTROLES + " (" +
                COL_CONTROL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CONTROL_MASCOTA_ID + " INTEGER NOT NULL, " +
                COL_CONTROL_TIPO + " TEXT NOT NULL, " +
                COL_CONTROL_FECHA + " TEXT NOT NULL, " +
                COL_CONTROL_ESTADO + " TEXT NOT NULL, " +
                COL_CONTROL_OBSERVACION + " TEXT" +
                ")";

        db.execSQL(crearTabla);
    }

    private void crearTablaVacunas(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE IF NOT EXISTS " + TABLA_VACUNAS + " (" +
                COL_VACUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_VACUNA_MASCOTA_ID + " INTEGER NOT NULL, " +
                COL_VACUNA_NOMBRE + " TEXT NOT NULL, " +
                COL_VACUNA_FECHA + " TEXT NOT NULL, " +
                COL_VACUNA_ESTADO + " TEXT NOT NULL" +
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
        return db.insert(TABLA_MASCOTAS, null, values);
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
                TABLA_CONTROLES,
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
                TABLA_VACUNAS,
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
                TABLA_MASCOTAS,
                new String[]{COL_ID},
                COL_ID + " = ?",
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
                TABLA_CONTROLES,
                new String[]{COL_CONTROL_ID},
                COL_CONTROL_MASCOTA_ID + " = ? AND " +
                        COL_CONTROL_TIPO + " = ? AND " +
                        COL_CONTROL_FECHA + " = ?",
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
                TABLA_VACUNAS,
                new String[]{COL_VACUNA_ID},
                COL_VACUNA_MASCOTA_ID + " = ? AND " +
                        COL_VACUNA_NOMBRE + " = ? AND " +
                        COL_VACUNA_FECHA + " = ?",
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

    public long insertarUsuario(String nombre, String correo, String passwordHash) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USUARIO_NOMBRE, nombre == null ? "" : nombre.trim());
        values.put(COL_USUARIO_CORREO, normalizarCorreo(correo));
        values.put(COL_USUARIO_PASSWORD_HASH, passwordHash == null ? "" : passwordHash);
        return db.insert(TABLA_USUARIOS, null, values);
    }

    public boolean existeUsuarioPorCorreo(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLA_USUARIOS,
                new String[]{COL_USUARIO_ID},
                COL_USUARIO_CORREO + " = ?",
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

    public Usuario validarUsuario(String correo, String passwordHash) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLA_USUARIOS,
                null,
                COL_USUARIO_CORREO + " = ? AND " + COL_USUARIO_PASSWORD_HASH + " = ?",
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
                TABLA_USUARIOS,
                null,
                COL_USUARIO_ID + " = ?",
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
                TABLA_USUARIOS,
                null,
                COL_USUARIO_CORREO + " = ?",
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

        long id = db.insert(TABLA_MASCOTAS, null, values);
        if (id != -1) {
            mascota.setId(id);
            mascota.setUsuarioId(usuarioId);
        }
        return id;
    }

    public List<Mascota> obtenerMascotas() {
        return obtenerMascotas(obtenerUsuarioSesionId());
    }

    public List<Mascota> obtenerMascotas(long usuarioId) {
        List<Mascota> mascotas = new ArrayList<>();
        if (usuarioId <= 0) {
            return mascotas;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLA_MASCOTAS,
                null,
                COL_MASCOTA_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioId)},
                null,
                null,
                COL_ID + " ASC"
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
                TABLA_MASCOTAS,
                null,
                COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
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
                TABLA_MASCOTAS,
                values,
                COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
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
        values.put(COL_FOTO_PATH, fotoPath == null ? "" : fotoPath);

        int filasActualizadas = db.update(
                TABLA_MASCOTAS,
                values,
                COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
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
        values.putNull(COL_FOTO_PATH);

        int filasActualizadas = db.update(
                TABLA_MASCOTAS,
                values,
                COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
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
                    TABLA_CONTROLES,
                    COL_CONTROL_MASCOTA_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            db.delete(
                    TABLA_VACUNAS,
                    COL_VACUNA_MASCOTA_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            int filasEliminadas = db.delete(
                    TABLA_MASCOTAS,
                    COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
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
                TABLA_MASCOTAS,
                new String[]{COL_FOTO_PATH},
                COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
                new String[]{String.valueOf(mascotaId), String.valueOf(usuarioId)},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor.moveToFirst()) {
                String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO_PATH));
                return fotoPath == null ? "" : fotoPath;
            }
        } finally {
            cursor.close();
        }

        return "";
    }

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

        long id = db.insert(TABLA_CONTROLES, null, values);
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
                "c." + COL_CONTROL_ESTADO + " = ?",
                new String[]{ESTADO_PENDIENTE}
        );
    }

    public List<Control> obtenerControlesPorMascota(long mascotaId) {
        return consultarControles(
                "c." + COL_CONTROL_MASCOTA_ID + " = ?",
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
                "SELECT c.* FROM " + TABLA_CONTROLES + " c " +
                        "INNER JOIN " + TABLA_MASCOTAS + " m ON m." + COL_ID + " = c." + COL_CONTROL_MASCOTA_ID + " " +
                        "WHERE c." + COL_CONTROL_ID + " = ? AND m." + COL_MASCOTA_USUARIO_ID + " = ? LIMIT 1",
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
                TABLA_CONTROLES,
                values,
                COL_CONTROL_ID + " = ? AND " + COL_CONTROL_MASCOTA_ID +
                        " IN (SELECT " + COL_ID + " FROM " + TABLA_MASCOTAS +
                        " WHERE " + COL_MASCOTA_USUARIO_ID + " = ?)",
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
                TABLA_CONTROLES,
                COL_CONTROL_ID + " = ? AND " + COL_CONTROL_MASCOTA_ID +
                        " IN (SELECT " + COL_ID + " FROM " + TABLA_MASCOTAS +
                        " WHERE " + COL_MASCOTA_USUARIO_ID + " = ?)",
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

        String where = "m." + COL_MASCOTA_USUARIO_ID + " = ?";
        if (condicionExtra != null && !condicionExtra.trim().isEmpty()) {
            where += " AND " + condicionExtra;
        }

        SQLiteDatabase db = getReadableDatabase();
        String ordenFecha = crearOrdenFecha("c." + COL_CONTROL_FECHA) + ", c." + COL_CONTROL_ID + " ASC";
        Cursor cursor = db.rawQuery(
                "SELECT c.* FROM " + TABLA_CONTROLES + " c " +
                        "INNER JOIN " + TABLA_MASCOTAS + " m ON m." + COL_ID + " = c." + COL_CONTROL_MASCOTA_ID + " " +
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

        long id = db.insert(TABLA_VACUNAS, null, values);
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
                "v." + COL_VACUNA_MASCOTA_ID + " = ?",
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
                "SELECT v.* FROM " + TABLA_VACUNAS + " v " +
                        "INNER JOIN " + TABLA_MASCOTAS + " m ON m." + COL_ID + " = v." + COL_VACUNA_MASCOTA_ID + " " +
                        "WHERE v." + COL_VACUNA_ID + " = ? AND m." + COL_MASCOTA_USUARIO_ID + " = ? LIMIT 1",
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
                TABLA_VACUNAS,
                values,
                COL_VACUNA_ID + " = ? AND " + COL_VACUNA_MASCOTA_ID +
                        " IN (SELECT " + COL_ID + " FROM " + TABLA_MASCOTAS +
                        " WHERE " + COL_MASCOTA_USUARIO_ID + " = ?)",
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
                TABLA_VACUNAS,
                COL_VACUNA_ID + " = ? AND " + COL_VACUNA_MASCOTA_ID +
                        " IN (SELECT " + COL_ID + " FROM " + TABLA_MASCOTAS +
                        " WHERE " + COL_MASCOTA_USUARIO_ID + " = ?)",
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

        String where = "m." + COL_MASCOTA_USUARIO_ID + " = ?";
        if (condicionExtra != null && !condicionExtra.trim().isEmpty()) {
            where += " AND " + condicionExtra;
        }

        SQLiteDatabase db = getReadableDatabase();
        String ordenFecha = crearOrdenFecha("v." + COL_VACUNA_FECHA) + ", v." + COL_VACUNA_ID + " ASC";
        Cursor cursor = db.rawQuery(
                "SELECT v.* FROM " + TABLA_VACUNAS + " v " +
                        "INNER JOIN " + TABLA_MASCOTAS + " m ON m." + COL_ID + " = v." + COL_VACUNA_MASCOTA_ID + " " +
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

    private boolean mascotaPerteneceAUsuario(SQLiteDatabase db, long mascotaId, long usuarioId) {
        if (mascotaId <= 0 || usuarioId <= 0) {
            return false;
        }

        Cursor cursor = db.query(
                TABLA_MASCOTAS,
                new String[]{COL_ID},
                COL_ID + " = ? AND " + COL_MASCOTA_USUARIO_ID + " = ?",
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
        values.put(COL_MASCOTA_USUARIO_ID, usuarioId);
        return values;
    }

    private ContentValues crearValoresMascotaSinUsuario(String nombre, String especie, int edad, String raza) {
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_ESPECIE, especie);
        values.put(COL_EDAD, edad);
        values.put(COL_RAZA, raza == null ? "" : raza);
        return values;
    }

    private ContentValues crearValoresControl(long mascotaId, String tipo, String fecha, String estado, String observacion) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTROL_MASCOTA_ID, mascotaId);
        values.put(COL_CONTROL_TIPO, tipo);
        values.put(COL_CONTROL_FECHA, fecha);
        values.put(COL_CONTROL_ESTADO, estado);
        values.put(COL_CONTROL_OBSERVACION, observacion == null ? "" : observacion);
        return values;
    }

    private ContentValues crearValoresVacuna(long mascotaId, String nombre, String fecha, String estado) {
        ContentValues values = new ContentValues();
        values.put(COL_VACUNA_MASCOTA_ID, mascotaId);
        values.put(COL_VACUNA_NOMBRE, nombre);
        values.put(COL_VACUNA_FECHA, fecha);
        values.put(COL_VACUNA_ESTADO, estado);
        return values;
    }

    private Usuario crearUsuarioDesdeCursor(Cursor cursor) {
        return new Usuario(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_USUARIO_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_NOMBRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_CORREO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USUARIO_PASSWORD_HASH))
        );
    }

    private Mascota crearMascotaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        long usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MASCOTA_USUARIO_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE));
        String especie = cursor.getString(cursor.getColumnIndexOrThrow(COL_ESPECIE));
        int edad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EDAD));
        String raza = cursor.getString(cursor.getColumnIndexOrThrow(COL_RAZA));
        String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO_PATH));

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
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CONTROL_ID));
        long mascotaId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CONTROL_MASCOTA_ID));
        String tipo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTROL_TIPO));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTROL_FECHA));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTROL_ESTADO));
        String observacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTROL_OBSERVACION));

        return new Control(id, mascotaId, tipo, fecha, estado, observacion == null ? "" : observacion);
    }

    private Vacuna crearVacunaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_VACUNA_ID));
        long mascotaId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_VACUNA_MASCOTA_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_VACUNA_NOMBRE));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_VACUNA_FECHA));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(COL_VACUNA_ESTADO));

        return new Vacuna(id, mascotaId, nombre, fecha, estado);
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
