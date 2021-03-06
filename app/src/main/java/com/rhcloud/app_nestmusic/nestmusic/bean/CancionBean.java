package com.rhcloud.app_nestmusic.nestmusic.bean;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by joseluis on 31/01/15.
 */
public class CancionBean {

    private Integer imagenId;
    private String titulo;
    private String artista;
    private String artistaOrig;
    private String duracion;
    private String duracionOrig;
    private Bitmap imagen;
    private Uri pathMusica;
    private String urlMusica;
    private String urlParent;
    private long id;
    private boolean isOnline;
    private boolean isPlaying;

    public CancionBean(){
        this.isOnline = false;
        this.isPlaying = false;
    }

    public String getArtistaOrig() {
        return artistaOrig;
    }

    public void setArtistaOrig(String artistaOrig) {
        this.artistaOrig = artistaOrig;
    }

    public String getDuracionOrig() {
        return duracionOrig;
    }

    public void setDuracionOrig(String duracionOrig) {
        this.duracionOrig = duracionOrig;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
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

    public String getUrlMusica() {
        return urlMusica;
    }

    public void setUrlMusica(String urlMusica) {
        this.urlMusica = urlMusica;
    }

    public String getUrlParent() {
        return urlParent;
    }

    public void setUrlParent(String urlParent) {
        this.urlParent = urlParent;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
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

    @Override
    public String toString() {
        return "CancionBean{" +
                "imagenId=" + imagenId +
                ", titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", duracion='" + duracion + '\'' +
                ", imagen=" + imagen +
                ", pathMusica=" + pathMusica +
                ", urlMusica='" + urlMusica + '\'' +
                ", urlParent='" + urlParent + '\'' +
                ", id=" + id +
                '}';
    }
}
