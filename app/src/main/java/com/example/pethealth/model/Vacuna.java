package com.example.pethealth.model;

/**
 * Modelo que representa una vacuna registrada para una mascota.
 */
public class Vacuna {

    private long id;
    private final long mascotaId;
    private final String nombre;
    private final String fecha;
    private final String estado;

    /**
     * Crea una vacuna con los datos almacenados en SQLite.
     *
     * @param id identificador de la vacuna
     * @param mascotaId mascota asociada
     * @param nombre nombre de la vacuna
     * @param fecha fecha registrada
     * @param estado estado de aplicación
     */
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

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getMascotaId() { return mascotaId; }
    public String getNombre() { return nombre; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
}
