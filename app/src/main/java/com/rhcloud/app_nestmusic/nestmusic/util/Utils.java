package com.rhcloud.app_nestmusic.nestmusic.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by joseluis on 24/12/14.
 */
public class Utils {

    public static void mostrarNotificacion(Context context, String mensaje){
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }

    public static boolean conectadoInternet(Activity activity){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        }catch (Exception e){
            return false;
        }
    }
}
