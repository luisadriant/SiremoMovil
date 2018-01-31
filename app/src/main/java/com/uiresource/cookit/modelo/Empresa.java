package com.uiresource.cookit.modelo;

import com.uiresource.cookit.recycler.Vestimenta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisl on 1/9/2018.
 */

public class Empresa {
    private int id;
    private String nombre;
    private String descripcion;
    private String ruc;
    private String direccion;
    private String contacto;
    private List<Vestimenta> vestimentas;

    public List<Vestimenta> getVestimentas() {
        return vestimentas;
    }
    public void setVestimentas(List<Vestimenta> vestimentas) {
        this.vestimentas = vestimentas;
    }
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
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getRuc() {
        return ruc;
    }
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getContacto() {
        return contacto;
    }
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    public void addVestimenta(Vestimenta v) {
        if (vestimentas==null)
            vestimentas=new ArrayList<Vestimenta>();
        vestimentas.add(v);
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ruc='" + ruc + '\'' +
                ", direccion='" + direccion + '\'' +
                ", contacto='" + contacto + '\'' +
                '}';
    }
}
