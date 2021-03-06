package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jose Luis Martinez on 18/01/15.
 */
public class ListaMusicaAdapter extends ListaMusicaAdapterAbstract {

    private final Activity contexto;
    private ArrayList<CancionBean> listaCancion;
    private ArrayList<CancionBean> listaOriginal;
    private SparseBooleanArray itemsSeleccionados;
    private int alturaNormal;
    private int altura90dp;

    public ListaMusicaAdapter(Activity contexto, ArrayList<CancionBean> listaCancion){
        super(contexto, R.layout.lista_musica_v2, listaCancion);
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

    public ArrayList<CancionBean> getListaCancion(){
        return listaCancion;
    }

    public void toggleSelection(int posicion){
        selectedView(posicion, !itemsSeleccionados.get(posicion));
    }

    public void removeSelection(){
        itemsSeleccionados = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    void selectedView(int posicion, boolean value){
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
        if (texto.trim().length() == 0) {
            listaCancion.addAll(listaOriginal);
        } else {
            for (CancionBean cancionBean : listaOriginal) {
                if (cancionBean.getTitulo().toLowerCase(Locale.getDefault()).contains(texto)) {
                    listaCancion.add(cancionBean);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setPlayIcon(int posicion){
        int tam = listaCancion.size();
        for (int i = 0; i < tam; i++){
            CancionBean cancionBean = listaCancion.get(i);
            if(i == posicion) {
                cancionBean.setPlaying(true);
            }else {
                cancionBean.setPlaying(false);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){

        CancionBean cancion = this.listaCancion.get(position);

        ViewHolder viewHolder;

        if (view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = contexto.getLayoutInflater();
            view = inflater.inflate(R.layout.lista_musica_v2, parent, false);

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            TypedValue value = new TypedValue();
            DisplayMetrics metrics = new DisplayMetrics();
            getContext().getTheme().resolveAttribute(
                    android.R.attr.listPreferredItemHeight, value, true);
            ((WindowManager) (getContext().getSystemService(getContext().WINDOW_SERVICE)))
                    .getDefaultDisplay().getMetrics(metrics);
            layoutParams.height = (int) TypedValue.complexToDimension(value.data, metrics);
            alturaNormal = layoutParams.height;
            altura90dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getContext().getResources().getDisplayMetrics());
            viewHolder.titulo = (TextView) view.findViewById(R.id.titulo);
            viewHolder.artista = (TextView) view.findViewById(R.id.artista);
            viewHolder.duracion = (TextView) view.findViewById(R.id.duracion);
            viewHolder.icon_play = (ImageView) view.findViewById(R.id.icon_play);
            viewHolder.icon_favorito = (ImageView) view.findViewById(R.id.icon_favorito);
            viewHolder.icon_listaRep = (ImageView) view.findViewById(R.id.icon_listaRep);
            viewHolder.icon_descargar = (ImageView) view.findViewById(R.id.icon_descargar);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.titulo.setText(cancion.getTitulo());
        viewHolder.artista.setText(cancion.getArtista());
        viewHolder.duracion.setText(cancion.getDuracion());

        if(cancion.isPlaying()){
            viewHolder.icon_play.setImageResource(R.drawable.play_activo);
            viewHolder.icon_play.setVisibility(View.VISIBLE);
            viewHolder.icon_favorito.setVisibility(View.VISIBLE);
            viewHolder.icon_listaRep.setVisibility(View.VISIBLE);
            viewHolder.icon_descargar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = altura90dp;
        }else{
            viewHolder.icon_play.setImageResource(R.drawable.play_inactivo);
            viewHolder.icon_play.setVisibility(View.GONE);
            viewHolder.icon_favorito.setVisibility(View.GONE);
            viewHolder.icon_listaRep.setVisibility(View.GONE);
            viewHolder.icon_descargar.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = alturaNormal;
        }

        return view;
    }

    private static class ViewHolder {
        TextView titulo;
        TextView artista;
        TextView duracion;
        ImageView icon_play;
        ImageView icon_favorito;
        ImageView icon_listaRep;
        ImageView icon_descargar;
    }

}
