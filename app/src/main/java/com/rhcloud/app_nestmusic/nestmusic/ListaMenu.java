package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by joseluis on 14/01/15.
 */
public class ListaMenu extends ArrayAdapter<String> {

    private final Activity contexto;
    private final String[] texto;
    private final Integer[] imagenId;

    public ListaMenu(Activity contexto, String[] texto, Integer[] imagenId){
        super(contexto, R.layout.lista_menu, texto);
        this.contexto = contexto;
        this.texto = texto;
        this.imagenId = imagenId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = contexto.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.lista_menu, null);
        TextView texto = (TextView) rowView.findViewById(R.id.texto);
        ImageView imagen = (ImageView) rowView.findViewById(R.id.imagen);
        texto.setText(this.texto[position]);
        imagen.setImageResource(this.imagenId[position]);
        return rowView;
    }

}
