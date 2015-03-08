package com.rhcloud.app_nestmusic.nestmusic.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;

import java.util.ArrayList;

/**
 * Created by joseluis on 3/7/15.
 */
public abstract class ListaMusicaAdapterAbstract extends ArrayAdapter<CancionBean> {

    public ListaMusicaAdapterAbstract(Context context, int resource, ArrayList<CancionBean> objects) {
        super(context, resource, objects);
    }

    public abstract void setPlayIcon(int posicion);
}
