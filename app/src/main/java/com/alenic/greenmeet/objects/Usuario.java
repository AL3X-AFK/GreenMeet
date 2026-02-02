package com.alenic.greenmeet.objects;

public class Usuario {
    private String nombre;
    private String email;
    private String telefono;
    private String genero;

    public Usuario() {} //esto es obligatorio para Firebase

    public Usuario(String nombre, String email, String telefono, String genero) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.genero = genero;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getGenero() { return genero; }
}
