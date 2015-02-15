package com.rhcloud.app_nestmusic.nestmusic.musica;

import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;

/**
 * Created by joseluis on 2/15/15.
 */
public interface MusicaCallbacks {
    void setListaCancionesMusica(ArrayList<CancionBean> canciones);
    void setTituloActivityMusica(String titulo);
    void setPosicionMusicaReproducir(int posicion);
}
