package com.alenic.greenmeet.data;

public class Usuario {

    private String uid;
    private String nombre;
    private String telefono;
    private String genero;

    public Usuario() {}

    public Usuario(String nombre, String telefono, String genero) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.genero = genero;
    }

    public String getUid() { return uid; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getGenero() { return genero; }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }
}