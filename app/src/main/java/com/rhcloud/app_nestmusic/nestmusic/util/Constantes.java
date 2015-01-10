package com.rhcloud.app_nestmusic.nestmusic.util;

/**
 * Created by joseluis on 24/12/14.
 */
public class Constantes {

    public static final String NOMBRE_TABLA_SESION = "SESION";

    public static final String USUARIO = "USUARIO";
    public static final String TOKEN = "TOKEN";

    public static final String SQL_CREAR_TABLA_SESION = "CREATE TABLE "+ NOMBRE_TABLA_SESION +" (USUARIO TEXT, TOKEN TEXT)";

    public static final String BASE_DATOS_NOMBRE = "NEST_MUSIC";

    public static final String CONSULTA_SESION_TODOS = "SELECT USUARIO, TOKEN FROM " + NOMBRE_TABLA_SESION;

    public static final int CONEXION_TIMEOUT = 10000;
    public static final int SOCKET_TIMEOUT = 10000;
    public static final String SERVER = "http://10.0.2.2:8080/servicios";

    public static final String INGRESO_ENDPOINT = SERVER + "/servicios/rest/usuario/login?";
    public static final String REGISTRO_ENDPOINT = SERVER + "/servicios/rest/usuario/registro?";
    public static final String RECUPERARPASS_ENDPOINT = SERVER + "/servicios/rest/usuario/recuperarDatos?";
    public static final String SOLICITUDVALIDACION_ENDPOINT = SERVER + "/servicios/rest/usuario/reenviarCorreoValidacion?";

    public static final int FAVORITOS = 0;
    public static final int LISTA_REPROD = 1;
    public static final int HISTO_REPROD = 2;
    public static final int DESCARGAS = 3;
    public static final int CERRAR_SESION = 4;

}