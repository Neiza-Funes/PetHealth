package com.example.pethealth;

public class Control {

    private long id;
    private final long mascotaId;
    private final String tipo;
    private final String fecha;
    private final String estado;
    private final String observacion;

    public Control(long id, long mascotaId, String tipo, String fecha, String estado, String observacion) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.tipo = tipo;
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = observacion == null ? "" : observacion;
    }

    public Control(long mascotaId, String tipo, String fecha, String estado, String observacion) {
        this(0, mascotaId, tipo, fecha, estado, observacion);
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

    public String getTipo() {
        return tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservacion() {
        return observacion;
    }
}
