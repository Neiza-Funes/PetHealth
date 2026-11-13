package com.example.pethealth.model;

/**
 * Modelo que representa un registro de desparasitación asociado a una mascota.
 */
public class Desparasitacion {

    private long id;
    private final long mascotaId;
    private final String mascotaNombre;
    private final String producto;
    private final String fecha;
    private final String proximaFecha;
    private final String observacion;

    /**
     * Crea un registro completo recuperado desde SQLite.
     *
     * @param id identificador del registro
     * @param mascotaId mascota asociada
     * @param mascotaNombre nombre de la mascota mostrado en el listado
     * @param producto producto utilizado
     * @param fecha fecha de aplicación
     * @param proximaFecha próxima fecha indicada
     * @param observacion observación opcional
     */
    public Desparasitacion(long id, long mascotaId, String mascotaNombre, String producto,
                           String fecha, String proximaFecha, String observacion) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.mascotaNombre = mascotaNombre == null ? "" : mascotaNombre;
        this.producto = producto;
        this.fecha = fecha;
        this.proximaFecha = proximaFecha;
        this.observacion = observacion == null ? "" : observacion;
    }

    public Desparasitacion(long mascotaId, String producto, String fecha,
                           String proximaFecha, String observacion) {
        this(0, mascotaId, "", producto, fecha, proximaFecha, observacion);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getMascotaId() { return mascotaId; }
    public String getMascotaNombre() { return mascotaNombre; }
    public String getProducto() { return producto; }
    public String getFecha() { return fecha; }
    public String getProximaFecha() { return proximaFecha; }
    public String getObservacion() { return observacion; }
}
