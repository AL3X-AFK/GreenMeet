package com.alenic.greenmeet.data;

import com.google.firebase.firestore.Exclude;

public class User {
    @Exclude
    private String uid;
    private String nombre;
    private String telefono;
    private String genero;
    private String imagenProfileURL;

    public User() {
    }

    public User(String nombre, String telefono, String genero) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.genero = genero;
    }
    @Exclude
    public String getUid() {
        return uid;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getGenero() {
        return genero;
    }
    public String getImagenProfileURL() { return imagenProfileURL;  }
    @Exclude
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

    public void setImagenProfileURL(String imagenProfileURL) { this.imagenProfileURL = imagenProfileURL;}
}