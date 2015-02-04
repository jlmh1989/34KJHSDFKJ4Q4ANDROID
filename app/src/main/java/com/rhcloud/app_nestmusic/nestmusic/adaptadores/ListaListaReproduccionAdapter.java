package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.ListaReproduccionBean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseluis on 02/02/15.
 */
public class ListaListaReproduccionAdapter extends ArrayAdapter<ListaReproduccionBean> {

    private final Activity contexto;
    private ArrayList<ListaReproduccionBean> listaReproduccionBeans;
    private SparseBooleanArray itemsSeleccionados;

    public ListaListaReproduccionAdapter(Activity contexto, ArrayList<ListaReproduccionBean> listaReproduccionBeans){
        super(contexto, R.layout.lista_lista_reprod, listaReproduccionBeans);
        itemsSeleccionados = new SparseBooleanArray();
        this.contexto = contexto;
        this.listaReproduccionBeans = listaReproduccionBeans;
    }

    public void limpiarLista(){
        this.listaReproduccionBeans.clear();
    }

    public void addListaReproduccion(ListaReproduccionBean listaReproduccionBean){
        this.listaReproduccionBeans.add(listaReproduccionBean);
    }

    @Override
    public void remove(ListaReproduccionBean object) {
        listaReproduccionBeans.remove(object);
        notifyDataSetChanged();
    }

    public List<ListaReproduccionBean> getListaReproduccionBeans(){
        return listaReproduccionBeans;
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

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = contexto.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.lista_lista_reprod, parent, false);

        TextView nombre = (TextView) rowView.findViewById(R.id.nombreLista);
        TextView fechaCreacion = (TextView) rowView.findViewById(R.id.fechaCreacion);

        ListaReproduccionBean listaReproduccionBean = this.listaReproduccionBeans.get(position);

        nombre.setText(listaReproduccionBean.getNombre());
        fechaCreacion.setText(listaReproduccionBean.getFechaCreacion());

        return rowView;
    }

}
