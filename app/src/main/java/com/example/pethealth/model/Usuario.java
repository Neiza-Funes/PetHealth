package com.example.pethealth.model;

/**
 * Representa un usuario registrado en PetHealth.
 * Conserva los datos básicos utilizados para autenticación y asociación de la sesión local.
 */
public class Usuario {

    private final long id;
    private final String nombre;
    private final String correo;
    private final String passwordHash;

    /**
     * Crea un usuario con los datos recuperados desde SQLite.
     *
     * @param id identificador del usuario
     * @param nombre nombre visible del usuario
     * @param correo correo utilizado para iniciar sesión
     * @param passwordHash hash almacenado para validar la contraseña
     */
    public Usuario(long id, String nombre, String correo, String passwordHash) {
        this.id = id;
        this.nombre = nombre == null ? "" : nombre;
        this.correo = correo == null ? "" : correo;
        this.passwordHash = passwordHash == null ? "" : passwordHash;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getPasswordHash() { return passwordHash; }
}
