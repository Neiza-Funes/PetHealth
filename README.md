# PetHealth - Aplicación Móvil de Gestión de Salud Animal

> PetHealth es una aplicación Android desarrollada para apoyar el registro y seguimiento de información relacionada con la salud y cuidado de las mascotas, permitiendo mantener sus datos organizados de forma local dentro del dispositivo.

## Ficha Técnica del Proyecto

- **Nombre de la aplicación:** PetHealth
- **Asignatura:** Programación Android (IEI-093)
- **Institución:** Santo Tomás
- **Estudiante:** Neiza Jazmín Funes Bautista
- **Lenguaje:** Java 17
- **Interfaz:** XML
- **minSdk:** 29
- **compileSdk:** 35
- **targetSdk:** 35
- **Persistencia:** SQLite mediante `SQLiteOpenHelper`
- **Construcción:** Gradle Groovy

## Propósito y Problema que Resuelve

Muchas veces la información relacionada con las mascotas, como vacunas, controles médicos o desparasitaciones, se encuentra separada o puede ser difícil de recordar con el tiempo.

PetHealth busca facilitar este proceso permitiendo mantener en un solo lugar los datos principales de cada mascota y sus registros de cuidado.

La aplicación permite que cada usuario mantenga sus propias mascotas registradas y pueda consultar información relacionada con sus controles, vacunas y desparasitaciones.

## Funcionalidades Principales

Actualmente PetHealth cuenta con las siguientes funcionalidades:

- Registro de usuarios.
- Inicio de sesión.
- Manejo de sesión del usuario.
- Registro de mascotas.
- Listado de mascotas asociadas al usuario.
- Visualización del detalle de una mascota.
- Edición de información de mascotas.
- Fotografía de mascota utilizando la cámara.
- Registro y visualización de controles médicos.
- Edición de controles.
- Registro y visualización de vacunas.
- Edición de vacunas.
- Registro de desparasitaciones.
- Listado de desparasitaciones por mascota.
- Eliminación de registros de desparasitación.
- Almacenamiento local mediante SQLite.
- Separación de información según el usuario y la mascota seleccionada.

## Stack Tecnológico y Estructura

PetHealth fue desarrollada de forma nativa para Android utilizando herramientas vistas durante la asignatura.

Las principales tecnologías utilizadas son:

- **Android Studio** como entorno de desarrollo.
- **Java 17** para la lógica de la aplicación.
- **XML** para la construcción de las interfaces.
- **Gradle Groovy** para la configuración del proyecto.
- **SQLite** para el almacenamiento local.
- **SQLiteOpenHelper** para la creación y administración de la base de datos.
- **RecyclerView** para mostrar listados dinámicos.
- **SharedPreferences** para mantener información básica de la sesión.
- **ExecutorService** para ejecutar algunas operaciones de base de datos fuera del hilo principal.

El proyecto también utiliza una clase `PetHealthContract` para mantener organizados los nombres de tablas y columnas utilizados por SQLite.

## Modelo de Persistencia SQLite

PetHealth utiliza una base de datos SQLite local para almacenar la información de usuarios, mascotas y sus distintos registros.

La estructura utilizada se puede representar de forma simple de la siguiente manera:

```text
PetHealthContract → DatabaseHelper → SQLite
```

`PetHealthContract` mantiene centralizados los nombres de las tablas y columnas, mientras que `DatabaseHelper` se encarga de crear la base de datos y realizar las operaciones necesarias.

Actualmente la base de datos posee las siguientes tablas:

- `usuarios`
- `mascotas`
- `controles`
- `vacunas`
- `desparasitaciones`

Los registros se relacionan utilizando identificadores como:

- `usuario_id`
- `mascota_id`

De esta forma, las mascotas pueden asociarse al usuario que mantiene la sesión iniciada y los controles, vacunas y desparasitaciones pueden relacionarse con la mascota correspondiente.

## Seguridad y Buenas Prácticas

Dentro del alcance académico del proyecto se incorporaron algunas buenas prácticas para mejorar el manejo de los datos.

Entre ellas se encuentran:

- Consultas SQLite parametrizadas.
- Uso de `selectionArgs`.
- Uso de `ContentValues` para inserciones y actualizaciones.
- Cierre correcto de objetos `Cursor`.
- Almacenamiento de contraseñas mediante hash.
- Uso de `SessionManager` para mantener los datos mínimos necesarios de la sesión.
- `FLAG_SECURE` en las pantallas de Login y Registro.
- `android:allowBackup="false"`.
- Activities internas configuradas con `android:exported="false"` cuando corresponde.

Estas medidas buscan mantener una estructura más segura y ordenada dentro del nivel trabajado durante el desarrollo de la aplicación.

## ⚙️ Procesamiento en Segundo Plano

Algunas operaciones relacionadas con SQLite utilizan `ExecutorService`.

Esto permite realizar consultas o escrituras de base de datos fuera del hilo principal de Android y luego actualizar la interfaz cuando corresponde.

El objetivo es evitar que ciertas operaciones puedan afectar la fluidez de la aplicación mientras se consultan o guardan datos.

## Organización del Proyecto

A medida que PetHealth fue creciendo, las clases se organizaron por paquetes para facilitar su lectura y mantenimiento.

```text
com.example.pethealth
├── adapter/
├── auth/
├── control/
├── database/
├── desparasitacion/
├── mascota/
├── model/
├── vacuna/
└── InicioActivity
```

La organización principal corresponde a:

- `adapter/`: clases utilizadas por los RecyclerView.
- `auth/`: Login, Registro, sesión y utilidades relacionadas.
- `control/`: gestión de controles médicos.
- `database/`: clases relacionadas con SQLite.
- `desparasitacion/`: registro y visualización de desparasitaciones.
- `mascota/`: registro, listado, detalle y edición de mascotas.
- `model/`: clases que representan los datos utilizados por la aplicación.
- `vacuna/`: registro, listado y edición de vacunas.
- `InicioActivity`: pantalla principal desde donde se accede a los distintos módulos.

## Capturas de Pantalla

En esta sección se pueden incorporar posteriormente capturas reales de las principales pantallas de PetHealth, por ejemplo:

- Login.
- Inicio.
- Mis Mascotas.
- Detalle de mascota.
- Vacunas.
- Controles médicos.
- Desparasitación.

Las capturas pueden agregarse cuando se tengan disponibles las imágenes definitivas del proyecto.

## Diagramas

### Diagrama de Navegación

El siguiente diagrama representa de forma general la navegación entre las principales pantallas de PetHealth.

![Diagrama de Navegación](./docs/diagramas/PetHealth_Diagrama_Navegacion.png)

### Modelo Entidad-Relación

El modelo muestra la estructura utilizada para representar la información almacenada mediante SQLite.

![Modelo Entidad-Relación](./docs/diagramas/PetHealth_MER_SQLite.png)

## Instalación y Ejecución

Para ejecutar PetHealth:

1. Clonar o descargar el repositorio.
2. Abrir el proyecto utilizando Android Studio.
3. Esperar que finalice la sincronización de Gradle.
4. Seleccionar un emulador o dispositivo Android compatible.
5. Ejecutar la aplicación desde Android Studio.

El proyecto requiere como mínimo Android API 29.

##  Autora

**Neiza Jazmín Funes Bautista**

Proyecto desarrollado como parte de la asignatura **Programación Android (IEI-093)** de Santo Tomás.

PetHealth fue desarrollado de manera progresiva durante la asignatura, incorporando funcionalidades relacionadas con persistencia local, manejo de sesiones, RecyclerView, cámara, seguridad básica y organización del código.