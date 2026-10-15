package com.example.pethealth;

public class Usuario {

    private final long id;
    private final String nombre;
    private final String correo;
    private final String passwordHash;

    public Usuario(long id, String nombre, String correo, String passwordHash) {
        this.id = id;
        this.nombre = nombre == null ? "" : nombre;
        this.correo = correo == null ? "" : correo;
        this.passwordHash = passwordHash == null ? "" : passwordHash;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
