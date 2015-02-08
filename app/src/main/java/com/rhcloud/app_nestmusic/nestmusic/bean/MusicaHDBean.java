package com.rhcloud.app_nestmusic.nestmusic.bean;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by joseluis on 07/02/15.
 */
public class MusicaHDBean {

    private long id;
    private String titulo;
    private String artista;
    private String duracion;
    private Bitmap imagen;
    private Uri pathMusica;

    public MusicaHDBean(long id, String titulo, String artista, String duracion) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
    }

    public MusicaHDBean() {
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public Uri getPathMusica() {
        return pathMusica;
    }

    public void setPathMusica(Uri pathMusica) {
        this.pathMusica = pathMusica;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }
}
