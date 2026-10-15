package com.example.pethealth;

public class Mascota {

    private long id;
    private long usuarioId;
    private final String nombre;
    private final String especie;
    private final int edad;
    private final String raza;
    private final String fotoPath;

    public Mascota(long id, String nombre, String especie, int edad, String raza) {
        this(id, 0, nombre, especie, edad, raza, "");
    }

    public Mascota(long id, String nombre, String especie, int edad, String raza, String fotoPath) {
        this(id, 0, nombre, especie, edad, raza, fotoPath);
    }

    public Mascota(long id, long usuarioId, String nombre, String especie, int edad, String raza, String fotoPath) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.especie = especie;
        this.edad = edad;
        this.raza = raza == null ? "" : raza;
        this.fotoPath = fotoPath == null ? "" : fotoPath;
    }

    public Mascota(String nombre, String especie, int edad, String raza) {
        this(0, 0, nombre, especie, edad, raza, "");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public int getEdad() {
        return edad;
    }

    public String getRaza() {
        return raza;
    }

    public String getFotoPath() {
        return fotoPath;
    }
}
