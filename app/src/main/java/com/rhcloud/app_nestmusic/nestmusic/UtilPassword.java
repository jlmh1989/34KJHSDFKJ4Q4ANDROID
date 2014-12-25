package com.rhcloud.app_nestmusic.nestmusic;

import android.util.Base64;

import java.util.Random;

/**
 * Created by joseluis on 18/12/14.
 */
public class UtilPassword {

    /**
     * Codifica password a base64
     *
     * @param password
     * @return
     */
    public static String encodePassword(String password) {
        Random ran = new Random();
        String inicio = getLpad(4, (int) (ran.nextDouble() * 1000));
        String fin = getLpad(4, (int) (ran.nextDouble() * 1000));
        password = inicio + password + fin;
        byte bytes[] = Base64.encode(password.getBytes(), 512);
        return new String(bytes);
    }

    /**
     * Rellena de ceros a la izquierda un valor numero, para cumplir con cierto
     * tamaño de longitud
     *
     * @param cantidad
     * @param valor
     * @return
     */
    private static String getLpad(int cantidad, int valor) {
        String valorStr = "" + valor;
        int tamValor = valorStr.length();
        int faltante = cantidad - tamValor;
        String cadenaRelleno = "";
        for (int i = 0; i < faltante; i++) {
            cadenaRelleno += "0";
        }
        return cadenaRelleno + valorStr;
    }
}
