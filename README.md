# PetHealth

## Descripción

PetHealth es una aplicación Android pensada para apoyar en el cuidado y seguimiento de la salud de las mascotas.

La idea del proyecto es poder guardar en un solo lugar información importante de cada mascota, como sus vacunas, controles médicos y desparasitaciones, además de mantener sus datos principales organizados.

El proyecto se ha ido desarrollando de manera progresiva durante la asignatura de Programación Android, agregando nuevas funciones y mejorando su organización a medida que avanza el semestre.

## Objetivo

El objetivo de PetHealth es crear una aplicación sencilla y fácil de utilizar que permita registrar mascotas y mantener ordenada información relacionada con sus cuidados.

Con la aplicación se busca facilitar el acceso a datos como vacunas, controles y desparasitaciones de cada mascota, manteniendo la información asociada al usuario que tiene la sesión iniciada.

## Funcionalidades actuales

Actualmente PetHealth permite:

- registrar usuarios e iniciar sesión;
- mantener una sesión de usuario;
- registrar mascotas;
- ver el detalle de cada mascota;
- editar la información de las mascotas;
- agregar una fotografía de la mascota utilizando la cámara;
- registrar, visualizar y editar controles;
- registrar, visualizar y editar vacunas;
- registrar y visualizar desparasitaciones;
- guardar la información de forma local utilizando SQLite.

## Tecnologías utilizadas

- Java 17
- XML
- Android Studio
- Gradle Groovy
- SQLite
- SQLiteOpenHelper
- RecyclerView
- SharedPreferences
- ExecutorService

## Requisitos

- minSdk 29
- compileSdk 35
- targetSdk 35
- Java 17

## Base de datos

PetHealth utiliza SQLite para guardar la información de forma local dentro de la aplicación.

La organización principal de la base de datos se apoya en:

```text
PetHealthContract → DatabaseHelper → SQLite
```

Actualmente se utilizan las siguientes tablas:

- `usuarios`
- `mascotas`
- `controles`
- `vacunas`
- `desparasitaciones`

De esta forma, cada usuario puede trabajar con sus mascotas y cada mascota puede mantener sus propios controles, vacunas y registros de desparasitación.

## Seguridad básica

Durante el desarrollo también se incorporaron algunas buenas prácticas básicas para cuidar mejor la información de la aplicación.

Entre ellas se encuentran:

- consultas SQLite parametrizadas;
- uso de `ContentValues` y `selectionArgs`;
- cierre de los objetos `Cursor` después de utilizarlos;
- almacenamiento de contraseñas mediante hash;
- manejo de la sesión mediante `SessionManager`;
- uso de `FLAG_SECURE` en las pantallas de inicio de sesión y registro;
- configuración de `allowBackup="false"`.

Estas medidas corresponden a mejoras básicas realizadas dentro del contexto académico del proyecto.

## Organización del proyecto

Para mantener el código más ordenado, las clases se encuentran separadas según la función que cumplen dentro de PetHealth.

```text
auth/              Inicio de sesión, registro y sesión
database/          Base de datos SQLite
model/             Modelos de datos
adapter/           Adaptadores utilizados en los listados
mascota/           Gestión de mascotas
control/           Controles médicos
vacuna/            Vacunas
desparasitacion/   Desparasitación
```

Esta organización permite encontrar de forma más sencilla las clases relacionadas con cada parte de la aplicación.

## Diagramas

### Diagrama de Navegación

El siguiente diagrama representa de manera general la navegación entre las principales pantallas de PetHealth.

![Diagrama de Navegación](./docs/diagramas/PetHealth_Diagrama_Navegacion.png)

### Modelo Entidad-Relación

El modelo muestra la estructura principal utilizada para organizar la información almacenada en SQLite.

![Modelo Entidad-Relación](./docs/diagramas/PetHealth_MER_SQLite.png)

El proyecto actual también incorpora el registro de desparasitaciones asociado a cada mascota.

## Instalación

1. Descargar o clonar el proyecto.
2. Abrir la carpeta en Android Studio.
3. Esperar que Gradle termine de sincronizar.
4. Ejecutar la aplicación en un emulador o dispositivo Android compatible.

## Autora

**Neiza Funes**

Proyecto desarrollado para la asignatura de Programación Android.
