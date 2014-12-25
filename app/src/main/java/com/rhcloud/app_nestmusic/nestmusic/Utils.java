package com.rhcloud.app_nestmusic.nestmusic;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by joseluis on 24/12/14.
 */
public class Utils {

    public static void mostrarNotificacion(Context context, String mensaje){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
