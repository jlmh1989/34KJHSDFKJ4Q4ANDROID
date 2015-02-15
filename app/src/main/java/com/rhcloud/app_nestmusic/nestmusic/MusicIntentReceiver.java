package com.rhcloud.app_nestmusic.nestmusic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rhcloud.app_nestmusic.nestmusic.servicio.MusicaService;

/**
 * Created by joseluis on 2/14/15.
 */
public class MusicIntentReceiver extends android.content.BroadcastReceiver {

    private MusicaService srvMusica;

    public MusicIntentReceiver(MusicaService srvMusica){
        this.srvMusica = srvMusica;
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.w("MusicIntentReceiver", "Registrada");
        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            boolean isConnected = intent.getIntExtra("state", 0) == 1;
            if (isConnected) {
                Log.w("onReceive()", "Reanudar reproduccion");
                if (srvMusica.isPng()) {
                    srvMusica.go();
                }
            } else {
                Log.w("onReceive()", "Detener reproduccion");
                if (srvMusica.isPng()) {
                    srvMusica.pausePlayer();
                }
            }
        }
    }
}