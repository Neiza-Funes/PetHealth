Proyecto Android - PetHealth
PetHealth es una aplicación Android orientada a la gestión y seguimiento de mascotas.
El proyecto permite registrar mascotas y mantener información relacionada con sus controles, vacunas y otros datos importantes para su cuidado.
Fue desarrollado progresivamente en Android Studio mediante distintos avances hasta llegar a una versión final funcional.

Objetivo:El objetivo de PetHealth es facilitar la organización de información básica relacionada con la salud y cuidado de las mascotas mediante una aplicación Android sencilla.
La aplicación permite mantener los datos almacenados localmente utilizando SQLite y acceder a ellos desde distintas pantallas.

Funcionalidades:PetHealth incorpora registro e inicio de sesión de usuarios.
La sesión del usuario puede mantenerse activa para evitar iniciar sesión nuevamente cada vez que se abre la aplicación.
La aplicación permite registrar mascotas y consultar posteriormente sus datos.
También permite editar y eliminar mascotas registradas.
Cada mascota puede mantener información relacionada con controles y vacunas.
Los controles pueden registrarse, visualizarse, editarse y eliminarse.
Las vacunas también pueden registrarse y administrarse desde la aplicación.
La información permanece almacenada localmente utilizando SQLite.
Gestión de mascotas: Cada mascota puede registrar información como nombre, especie, edad y raza.
Desde la sección de mascotas es posible consultar los registros existentes y acceder al detalle de cada mascota.
La aplicación permite actualizar los datos o eliminar una mascota cuando sea necesario.

Controles:PetHealth permite registrar controles asociados a una mascota.
Los controles pueden contener información como tipo, fecha, estado y observaciones.
Cada control queda relacionado con la mascota correspondiente.

Vacunas:La aplicación permite registrar vacunas asociadas a las mascotas.
Las vacunas incluyen información como nombre, fecha y estado.
Desde PetHealth es posible consultar y administrar estos registros.

Fotografía de la mascota:PetHealth permite utilizar la cámara del dispositivo para tomar una fotografía de la mascota.
La fotografía puede utilizarse como imagen de perfil dentro de la aplicación.
La imagen se almacena internamente en el dispositivo y SQLite mantiene la referencia correspondiente.
También es posible cambiar o eliminar la fotografía posteriormente.

Base de datos:PetHealth utiliza una base de datos SQLite local llamada: `pethealth.db`
La base de datos se administra mediante `SQLiteOpenHelper`.
Entre la información almacenada se encuentran los usuarios, mascotas, controles, vacunas y las referencias necesarias para mantener los datos asociados.
La información permanece disponible después de cerrar y volver a abrir la aplicación.

Sesión de usuario
PetHealth incorpora inicio de sesión y persistencia de sesión.
El sistema permite mantener al usuario autenticado mientras su sesión continúe activa.
La contraseña no se guarda directamente en SharedPreferences.
Cerrar sesión no elimina la información almacenada en SQLite.

Tecnologías utilizadas
Android Studio
Java
XML
Gradle Groovy
SQLite
SQLiteOpenHelper
SharedPreferences
Intent
Cámara mediante Android
Minimum SDK API 29

Estructura del proyecto
El proyecto utiliza una estructura sencilla basada en Activities, clases Java, layouts XML y almacenamiento local mediante SQLite.
Entre sus componentes principales se encuentran:
`DatabaseHelper`
`SessionManager`
`Mascota`
`Control`
`Vacuna`
`LoginActivity`
`RegistroActivity`
`InicioActivity`
`MisMascotasActivity`
`DetalleMascotaActivity`
`RegistrarMascotaActivity`
`ControlesActivity`
`VacunasActivity`
El proyecto mantiene una arquitectura sencilla acorde a los requerimientos de la asignatura.

Desarrollo progresivo
PetHealth fue desarrollado mediante distintos avances organizados en ramas dentro del repositorio.
Cada rama representa una etapa del desarrollo e incorporación de nuevas funcionalidades.
La versión actual y final del proyecto se encuentra disponible en `main`
La rama:`dev`, también contiene la versión actual utilizada durante el desarrollo.
Los avances anteriores permanecen disponibles para observar la evolución del proyecto.

El proyecto utiliza Java, XML y Gradle Groovy.
El `minSdk` configurado es API 29.
Puede ejecutarse mediante un emulador Android o utilizando un dispositivo físico mediante USB.
Para comprobar correctamente la función de fotografía utilizar un teléfono físico con cámara disponible.

Autora: Neiza Funes
Carrera: Ingeniería en Informática
Proyecto desarrollado en Android Studio
