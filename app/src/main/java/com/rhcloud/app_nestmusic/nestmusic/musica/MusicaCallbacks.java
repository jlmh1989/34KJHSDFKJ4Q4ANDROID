package com.rhcloud.app_nestmusic.nestmusic.musica;

import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaAdapterAbstract;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;

/**
 * Created by joseluis on 2/15/15.
 */
public interface MusicaCallbacks {
    void setListaCanciones(ArrayList<CancionBean> canciones);
    void setTituloActivity(String titulo);
    void setPosicionMusicaReproducir(int posicion);
    void setAdapterAbstract(ListaMusicaAdapterAbstract adapterAbstract);
    void onTouchList();
}
