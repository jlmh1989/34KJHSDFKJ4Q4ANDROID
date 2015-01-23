package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by joseluis on 18/01/15.
 */
public class ListaMusica extends ArrayAdapter<String> {

    private final Activity contexto;
    private final Integer[] imagenId;
    private final String[] titulo;
    private final String[] artista;
    private final String[] duracion;

    public ListaMusica(Activity contexto, Integer[] imagenId, String[] titulo, String[] artista, String[] duracion){
        super(contexto, R.layout.lista_musica, titulo);
        this.contexto = contexto;
        this.imagenId = imagenId;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = contexto.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.lista_musica, parent, false);

        ImageView icono = (ImageView) rowView.findViewById(R.id.icon);
        TextView titulo = (TextView) rowView.findViewById(R.id.titulo);
        TextView artista = (TextView) rowView.findViewById(R.id.artista);
        TextView duracion = (TextView) rowView.findViewById(R.id.duracion);

        icono.setImageResource(imagenId[position]);
        titulo.setText(this.titulo[position]);
        artista.setText(this.artista[position]);
        duracion.setText(this.duracion[position]);

        return rowView;
    }

}
