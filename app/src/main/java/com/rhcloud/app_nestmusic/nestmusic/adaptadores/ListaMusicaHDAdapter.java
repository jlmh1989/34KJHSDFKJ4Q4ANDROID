package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.MusicaHDBean;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by joseluis on 07/02/15.
 */
public class ListaMusicaHDAdapter extends BaseAdapter{

    private ArrayList<MusicaHDBean> arrayMusica;
    private ArrayList<MusicaHDBean> arrayMusicaOrginal;
    private LayoutInflater musicaInf;

    public ListaMusicaHDAdapter(Context c, ArrayList<MusicaHDBean> arrayMusica){
        this.arrayMusica = arrayMusica;
        arrayMusicaOrginal = new ArrayList<MusicaHDBean>();
        arrayMusicaOrginal.addAll(arrayMusica);
        musicaInf = LayoutInflater.from(c);
    }

    public void addMusica(MusicaHDBean cancion){
        this.arrayMusica.add(cancion);
        this.arrayMusicaOrginal.add(cancion);
    }

    public void filtrar(String texto){
        arrayMusica.clear();
        if (texto.trim().length() == 0) {
            arrayMusica.addAll(arrayMusicaOrginal);
        } else {
            for (MusicaHDBean cancionBean : arrayMusicaOrginal) {
                if (cancionBean.getTitulo().toLowerCase(Locale.getDefault()).contains(texto)) {
                    arrayMusica.add(cancionBean);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrayMusica.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout musicaLayout = (RelativeLayout) musicaInf.inflate(R.layout.lista_musica, parent, false);

        final ImageView icono = (ImageView) musicaLayout.findViewById(R.id.icon);
        TextView titulo = (TextView) musicaLayout.findViewById(R.id.titulo);
        TextView artista = (TextView) musicaLayout.findViewById(R.id.artista);
        TextView duracion = (TextView) musicaLayout.findViewById(R.id.duracion);

        final MusicaHDBean musica = arrayMusica.get(position);

        if(musica.getImagen() != null){
            icono.post(new Runnable() {
                @Override
                public void run() {
                    icono.setImageBitmap(musica.getImagen());
                }
            });
        }
        titulo.setText(musica.getTitulo());
        artista.setText(musica.getArtista());
        duracion.setText(musica.getDuracion());

        musicaLayout.setTag(position);

        return musicaLayout;
    }
}
