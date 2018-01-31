package com.uiresource.cookit.modelo;

/**
 * Created by luisl on 1/9/2018.
 */

public class Marca {

    private int id;
    private String nombre;

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

    @Override
    public String toString() {
        return  nombre;
    }
}
