package com.alenic.greenmeet.data;

import com.google.firebase.firestore.Exclude;

public class Duda {
    @Exclude
    private String id;
    private String actUid;
    private String creadorActUid;
    private String userUidPregunta;
    private String nombreAutor;
    private String pregunta;
    private String tituloActividad;
    private String respuesta;
    private boolean respondida;
    private long fechaCreacion;
    private boolean leidaUsuario;

    public Duda() {}

    public Duda(String actUid, String creadorActUid, String userUidPregunta, String nombreAutor, String pregunta,String tituloActividad) {
        this.actUid = actUid;
        this.creadorActUid = creadorActUid;
        this.userUidPregunta = userUidPregunta;
        this.nombreAutor = nombreAutor;
        this.pregunta = pregunta;
        this.respuesta = "";
        this.tituloActividad = tituloActividad;
        this.respondida = false;
        this.fechaCreacion = System.currentTimeMillis();
        this.leidaUsuario = false;
    }

    // Getters y Setters
    public String getId() { return id; }

    @Exclude
    public void setId(String id) { this.id = id; }
    public String getActUid() { return actUid; }
    public void setActUid(String actUid) { this.actUid = actUid; }
    public String getCreadorActUid() { return creadorActUid; }
    public void setCreadorActUid(String creadorActUid) { this.creadorActUid = creadorActUid; }
    public String getUserUidPregunta() { return userUidPregunta; }
    public void setUserUidPregunta(String userUidPregunta) { this.userUidPregunta = userUidPregunta; }
    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }
    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
    public String getTituloActividad() { return tituloActividad; }
    public void setTituloActividad(String tituloActividad) { this.tituloActividad = tituloActividad; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public boolean isRespondida() { return respondida; }
    public void setRespondida(boolean respondida) { this.respondida = respondida; }
    public long getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(long fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public boolean isLeidaUsuario() { return leidaUsuario; }
    public void setLeidaUsuario(boolean leidaUsuario) { this.leidaUsuario = leidaUsuario; }
}