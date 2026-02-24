package com.alenic.greenmeet.data;

public class Act {
    private String uid;
    private String titulo;
    private String descripcion;
    private String categoria;
    private long fecha;
    private String ubicacion;
    private String imagenUrl;
    private long fechaCreacion;
    private String userUid;

    public Act() {
    }

    public Act(String titulo,
               String categoria,
               long fecha,
               String ubicacion,
               String descripcion,
               String imagenUrl,
               String ownerUid) {

        this.titulo = titulo;
        this.categoria = categoria;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.userUid = ownerUid;
        this.fechaCreacion = System.currentTimeMillis();
    }

    // Getters y setters
    public String getUid() {
        return uid;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public long getFecha() {
        return fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

}
