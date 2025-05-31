package com.colmena.model;

public class Rol {
    private int id;
    private String nombre;

    // constructores 
    public Rol() {
    }
    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    //gettes y setters 
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
