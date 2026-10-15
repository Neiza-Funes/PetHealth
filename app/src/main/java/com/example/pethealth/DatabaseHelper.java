package com.example.pethealth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "pethealth.db";
    private static final int VERSION_BD = 4;

    public static final String TABLA_MASCOTAS = "mascotas";
    public static final String COL_ID = "id";
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

    private static final String ESTADO_PENDIENTE = "Pendiente";
    private static final String ESTADO_APLICADA = "Aplicada";

    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        crearTablaMascotas(db);

        // Mascotas de ejemplo de los avances anteriores.
        // Se insertan solo cuando pethealth.db se crea por primera vez.
        long lunaId = insertarMascotaInicial(db, "Luna", "Perro", 3, "");
        long miloId = insertarMascotaInicial(db, "Milo", "Gato", 2, "");

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
            // Avance 12: se agrega controles sin borrar mascotas.
            crearTablaControles(db);
            insertarControlInicialSiCorresponde(
                    db, 1, "Revisión general", "05/10/2026", ESTADO_PENDIENTE, "");
            insertarControlInicialSiCorresponde(
                    db, 2, "Control preventivo", "12/10/2026", ESTADO_PENDIENTE, "");
        }

        if (oldVersion < 3) {
            // Avance 13: se agrega vacunas sin borrar mascotas ni controles.
            crearTablaVacunas(db);
            insertarVacunaInicialSiCorresponde(
                    db, 1, "Antirrábica", "15/09/2026", ESTADO_APLICADA);
            insertarVacunaInicialSiCorresponde(
                    db, 2, "Triple felina", "28/09/2026", ESTADO_PENDIENTE);
        }

        if (oldVersion < 4) {
            // Avance 18: se agrega la ruta de la foto sin perder mascotas existentes.
            db.execSQL("ALTER TABLE " + TABLA_MASCOTAS +
                    " ADD COLUMN " + COL_FOTO_PATH + " TEXT");
        }
    }

    private void crearTablaMascotas(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLA_MASCOTAS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
            String nombre,
            String especie,
            int edad,
            String raza) {

        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_ESPECIE, especie);
        values.put(COL_EDAD, edad);
        values.put(COL_RAZA, raza);
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

        if (!existeMascota(db, mascotaId) || existeControlInicial(db, mascotaId, tipo, fecha)) {
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

        if (!existeMascota(db, mascotaId) || existeVacunaInicial(db, mascotaId, nombre, fecha)) {
            return;
        }

        insertarVacunaInicial(db, mascotaId, nombre, fecha, estado);
    }

    private boolean existeMascota(SQLiteDatabase db, long mascotaId) {
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

    private boolean existeControlInicial(
            SQLiteDatabase db,
            long mascotaId,
            String tipo,
            String fecha) {

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

    private boolean existeVacunaInicial(
            SQLiteDatabase db,
            long mascotaId,
            String nombre,
            String fecha) {

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

    public long insertarMascota(Mascota mascota) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = crearValoresMascota(
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getEdad(),
                mascota.getRaza()
        );

        long id = db.insert(TABLA_MASCOTAS, null, values);
        if (id != -1) {
            mascota.setId(id);
        }
        return id;
    }

    public List<Mascota> obtenerMascotas() {
        List<Mascota> mascotas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLA_MASCOTAS,
                null,
                null,
                null,
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
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLA_MASCOTAS,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
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

    public boolean actualizarMascota(
            long id,
            String nombre,
            String especie,
            int edad,
            String raza) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = crearValoresMascota(nombre, especie, edad, raza);

        int filasActualizadas = db.update(
                TABLA_MASCOTAS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return filasActualizadas > 0;
    }

    public boolean actualizarFotoMascota(long mascotaId, String fotoPath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FOTO_PATH, fotoPath == null ? "" : fotoPath);

        int filasActualizadas = db.update(
                TABLA_MASCOTAS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(mascotaId)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarMascota(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Primero se eliminan los registros relacionados para evitar datos huérfanos.
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
                    COL_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            db.setTransactionSuccessful();
            return filasEliminadas > 0;
        } finally {
            db.endTransaction();
        }
    }

    public long insertarControl(Control control) {
        SQLiteDatabase db = getWritableDatabase();
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
                COL_CONTROL_ESTADO + " = ?",
                new String[]{ESTADO_PENDIENTE}
        );
    }

    public List<Control> obtenerControlesPorMascota(long mascotaId) {
        return consultarControles(
                COL_CONTROL_MASCOTA_ID + " = ?",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    public Control obtenerControlPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLA_CONTROLES,
                null,
                COL_CONTROL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1"
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

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = crearValoresControl(
                mascotaId,
                tipo,
                fecha,
                estado,
                observacion
        );

        int filasActualizadas = db.update(
                TABLA_CONTROLES,
                values,
                COL_CONTROL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarControl(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int filasEliminadas = db.delete(
                TABLA_CONTROLES,
                COL_CONTROL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        return filasEliminadas > 0;
    }

    private List<Control> consultarControles(String seleccion, String[] argumentos) {
        List<Control> controles = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String ordenFecha = crearOrdenFecha(COL_CONTROL_FECHA) + ", " + COL_CONTROL_ID + " ASC";
        Cursor cursor = db.query(
                TABLA_CONTROLES,
                null,
                seleccion,
                argumentos,
                null,
                null,
                ordenFecha
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
        SQLiteDatabase db = getWritableDatabase();
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
                COL_VACUNA_MASCOTA_ID + " = ?",
                new String[]{String.valueOf(mascotaId)}
        );
    }

    public Vacuna obtenerVacunaPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLA_VACUNAS,
                null,
                COL_VACUNA_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1"
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

    public boolean actualizarVacuna(
            long id,
            long mascotaId,
            String nombre,
            String fecha,
            String estado) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = crearValoresVacuna(
                mascotaId,
                nombre,
                fecha,
                estado
        );

        int filasActualizadas = db.update(
                TABLA_VACUNAS,
                values,
                COL_VACUNA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return filasActualizadas > 0;
    }

    public boolean eliminarVacuna(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int filasEliminadas = db.delete(
                TABLA_VACUNAS,
                COL_VACUNA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        return filasEliminadas > 0;
    }

    private List<Vacuna> consultarVacunas(String seleccion, String[] argumentos) {
        List<Vacuna> vacunas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String ordenFecha = crearOrdenFecha(COL_VACUNA_FECHA) + ", " + COL_VACUNA_ID + " ASC";
        Cursor cursor = db.query(
                TABLA_VACUNAS,
                null,
                seleccion,
                argumentos,
                null,
                null,
                ordenFecha
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

    private String crearOrdenFecha(String columnaFecha) {
        // Las fechas se guardan como DD/MM/AAAA y se ordenan como AAAAMMDD.
        return "substr(" + columnaFecha + ",7,4) || " +
                "substr(" + columnaFecha + ",4,2) || " +
                "substr(" + columnaFecha + ",1,2) ASC";
    }

    private ContentValues crearValoresMascota(
            String nombre,
            String especie,
            int edad,
            String raza) {

        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_ESPECIE, especie);
        values.put(COL_EDAD, edad);
        values.put(COL_RAZA, raza == null ? "" : raza);
        return values;
    }

    private ContentValues crearValoresControl(
            long mascotaId,
            String tipo,
            String fecha,
            String estado,
            String observacion) {

        ContentValues values = new ContentValues();
        values.put(COL_CONTROL_MASCOTA_ID, mascotaId);
        values.put(COL_CONTROL_TIPO, tipo);
        values.put(COL_CONTROL_FECHA, fecha);
        values.put(COL_CONTROL_ESTADO, estado);
        values.put(COL_CONTROL_OBSERVACION, observacion == null ? "" : observacion);
        return values;
    }

    private ContentValues crearValoresVacuna(
            long mascotaId,
            String nombre,
            String fecha,
            String estado) {

        ContentValues values = new ContentValues();
        values.put(COL_VACUNA_MASCOTA_ID, mascotaId);
        values.put(COL_VACUNA_NOMBRE, nombre);
        values.put(COL_VACUNA_FECHA, fecha);
        values.put(COL_VACUNA_ESTADO, estado);
        return values;
    }

    private Mascota crearMascotaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE));
        String especie = cursor.getString(cursor.getColumnIndexOrThrow(COL_ESPECIE));
        int edad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EDAD));
        String raza = cursor.getString(cursor.getColumnIndexOrThrow(COL_RAZA));
        String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO_PATH));

        return new Mascota(
                id,
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

        return new Control(
                id,
                mascotaId,
                tipo,
                fecha,
                estado,
                observacion == null ? "" : observacion
        );
    }

    private Vacuna crearVacunaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_VACUNA_ID));
        long mascotaId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_VACUNA_MASCOTA_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_VACUNA_NOMBRE));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_VACUNA_FECHA));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(COL_VACUNA_ESTADO));

        return new Vacuna(id, mascotaId, nombre, fecha, estado);
    }
}
