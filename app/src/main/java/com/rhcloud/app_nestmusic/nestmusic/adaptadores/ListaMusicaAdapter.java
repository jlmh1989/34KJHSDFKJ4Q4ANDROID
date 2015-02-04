package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;

/**
 * Created by joseluis on 18/01/15.
 */
public class ListaMusicaAdapter extends ArrayAdapter<CancionBean> {

    private final Activity contexto;
    private ArrayList<CancionBean> listaCancion;

    public ListaMusicaAdapter(Activity contexto, ArrayList<CancionBean> listaCancion){
        super(contexto, R.layout.lista_musica, listaCancion);
        this.contexto = contexto;
        this.listaCancion = listaCancion;
    }

    public void limpiarLista(){
        this.listaCancion.clear();
    }

    public void addCancion(CancionBean cancion){
        this.listaCancion.add(cancion);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = contexto.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.lista_musica, parent, false);

        ImageView icono = (ImageView) rowView.findViewById(R.id.icon);
        TextView titulo = (TextView) rowView.findViewById(R.id.titulo);
        TextView artista = (TextView) rowView.findViewById(R.id.artista);
        TextView duracion = (TextView) rowView.findViewById(R.id.duracion);

        CancionBean cancion = this.listaCancion.get(position);

        icono.setImageResource(cancion.getImagenId());
        titulo.setText(cancion.getTitulo());
        artista.setText(cancion.getArtista());
        duracion.setText(cancion.getDuracion());

        return rowView;
    }

}
