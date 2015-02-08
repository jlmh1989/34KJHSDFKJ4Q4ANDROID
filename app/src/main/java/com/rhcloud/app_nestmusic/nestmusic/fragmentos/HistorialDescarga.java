package com.rhcloud.app_nestmusic.nestmusic.fragmentos;


import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaAdapter;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;
import com.rhcloud.app_nestmusic.nestmusic.util.Constantes;
import com.rhcloud.app_nestmusic.nestmusic.util.UtilPassword;
import com.rhcloud.app_nestmusic.nestmusic.util.Utils;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistorialDescarga extends Fragment {


    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listaCancion;
    private ProgressBar cargando;
    private TextView mensajeHistorialDescarga;
    private ListaMusicaAdapter adapterMusica;
    private ArrayList<CancionBean> arrayCancion;

    public static HistorialDescarga newInstance(int sectionNumber, String usuario, String token){
        HistorialDescarga fragment = new HistorialDescarga();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(Constantes.USUARIO, usuario);
        args.putString(Constantes.TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public HistorialDescarga() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_historial_descarga, container, false);

        arrayCancion = new ArrayList<CancionBean>();
        adapterMusica = new ListaMusicaAdapter(this.getActivity(), arrayCancion);

        listaCancion = (ListView) rootView.findViewById(R.id.listaCancion);
        listaCancion.setAdapter(adapterMusica);
        listaCancion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accion seleccion
            }
        });

        cargando = (ProgressBar) rootView.findViewById(R.id.cargando);
        mensajeHistorialDescarga = (TextView) rootView.findViewById(R.id.mensajeHistorialDescarga);

        setHasOptionsMenu(true);

        new RequestConsultaRest().execute(getArguments().getString(Constantes.USUARIO), getArguments().getString(Constantes.TOKEN));

        return  rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(getActivity(), mensaje);
    }


    /**
     * Clase auxiliar para invocar consulta REST
     */
    private class RequestConsultaRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            cargando.setVisibility(View.VISIBLE);
            listaCancion.setVisibility(View.GONE);
            mensajeHistorialDescarga.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            final String authorization = "Basic " + UtilPassword.encodeBase64(params[0] + ":" + params[1]);
            String url = Uri.parse(Constantes.HISTORIAL_DESCARGA_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("usuario", params[0])
                    .build().toString();
            HttpGet post = new HttpGet(url);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Constantes.CONEXION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, Constantes.SOCKET_TIMEOUT);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
                @Override
                public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                    httpRequest.addHeader(Constantes.AUTHORIZATION, authorization);
                }
            });
            try {
                HttpResponse response = httpClient.execute(post);
                String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                JSONObject respJSON = new JSONObject(respStr);

                int estatus = respJSON.getInt("codigo");
                final String mensaje = respJSON.getString("mensaje");

                if(estatus == 200){
                    arrayCancion.clear();
                    adapterMusica.limpiarLista();

                    String entity = respJSON.getString("entity");
                    JSONArray descargas = new JSONArray(entity);

                    int tamArray = descargas.length();

                    if(tamArray > 0) {
                        for (int i = 0; i < tamArray; i++) {
                            JSONObject descarga = descargas.getJSONObject(i);
                            JSONObject cancion = descarga.getJSONObject("cancion");
                            CancionBean cancionBean = new CancionBean();
                            cancionBean.setId(cancion.getInt("id"));
                            cancionBean.setTitulo(cancion.getString("nombre"));
                            cancionBean.setArtista(!cancion.getString("artista").equals("null") ? cancion.getString("artista") : getString(R.string.artista_desconocido));
                            cancionBean.setDuracion(getString(R.string.duracion_cancion) + " " + (!cancion.getString("duracion").equals("null") ? cancion.getString("duracion") : getString(R.string.desconocido)));
                            cancionBean.setImagenId(R.drawable.audio);
                            adapterMusica.addCancion(cancionBean);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cargando.setVisibility(View.GONE);
                                listaCancion.setVisibility(View.VISIBLE);
                                mensajeHistorialDescarga.setVisibility(View.GONE);
                                adapterMusica.notifyDataSetChanged();
                            }
                        });

                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensajeHistorialDescarga.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarNotificacion(mensaje);
                        }
                    });
                }

                return estatus;
            }catch (ClientProtocolException e){
                Log.e("ClientProtocolException", e.getMessage(), e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion(getString(R.string.error_conexion));
                    }
                });
            }catch (IOException e){
                Log.e("IOException", e.getMessage(), e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion(getString(R.string.error_servidor));
                    }
                });
            }catch (JSONException e){
                Log.e("JSONException", e.getMessage(), e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion(getString(R.string.error_datos));
                    }
                });
            }
            return 406;
        }

        @Override
        protected void onPostExecute(Integer result) {
            cargando.setVisibility(View.GONE);
        }
    }
}
