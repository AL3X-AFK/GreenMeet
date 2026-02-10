package com.alenic.greenmeet.data;

import kotlin.contracts.Returns;

public class Act {

    private String titulo;
    private String descripcion;
    private String categoria;
    private String fecha;
    private String ubicacion;
    private String imagenUrl;
    private long createdAt;

    public Act() {
    }

    public Act(String titulo,String categoria, String fecha, String ubicacion,
               String descripcion, String imagenUrl) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.createdAt = System.currentTimeMillis();
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCategoria(){
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}