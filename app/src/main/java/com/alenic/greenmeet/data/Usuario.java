package com.alenic.greenmeet.data;

public class Usuario {

    private String nombre;
    private String telefono;
    private String genero;

    public Usuario() {}

    public Usuario(String nombre, String telefono, String genero) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.genero = genero;
    }

    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getGenero() { return genero; }
}