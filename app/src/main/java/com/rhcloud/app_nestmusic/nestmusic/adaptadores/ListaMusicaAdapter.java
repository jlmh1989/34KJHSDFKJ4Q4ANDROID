package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by joseluis on 18/01/15.
 */
public class ListaMusicaAdapter extends ArrayAdapter<CancionBean> {

    private final Activity contexto;
    private ArrayList<CancionBean> listaCancion;
    private ArrayList<CancionBean> listaOriginal;
    private SparseBooleanArray itemsSeleccionados;

    public ListaMusicaAdapter(Activity contexto, ArrayList<CancionBean> listaCancion){
        super(contexto, R.layout.lista_musica, listaCancion);
        itemsSeleccionados = new SparseBooleanArray();
        this.contexto = contexto;
        this.listaCancion = listaCancion;
        this.listaOriginal = new ArrayList<CancionBean>();
        this.listaOriginal.addAll(listaCancion);
    }

    public void limpiarLista(){
        this.listaCancion.clear();
    }

    public void addCancion(CancionBean cancion){
        this.listaCancion.add(cancion);
        this.listaOriginal.add(cancion);
    }

    @Override
    public void remove(CancionBean object) {
        listaCancion.remove(object);
        notifyDataSetChanged();
    }

    public List<CancionBean> getListaCancion(){
        return listaCancion;
    }

    public void toggleSelection(int posicion){
        selectedView(posicion, !itemsSeleccionados.get(posicion));
    }

    public void removeSelection(){
        itemsSeleccionados = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectedView(int posicion, boolean value){
        if(value){
            itemsSeleccionados.put(posicion, value);
        }else {
            itemsSeleccionados.delete(posicion);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount(){
        return itemsSeleccionados.size();
    }

    public SparseBooleanArray getItemsSeleccionados(){
        return itemsSeleccionados;
    }

    public void filtrar(String texto){
        listaCancion.clear();
        if(texto.trim().length() == 0){
            listaCancion.addAll(listaOriginal);
        }else {
            for (CancionBean cancionBean : listaOriginal){
                if (cancionBean.getTitulo().toLowerCase(Locale.getDefault()).contains(texto)){
                    listaCancion.add(cancionBean);
                }
            }
        }
        notifyDataSetChanged();
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
