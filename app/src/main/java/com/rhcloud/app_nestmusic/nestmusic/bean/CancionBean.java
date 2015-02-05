package com.rhcloud.app_nestmusic.nestmusic.bean;

/**
 * Created by joseluis on 31/01/15.
 */
public class CancionBean {

    private Integer imagenId;
    private String titulo;
    private String artista;
    private String duracion;
    private int id;

    public CancionBean(int id, Integer imagenId, String titulo, String artista, String duracion) {
        this.id = id;
        this.imagenId = imagenId;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
    }

    public CancionBean(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImagenId(Integer imagenId) {
        this.imagenId = imagenId;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public Integer getImagenId() {
        return imagenId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArtista() {
        return artista;
    }

    public String getDuracion() {
        return duracion;
    }
}
