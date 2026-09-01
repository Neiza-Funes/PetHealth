PetHealth - Avance 02
=====================

Objetivo del avance:
- Continuar el proyecto PetHealth existente sin crearlo desde cero.
- Mantener Java + XML + Groovy DSL.
- Agregar varias vistas XML y navegación entre Activities.
- Incorporar validaciones simples y elementos interactivos.
- Mantener el nivel adecuado para un Avance 02 universitario.

Tecnología conservada del Avance 01:
- Paquete: com.example.pethealth
- Java
- Layouts XML
- Groovy DSL
- Minimum SDK: API 29
- Compile/Target SDK: 35

Flujo de la aplicación:
Pantalla inicial -> COMENZAR -> Login
Login -> Registro
Login válido -> Inicio
Registro válido -> vuelve al Login

Activities:
1. MainActivity.java
   - Carga activity_main.xml con setContentView().
   - Usa findViewById() para obtener el botón COMENZAR.
   - Usa setOnClickListener() e Intent para abrir LoginActivity.
   - Incluye FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS para personalizar la barra de estado.

2. LoginActivity.java
   - Carga activity_login.xml.
   - Valida correo y contraseña con setError().
   - Permite mostrar/ocultar contraseña.
   - Usa Toast para mensajes sencillos.
   - Usa Intent para ir a RegistroActivity e InicioActivity.

3. RegistroActivity.java
   - Carga activity_registro.xml.
   - Incluye nombre, correo, teléfono, contraseña y confirmación.
   - Valida campos, formato de correo, mínimo de 6 caracteres y coincidencia de contraseñas.
   - No guarda datos todavía.

4. InicioActivity.java
   - Carga activity_inicio.xml.
   - Muestra opciones: Mis mascotas, Registrar mascota, Próximos controles y Vacunas.
   - Las opciones muestran un Toast indicando que estarán disponibles en próximos avances.

Layouts XML:
- activity_main.xml
- activity_login.xml
- activity_registro.xml
- activity_inicio.xml

Recursos:
- colors.xml: identidad rojo, rosado y blanco.
- strings.xml: textos principales y mensajes.
- drawable/: fondos sencillos y logo de PetHealth.

Flag utilizado:
WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
Permite que la Activity pueda definir el color de la barra de estado. Se usa de manera sencilla en MainActivity para mantener la identidad visual de PetHealth.

Importante:
- No se agregó Firebase.
- No se agregó base de datos.
- No se agregó Room, Retrofit ni APIs.
- No existen usuarios o contraseñas fijas.
- Las funciones veterinarias completas se dejan para avances posteriores.

Pruebas recomendadas antes del commit:
1. Ejecutar la app y comprobar la pantalla inicial.
2. Presionar COMENZAR y verificar que abra Login.
3. Probar correo vacío, correo inválido y contraseña corta.
4. Abrir Registro y probar sus validaciones.
5. Crear una cuenta con datos válidos y comprobar que regrese al Login.
6. Ingresar un correo válido y contraseña de 6 o más caracteres.
7. Verificar que abra Inicio.
8. Presionar los cuatro botones de Inicio y comprobar los Toast.

No se realizó commit ni push desde este archivo generado.
