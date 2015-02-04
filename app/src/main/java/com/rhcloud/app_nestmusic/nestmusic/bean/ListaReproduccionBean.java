package com.rhcloud.app_nestmusic.nestmusic.bean;

/**
 * Created by joseluis on 02/02/15.
 */
public class ListaReproduccionBean {

    private int id;
    private String nombre;
    private String fechaCreacion;

    public ListaReproduccionBean(int id, String nombre, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
    }

    public ListaReproduccionBean() {
    }

    @Override
    public String toString() {
        return "ListaReproduccionBean{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fechaCreacion='" + fechaCreacion + '\'' +
                '}';
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

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
