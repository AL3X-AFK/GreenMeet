package com.alenic.greenmeet.objects;

public class Accion {

    private String titulo;
    private String fecha;
    private String ubicacion;
    private String descripcion;
    private String imagenUrl;
    private long createdAt;

    public Accion() {} // obligatorio

    public Accion(String titulo, String fecha, String ubicacion,
                  String descripcion, String imagenUrl) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getUbicacion() { return ubicacion; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
}