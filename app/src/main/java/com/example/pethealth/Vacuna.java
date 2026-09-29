package com.example.pethealth;

public class Vacuna {

    private long id;
    private final long mascotaId;
    private final String nombre;
    private final String fecha;
    private final String estado;

    public Vacuna(long id, long mascotaId, String nombre, String fecha, String estado) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.nombre = nombre;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Vacuna(long mascotaId, String nombre, String fecha, String estado) {
        this(0, mascotaId, nombre, fecha, estado);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMascotaId() {
        return mascotaId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }
}
