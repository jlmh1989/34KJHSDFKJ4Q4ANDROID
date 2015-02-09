package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Jose Luis Martinez on 07/02/15.
 */
public class ListaMusicaHDAdapter extends BaseAdapter{

    private ArrayList<CancionBean> arrayMusica;
    private ArrayList<CancionBean> arrayMusicaOrginal;
    private LayoutInflater musicaInf;

    public ListaMusicaHDAdapter(Context c, ArrayList<CancionBean> arrayMusica){
        this.arrayMusica = arrayMusica;
        arrayMusicaOrginal = new ArrayList<CancionBean>();
        arrayMusicaOrginal.addAll(arrayMusica);
        musicaInf = LayoutInflater.from(c);
    }

    public void addMusica(CancionBean cancion){
        this.arrayMusica.add(cancion);
        this.arrayMusicaOrginal.add(cancion);
    }

    public void filtrar(String texto){
        arrayMusica.clear();
        if (texto.trim().length() == 0) {
            arrayMusica.addAll(arrayMusicaOrginal);
        } else {
            for (CancionBean cancionBean : arrayMusicaOrginal) {
                if (cancionBean.getTitulo().toLowerCase(Locale.getDefault()).contains(texto)) {
                    arrayMusica.add(cancionBean);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<CancionBean> getListaMusica(){
        return this.arrayMusica;
    }

    @Override
    public int getCount() {
        return arrayMusica.size();
    }

    @Override
    public CancionBean getItem(int position) {
        return arrayMusica.get(position);
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

        final CancionBean musica = arrayMusica.get(position);

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
