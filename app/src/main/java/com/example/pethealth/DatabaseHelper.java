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
    private static final int VERSION_BD = 2;

    public static final String TABLA_MASCOTAS = "mascotas";
    public static final String COL_ID = "id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_ESPECIE = "especie";
    public static final String COL_EDAD = "edad";
    public static final String COL_RAZA = "raza";

    public static final String TABLA_CONTROLES = "controles";
    public static final String COL_CONTROL_ID = "id";
    public static final String COL_CONTROL_MASCOTA_ID = "mascota_id";
    public static final String COL_CONTROL_TIPO = "tipo";
    public static final String COL_CONTROL_FECHA = "fecha";
    public static final String COL_CONTROL_ESTADO = "estado";
    public static final String COL_CONTROL_OBSERVACION = "observacion";

    private static final String ESTADO_PENDIENTE = "Pendiente";

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

        // Controles de ejemplo existentes en PetHealth.
        // También se crean solo una vez junto con la base de datos.
        insertarControlInicial(db, lunaId, "Revisión general", "05/10/2026", ESTADO_PENDIENTE, "");
        insertarControlInicial(db, miloId, "Control preventivo", "12/10/2026", ESTADO_PENDIENTE, "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Avance 12: se agrega la tabla controles sin borrar la tabla mascotas.
            crearTablaControles(db);

            // Si Luna o Milo aún existen con sus IDs iniciales, se agregan sus controles.
            // No se recrean mascotas eliminadas ni se sobrescriben datos editados.
            insertarControlInicialSiCorresponde(
                    db, 1, "Revisión general", "05/10/2026", ESTADO_PENDIENTE, "");
            insertarControlInicialSiCorresponde(
                    db, 2, "Control preventivo", "12/10/2026", ESTADO_PENDIENTE, "");
        }
    }

    private void crearTablaMascotas(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLA_MASCOTAS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE + " TEXT NOT NULL, " +
                COL_ESPECIE + " TEXT NOT NULL, " +
                COL_EDAD + " INTEGER NOT NULL, " +
                COL_RAZA + " TEXT" +
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

        ContentValues values = crearValoresControl(
                mascotaId, tipo, fecha, estado, observacion);
        db.insert(TABLA_CONTROLES, null, values);
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

    public boolean eliminarMascota(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Los controles se eliminan primero para evitar registros huérfanos.
            db.delete(
                    TABLA_CONTROLES,
                    COL_CONTROL_MASCOTA_ID + " = ?",
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

    private List<Control> consultarControles(String seleccion, String[] argumentos) {
        List<Control> controles = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Las fechas se guardan como DD/MM/AAAA. Esta expresión las ordena como AAAAMMDD.
        String ordenFecha = "substr(" + COL_CONTROL_FECHA + ",7,4) || " +
                "substr(" + COL_CONTROL_FECHA + ",4,2) || " +
                "substr(" + COL_CONTROL_FECHA + ",1,2) ASC, " +
                COL_CONTROL_ID + " ASC";

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

    private Mascota crearMascotaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE));
        String especie = cursor.getString(cursor.getColumnIndexOrThrow(COL_ESPECIE));
        int edad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EDAD));
        String raza = cursor.getString(cursor.getColumnIndexOrThrow(COL_RAZA));

        return new Mascota(id, nombre, especie, edad, raza == null ? "" : raza);
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
}
