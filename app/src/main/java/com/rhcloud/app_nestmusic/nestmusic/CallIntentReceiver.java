package com.rhcloud.app_nestmusic.nestmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by joseluis on 2/15/15.
 */
public class CallIntentReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("CallIntentReceiver", "Registrada");
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = "";
        Bundle bundle = intent.getExtras();
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // Phone is ringing
            number = bundle.getString("incoming_number");
            Log.w("LlamadaEntrante", number);
        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // Call received
            Log.w("LlamadaEnProceso", number);
        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.w("LlamadaTerminada", number);
        }
    }
}
