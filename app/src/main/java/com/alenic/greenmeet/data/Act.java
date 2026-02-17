package com.alenic.greenmeet.data;

import java.util.ArrayList;
import java.util.List;

public class Act {
    private String id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String fecha;
    private String ubicacion;
    private String imagenUrl;
    private long createdAt;
    private String ownerUid;

    private List<String> participantes;

    public Act() { }

    public Act(String titulo,String categoria, String fecha, String ubicacion,
               String descripcion, String imagenUrl) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.createdAt = System.currentTimeMillis();
        this.participantes = new ArrayList<>();
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }
    public String getUbicacion() { return ubicacion; }
    public String getImagenUrl() { return imagenUrl; }
    public long getCreatedAt() { return createdAt; }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }
    public List<String> getParticipantes() { return participantes; }

    public void setParticipantes(List<String> participantes) {
        this.participantes = participantes;
    }

}
