package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by joseluis on 17/01/15.
 */
public class Inicio extends Fragment implements SearchView.OnQueryTextListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView listaCancion;
    private ProgressBar cargando;
    private TextView mensajeBusqueda;
    private SearchView searchView;
    private ListaMusica adapter;

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
        if(adapter != null){
            listaCancion.setAdapter(adapter);
        }
        listaCancion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accion seleccion
            }
        });

        cargando = (ProgressBar) rootView.findViewById(R.id.cargando);
        mensajeBusqueda = (TextView) rootView.findViewById(R.id.mensajeBusqueda);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
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
        new RequestRest().execute(query, ""+Constantes.LIMIT_RANDOM);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }

    private class RequestRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            mensajeBusqueda.setVisibility(View.GONE);
            listaCancion.setVisibility(View.GONE);
            cargando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = Uri.parse(Constantes.BUSQUEDA_CANCION_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("nombre", params[0])
                    .appendQueryParameter("cantidad", params[1])
                    .build().toString();
            HttpGet post = new HttpGet(url);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Constantes.CONEXION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, Constantes.SOCKET_TIMEOUT);
            HttpClient httpClient = new DefaultHttpClient(httpParams);
            try {
                HttpResponse response = httpClient.execute(post);
                String respStr = EntityUtils.toString(response.getEntity());
                JSONObject respJSON = new JSONObject(respStr);

                int estatus = respJSON.getInt("codigo");
                final String mensaje = respJSON.getString("mensaje");

                if(estatus == 200){
                    String entity = respJSON.getString("entity");
                    JSONArray canciones = new JSONArray(entity);

                    int tamArray = canciones.length();

                    if(tamArray > 0) {

                        final String[] titulo = new String[tamArray];
                        final String[] artista = new String[tamArray];
                        final String[] duracion = new String[tamArray];
                        final Integer[] imagenId = new Integer[tamArray];

                        for (int i = 0; i < tamArray; i++) {
                            JSONObject cancion = canciones.getJSONObject(i);
                            titulo[i] = cancion.getString("nombre");
                            artista[i] = cancion.getString("artista") != "null" ? cancion.getString("artista") : getString(R.string.artista_desconocido);
                            duracion[i] = getString(R.string.duracion_cancion) + " " + (cancion.getString("duracion") != "null" ? cancion.getString("duracion") : getString(R.string.desconocido));
                            imagenId[i] = R.drawable.audio;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new ListaMusica(Inicio.this.getActivity(), imagenId, titulo, artista, duracion);
                                listaCancion.setAdapter(adapter);
                            }
                        });

                    }else{
                        mensajeBusqueda.setVisibility(View.VISIBLE);
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
            listaCancion.setVisibility(View.VISIBLE);
            cargando.setVisibility(View.GONE);
        }
    }

}
