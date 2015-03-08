package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.app.Activity;
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
public class ListaMusicaHDAdapter extends ListaMusicaAdapterAbstract{

    private final Activity contexto;
    private ArrayList<CancionBean> arrayMusica;
    private ArrayList<CancionBean> arrayMusicaOrginal;

    public ListaMusicaHDAdapter(Activity c, ArrayList<CancionBean> arrayMusica){
        super(c, R.layout.lista_musica, arrayMusica);
        this.contexto = c;
        this.arrayMusica = arrayMusica;
        arrayMusicaOrginal = new ArrayList<CancionBean>();
        arrayMusicaOrginal.addAll(arrayMusica);
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

        final CancionBean musica = arrayMusica.get(position);
        final ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = contexto.getLayoutInflater();
            convertView = inflater.inflate(R.layout.lista_musica, parent, false);

            viewHolder.icono = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.titulo = (TextView) convertView.findViewById(R.id.titulo);
            viewHolder.artista = (TextView) convertView.findViewById(R.id.artista);
            viewHolder.duracion = (TextView) convertView.findViewById(R.id.duracion);
            viewHolder.icon_play = (ImageView) convertView.findViewById(R.id.icon_play);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(musica.getImagen() != null){
            viewHolder.icono.post(new Runnable() {
                @Override
                public void run() {
                    viewHolder.icono.setImageBitmap(musica.getImagen());
                }
            });
        }
        if(musica.isPlaying()){
            viewHolder.icon_play.setVisibility(View.VISIBLE);
        }else{
            viewHolder.icon_play.setVisibility(View.GONE);
        }

        viewHolder.titulo.setText(musica.getTitulo());
        viewHolder.artista.setText(musica.getArtista());
        viewHolder.duracion.setText(musica.getDuracion());

        return convertView;
    }

    @Override
    public void setPlayIcon(int posicion) {
        int tam = arrayMusica.size();
        for (int i = 0; i < tam; i++){
            CancionBean cancionBean = arrayMusica.get(i);
            if(i == posicion) {
                cancionBean.setPlaying(true);
            }else {
                cancionBean.setPlaying(false);
            }
        }
        notifyDataSetChanged();
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView icono;
        TextView titulo;
        TextView artista;
        TextView duracion;
        ImageView icon_play;
    }
}
