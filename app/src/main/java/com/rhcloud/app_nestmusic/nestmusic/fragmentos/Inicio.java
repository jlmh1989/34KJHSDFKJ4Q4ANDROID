package com.rhcloud.app_nestmusic.nestmusic.fragmentos;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaAdapter;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaAdapterAbstract;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;
import com.rhcloud.app_nestmusic.nestmusic.musica.MusicaCallbacks;
import com.rhcloud.app_nestmusic.nestmusic.util.Constantes;
import com.rhcloud.app_nestmusic.nestmusic.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by joseluis on 17/01/15.
 */
public class Inicio extends Fragment implements SearchView.OnQueryTextListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private int inicioBusqueda = 0;
    private ListView listaCancion;
    private ProgressBar cargando;
    private TextView mensajeBusqueda;
    private SearchView searchView;
    private ListaMusicaAdapter adapterMusica;
    private View footer;
    private ArrayList<CancionBean> arrayCancion;
    private String textoBusqueda = "";
    private boolean busquedaNueva = true;
    private ImageButton buscarMas;
    private ProgressBar cargandoBuscarMas;
    private TextView mensajeInicio;
    private MusicaCallbacks listener;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Inicio newInstance(int sectionNumber) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public Inicio() {
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        listaCancion = (ListView) rootView.findViewById(R.id.listaCancion);
        footer = ((LayoutInflater)this
                .getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.lista_footer, null, false);

        listaCancion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch(action) {
                    case (MotionEvent.ACTION_MOVE) :
                        listener.onTouchList();
                }
                return false;
            }
        });

        cargandoBuscarMas = (ProgressBar) footer.findViewById(R.id.pb_cargando_mas);
        buscarMas = (ImageButton) footer.findViewById(R.id.buscar_mas);
        buscarMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioBusqueda += Constantes.LIMITE_CONSULTA;
                busquedaNueva = false;
                new RequestRest().execute(textoBusqueda, ""+inicioBusqueda, ""+Constantes.LIMITE_CONSULTA);
            }
        });

        listaCancion.addFooterView(footer);
        arrayCancion = new ArrayList<CancionBean>();
        adapterMusica = new ListaMusicaAdapter(this.getActivity(), arrayCancion);
        listaCancion.setAdapter(adapterMusica);

        listaCancion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accion seleccion
                listener.setPosicionMusicaReproducir(position);
            }
        });

        listener.setTituloActivity(getString(R.string.menu_inicio));

        mensajeInicio = (TextView) rootView.findViewById(R.id.mensajeInicio);
        mensajeInicio.setVisibility(View.VISIBLE);

        cargando = (ProgressBar) rootView.findViewById(R.id.cargando);
        mensajeBusqueda = (TextView) rootView.findViewById(R.id.mensajeBusqueda);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (MusicaCallbacks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity no implementa iterface.");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        NavigationDrawerFragment navegacion = (NavigationDrawerFragment)getActivity().getFragmentManager().findFragmentById(R.id.navigation_drawer);

        if(!navegacion.isDrawerOpen()) {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.action_busqueda)
                    .getActionView();
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
        }
    }

    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(getActivity(), mensaje);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        textoBusqueda = query;
        arrayCancion.clear();
        adapterMusica.limpiarLista();
        busquedaNueva = true;
        inicioBusqueda = 0;
        mensajeInicio.setVisibility(View.GONE);
        new RequestRest().execute(query, ""+inicioBusqueda, ""+Constantes.LIMITE_CONSULTA);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    private class RequestRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            if(busquedaNueva) {
                mensajeBusqueda.setVisibility(View.GONE);
                listaCancion.setVisibility(View.GONE);
                cargando.setVisibility(View.VISIBLE);
            }else{
                cargandoBuscarMas.setVisibility(View.VISIBLE);
                buscarMas.setVisibility(View.GONE);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = Uri.parse(Constantes.BUSQUEDA_CANCION_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("nombre", params[0])
                    .appendQueryParameter("inicio", params[1])
                    .appendQueryParameter("cantidad", params[2])
                    .build().toString();
            HttpGet post = new HttpGet(url);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Constantes.CONEXION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, Constantes.SOCKET_TIMEOUT);
            HttpClient httpClient = new DefaultHttpClient(httpParams);
            try {
                HttpResponse response = httpClient.execute(post);
                String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                JSONObject respJSON = new JSONObject(respStr);

                int estatus = respJSON.getInt("codigo");
                final String mensaje = respJSON.getString("mensaje");

                if(estatus == 200){
                    String entity = respJSON.getString("entity");
                    JSONArray canciones = new JSONArray(entity);

                    int tamArray = canciones.length();

                    if(tamArray >= Constantes.LIMITE_CONSULTA){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                footer.setVisibility(View.VISIBLE);
                            }
                        });
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                footer.setVisibility(View.GONE);
                            }
                        });
                    }

                    if(tamArray > 0) {
                        for (int i = 0; i < tamArray; i++) {
                            JSONObject cancion = canciones.getJSONObject(i);
                            CancionBean cancionBean = new CancionBean();
                            cancionBean.setTitulo(cancion.getString("nombre"));
                            cancionBean.setArtista(!cancion.getString("artista").equals("null") ? cancion.getString("artista") : getString(R.string.artista_desconocido));
                            cancionBean.setDuracion(getString(R.string.duracion_cancion) + " " + (!cancion.getString("duracion").equals("null") ? cancion.getString("duracion") : getString(R.string.desconocido)));
                            cancionBean.setImagenId(R.drawable.audio);
                            cancionBean.setUrlMusica(cancion.getString("url"));
                            cancionBean.setUrlParent(cancion.getString("urlParent"));
                            cancionBean.setOnline(true);
                            adapterMusica.addCancion(cancionBean);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterMusica.notifyDataSetChanged();
                                listener.setListaCanciones(adapterMusica.getListaCancion());
                                listener.setAdapterAbstract(adapterMusica);
                            }
                        });

                    }else{
                        if(busquedaNueva) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mensajeBusqueda.setVisibility(View.VISIBLE);
                                }
                            });
                        }
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
            if(busquedaNueva) {
                listaCancion.setVisibility(View.VISIBLE);
                cargando.setVisibility(View.GONE);
            }else {
                cargandoBuscarMas.setVisibility(View.GONE);
                buscarMas.setVisibility(View.VISIBLE);
            }
        }
    }

}
