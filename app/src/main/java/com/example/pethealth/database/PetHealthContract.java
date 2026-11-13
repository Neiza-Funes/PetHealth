package com.example.pethealth.database;

import com.example.pethealth.database.PetHealthContract.ControlEntry;
import com.example.pethealth.database.PetHealthContract.DesparasitacionEntry;
import com.example.pethealth.database.PetHealthContract.MascotaEntry;
import com.example.pethealth.database.PetHealthContract.UsuarioEntry;
import com.example.pethealth.database.PetHealthContract.VacunaEntry;

import android.provider.BaseColumns;

/**
 * Define de forma centralizada los nombres de tablas y columnas usados por SQLite en PetHealth.
 * La clase representa el esquema actual de la aplicación y no modifica la estructura de la base de datos.
 */
public final class PetHealthContract {

    private PetHealthContract() {
        // Evita que la clase de contrato sea instanciada.
    }

    /** Tabla que almacena las cuentas de usuario. */
    public static final class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_CORREO = "correo";
        public static final String COLUMN_PASSWORD_HASH = "password_hash";

        private UsuarioEntry() {
        }
    }

    /** Tabla que almacena las mascotas asociadas a cada usuario. */
    public static final class MascotaEntry implements BaseColumns {
        public static final String TABLE_NAME = "mascotas";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USUARIO_ID = "usuario_id";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_ESPECIE = "especie";
        public static final String COLUMN_EDAD = "edad";
        public static final String COLUMN_RAZA = "raza";
        public static final String COLUMN_FOTO_PATH = "foto_path";

        private MascotaEntry() {
        }
    }

    /** Tabla que almacena los controles médicos de las mascotas. */
    public static final class ControlEntry implements BaseColumns {
        public static final String TABLE_NAME = "controles";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MASCOTA_ID = "mascota_id";
        public static final String COLUMN_TIPO = "tipo";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_ESTADO = "estado";
        public static final String COLUMN_OBSERVACION = "observacion";

        private ControlEntry() {
        }
    }

    /** Tabla que almacena las vacunas registradas para las mascotas. */
    public static final class VacunaEntry implements BaseColumns {
        public static final String TABLE_NAME = "vacunas";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MASCOTA_ID = "mascota_id";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_ESTADO = "estado";

        private VacunaEntry() {
        }
    }
    /** Tabla que almacena los registros de desparasitación de las mascotas. */
    public static final class DesparasitacionEntry implements BaseColumns {
        public static final String TABLE_NAME = "desparasitaciones";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MASCOTA_ID = "mascota_id";
        public static final String COLUMN_PRODUCTO = "producto";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_PROXIMA_FECHA = "proxima_fecha";
        public static final String COLUMN_OBSERVACION = "observacion";

        private DesparasitacionEntry() {
        }
    }

}
