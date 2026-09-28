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
    private static final int VERSION_BD = 1;

    public static final String TABLA_MASCOTAS = "mascotas";
    public static final String COL_ID = "id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_ESPECIE = "especie";
    public static final String COL_EDAD = "edad";
    public static final String COL_RAZA = "raza";

    public DatabaseHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLA_MASCOTAS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE + " TEXT NOT NULL, " +
                COL_ESPECIE + " TEXT NOT NULL, " +
                COL_EDAD + " INTEGER NOT NULL, " +
                COL_RAZA + " TEXT" +
                ")";

        db.execSQL(crearTabla);

        // Mascotas de ejemplo existentes en avances anteriores.
        // Se insertan aquí para que se creen solamente una vez junto con la base de datos.
        insertarMascotaInicial(db, "Luna", "Perro", 3, "");
        insertarMascotaInicial(db, "Milo", "Gato", 2, "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_MASCOTAS);
        onCreate(db);
    }

    private void insertarMascotaInicial(
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
        db.insert(TABLA_MASCOTAS, null, values);
    }

    public long insertarMascota(Mascota mascota) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, mascota.getNombre());
        values.put(COL_ESPECIE, mascota.getEspecie());
        values.put(COL_EDAD, mascota.getEdad());
        values.put(COL_RAZA, mascota.getRaza());

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

    private Mascota crearMascotaDesdeCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE));
        String especie = cursor.getString(cursor.getColumnIndexOrThrow(COL_ESPECIE));
        int edad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EDAD));
        String raza = cursor.getString(cursor.getColumnIndexOrThrow(COL_RAZA));

        return new Mascota(id, nombre, especie, edad, raza == null ? "" : raza);
    }
}
