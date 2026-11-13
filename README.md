# PetHealth

PetHealth es una aplicación Android orientada al registro y seguimiento de información relacionada con la salud de las mascotas. El proyecto se desarrolla de forma progresiva durante la asignatura de Programación Android.

## Objetivo

El objetivo de PetHealth es mantener en un solo lugar la información básica de las mascotas y facilitar el acceso a sus controles, vacunas y otros registros de cuidado asociados al usuario que mantiene la sesión iniciada.

## Funcionalidades actuales

Actualmente el proyecto cuenta con:

- registro e inicio de sesión de usuarios;
- sesión de usuario mediante `SharedPreferences`;
- registro y administración de mascotas;
- detalle y edición de mascotas;
- fotografía de la mascota desde la cámara;
- registro, visualización y edición de controles;
- registro, visualización y edición de vacunas;
- módulo de desparasitación con formulario y pantalla de consulta, cuya persistencia completa en SQLite todavía se encuentra pendiente;
- almacenamiento local mediante SQLite.

## Tecnologías utilizadas

- Java 17
- XML
- Android Studio
- Gradle Groovy
- SQLite
- SQLiteOpenHelper
- RecyclerView
- SharedPreferences

## Requisitos

- minSdk 29
- compileSdk 35
- targetSdk 35
- Java 17

## Persistencia de datos

PetHealth utiliza SQLite mediante la clase `DatabaseHelper` para almacenar la información local de la aplicación.

La base de datos actual utiliza principalmente las tablas:

- `usuarios`
- `mascotas`
- `controles`
- `vacunas`

Las mascotas se relacionan con el usuario autenticado mediante `usuario_id`, mientras que controles y vacunas utilizan `mascota_id` para identificar a la mascota correspondiente. En la implementación actual estas relaciones se administran mediante identificadores usados por la aplicación; las sentencias de creación de las tablas no declaran restricciones `FOREIGN KEY` de SQLite.

El modelo incluido en este avance se utiliza como representación conceptual de estas relaciones y respeta los campos principales existentes en el proyecto.


## Contract Class

En este avance se incorporó `PetHealthContract` para centralizar los nombres de las tablas y columnas que utiliza SQLite. De esta forma `DatabaseHelper` puede trabajar con constantes comunes y se evita repetir manualmente cadenas como `usuarios`, `mascotas`, `controles`, `vacunas`, `usuario_id` o `mascota_id` en distintas partes de la clase.

La estructura utilizada puede resumirse como:

```text
PetHealthContract → DatabaseHelper → SQLite
```

El Contract representa el esquema que PetHealth ya utilizaba. No se cambiaron nombres de tablas o columnas, no se agregaron restricciones `FOREIGN KEY` y la versión de la base de datos continúa en `5`.


## Seguridad y buenas prácticas

En este avance se revisaron medidas básicas de seguridad sin cambiar la estructura de la base de datos ni las funciones principales de PetHealth.

- Las consultas que reciben datos variables utilizan argumentos parametrizados con `?` y `selectionArgs`.
- Las inserciones y actualizaciones utilizan `ContentValues`.
- Los objetos `Cursor` utilizados por `DatabaseHelper` se cierran después de cada consulta.
- `PetHealthContract` continúa centralizando los nombres de tablas y columnas utilizados por SQLite.
- `MainActivity` se mantiene como la Activity pública de inicio y las Activities internas utilizan `android:exported="false"`.
- Se configuró `android:allowBackup="false"` para reducir la exposición de los datos locales mediante copias de respaldo.
- `LoginActivity` y `RegistroActivity` utilizan `FLAG_SECURE` por tratarse de pantallas donde se ingresan credenciales.
- Las contraseñas se almacenan mediante hash SHA-256 dentro del alcance académico del proyecto; no se guardan contraseñas ni hashes en `SharedPreferences`.
- `SessionManager` conserva únicamente los datos necesarios para identificar la sesión activa y los elimina al cerrar sesión.
- Mascotas, controles y vacunas continúan filtrándose mediante `usuario_id` y `mascota_id` según corresponda.

La fotografía de mascota se obtiene mediante `MediaStore.ACTION_IMAGE_CAPTURE` y PetHealth recibe el `Bitmap` devuelto por la aplicación de cámara. Luego la imagen se guarda dentro del almacenamiento privado de la aplicación. Como no se entrega una URI de archivo externa mediante `EXTRA_OUTPUT`, la implementación actual no necesita `FileProvider`. El permiso de cámara se mantiene porque el flujo actual lo comprueba y solicita antes de abrir la captura.

Estas medidas corresponden a buenas prácticas básicas de seguridad dentro del alcance académico del proyecto y no representan un esquema de seguridad avanzado de producción.

## Diagrama de Navegación

El siguiente diagrama corresponde al material preparado para PetHealth y resume el flujo principal entre gestión de usuarios, mascotas, controles y vacunas.

![Diagrama de Navegación de PetHealth](./docs/diagramas/PetHealth_Diagrama_Navegacion.png)

Además del flujo representado gráficamente, el proyecto actual permite acceder al módulo de desparasitación desde `InicioActivity`. Los identificadores necesarios para editar mascotas, controles o vacunas se transfieren entre Activities mediante `Intent` cuando corresponde.

## Modelo Entidad-Relación

El modelo representa la estructura principal de persistencia usada actualmente por PetHealth: usuarios, mascotas, controles y vacunas.

![Modelo Entidad-Relación de PetHealth](./docs/diagramas/PetHealth_MER_SQLite.png)

La tabla de desparasitación no se incorpora en este modelo porque el Avance 21 todavía no implementa su persistencia completa en SQLite.

## Instalación y ejecución

1. Clonar o descargar el proyecto.
2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar que Gradle termine de sincronizar.
4. Ejecutar la aplicación en un emulador o dispositivo Android compatible.

## Avance 24

Este avance mantiene la organización mediante `PetHealthContract` e incorpora una revisión de seguridad básica. Se ajustó el Manifest para deshabilitar copias de respaldo y se agregó `FLAG_SECURE` en Login y Registro. También se revisaron consultas SQLite, cursores, sesión, contraseñas y el flujo actual de cámara sin cambiar `VERSION_BD = 5`, el esquema de las tablas ni las funcionalidades existentes.
