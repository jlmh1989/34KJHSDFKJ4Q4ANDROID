package com.rhcloud.app_nestmusic.nestmusic.fragmentos;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaListaReproduccionAdapter;
import com.rhcloud.app_nestmusic.nestmusic.bean.ListaReproduccionBean;
import com.rhcloud.app_nestmusic.nestmusic.util.Constantes;
import com.rhcloud.app_nestmusic.nestmusic.util.UtilPassword;
import com.rhcloud.app_nestmusic.nestmusic.util.Utils;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
public class ListaReproduccion extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView listaListaReprod;
    private ProgressBar cargando;
    private TextView mensajeListaReprod;
    private ListaListaReproduccionAdapter listaReprodAdapter;
    private ArrayList<ListaReproduccionBean> listaReproduccionBeans;
    private ArrayList<ListaReproduccionBean> listaReproduccionBeansEliminar;
    private ProgressDialog progressDialog;
    private MenuItem itemEditar;

    public static ListaReproduccion newInstance(int sectionNumber, String usuario, String token){
        ListaReproduccion fragment = new ListaReproduccion();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(Constantes.USUARIO, usuario);
        args.putString(Constantes.TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public ListaReproduccion() {
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
        View rootView = inflater.inflate(R.layout.fragment_lista_reproduccion, container, false);

        listaReproduccionBeans = new ArrayList<ListaReproduccionBean>();
        listaReprodAdapter = new ListaListaReproduccionAdapter(this.getActivity(), listaReproduccionBeans);

        listaListaReprod = (ListView) rootView.findViewById(R.id.listaReproduccion);
        listaListaReprod.setAdapter(listaReprodAdapter);
        listaListaReprod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accion seleccion
            }
        });

        listaListaReprod.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listaListaReprod.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int contadorSeleccionado = listaListaReprod.getCheckedItemCount();
                mode.setTitle(contadorSeleccionado + " " + getString(R.string.seleccionado));
                listaReprodAdapter.toggleSelection(position);
                if(listaReprodAdapter.getSelectedCount() > 1){
                    itemEditar.setVisible(false);
                }else{
                    itemEditar.setVisible(true);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.editar, menu);
                itemEditar = menu.findItem(R.id.action_editar);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_eliminar:
                        SparseBooleanArray seleccionEliminar = listaReprodAdapter.getItemsSeleccionados();
                        listaReproduccionBeansEliminar = new ArrayList<ListaReproduccionBean>();
                        for (int i = (seleccionEliminar.size() - 1); i >= 0; i--){
                            if(seleccionEliminar.valueAt(i)) {
                                ListaReproduccionBean objetoEliminar = listaReprodAdapter.getItem(seleccionEliminar.keyAt(i));
                                listaReproduccionBeansEliminar.add(objetoEliminar);
                            }
                        }
                        new RequestDeleteRest().execute(getArguments().getString(Constantes.USUARIO), getArguments().getString(Constantes.TOKEN));
                        mode.finish();
                        return true;
                    case R.id.action_editar:
                        SparseBooleanArray seleccionEditar = listaReprodAdapter.getItemsSeleccionados();
                        final ListaReproduccionBean objetoEditar = listaReprodAdapter.getItem(seleccionEditar.keyAt(0));

                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle(getString(R.string.titulo_editar_lista_reprod));
                        alert.setMessage(getString(R.string.mensaje_editar_lista_reprod));

                        final EditText input = new EditText(getActivity());
                        input.setText(objetoEditar.getNombre());
                        alert.setView(input);
                        alert.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String texto = input.getText().toString();
                                if(texto != null && !texto.isEmpty()){
                                    input.clearFocus();
                                    new RequestEditarRest().execute(getArguments().getString(Constantes.USUARIO), getArguments().getString(Constantes.TOKEN), ""+objetoEditar.getId(), texto);
                                }
                            }
                        });
                        alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //No hace nada
                            }
                        });
                        alert.show();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listaReprodAdapter.removeSelection();
            }
        });

        cargando = (ProgressBar) rootView.findViewById(R.id.cargando);
        mensajeListaReprod = (TextView) rootView.findViewById(R.id.mensajeListaReproduccion);

        setHasOptionsMenu(true);

        new RequestConsultaRest().execute(getArguments().getString(Constantes.USUARIO), getArguments().getString(Constantes.TOKEN));

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_agregar:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle(getString(R.string.titulo_nueva_lista_reprod));
                alert.setMessage(getString(R.string.mensaje_nueva_lista_reprod));

                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String texto = input.getText().toString();
                        if(texto != null && !texto.isEmpty()){
                            input.clearFocus();
                            new RequestGuardarRest().execute(getArguments().getString(Constantes.USUARIO), getArguments().getString(Constantes.TOKEN), texto);
                        }
                    }
                });

                alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //No hace nada
                    }
                });

                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cargando.setVisibility(View.VISIBLE);
                    listaListaReprod.setVisibility(View.GONE);
                    mensajeListaReprod.setVisibility(View.GONE);
                }
            });
        }

        @Override
        protected Integer doInBackground(String... params) {
            final String authorization = "Basic " + UtilPassword.encodeBase64(params[0] + ":" + params[1]);
            String url = Uri.parse(Constantes.OBTENER_LISTA_REPRODUCCION_ENDPOINT)
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
                    String entity = respJSON.getString("entity");
                    JSONArray listaReprods = new JSONArray(entity);
                    listaReproduccionBeans.clear();
                    listaReprodAdapter.limpiarLista();
                    int tamArray = listaReprods.length();

                    if(tamArray > 0) {
                        for (int i = 0; i < tamArray; i++) {
                            JSONObject listaReprod = listaReprods.getJSONObject(i);
                            ListaReproduccionBean listaReproduccionBean = new ListaReproduccionBean();
                            listaReproduccionBean.setId(listaReprod.getInt("id"));
                            listaReproduccionBean.setNombre(listaReprod.getString("nombre"));
                            listaReproduccionBean.setFechaCreacion(getString(R.string.fecha_creacion) + " " + listaReprod.getString("fechaCreacion"));
                            listaReprodAdapter.addListaReproduccion(listaReproduccionBean);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cargando.setVisibility(View.GONE);
                                listaListaReprod.setVisibility(View.VISIBLE);
                                mensajeListaReprod.setVisibility(View.GONE);
                                listaReprodAdapter.notifyDataSetChanged();
                            }
                        });

                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensajeListaReprod.setVisibility(View.VISIBLE);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cargando.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Clase auxiliar para invocar guardado REST
     */
    private class RequestGuardarRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.guardando));
                }
            });
        }

        @Override
        protected Integer doInBackground(String... params) {
            final String authorization = "Basic " + UtilPassword.encodeBase64(params[0] + ":" + params[1]);
            String url = Uri.parse(Constantes.AGREGAR_LISTA_REPRODUCCION_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("usuario", params[0])
                    .appendQueryParameter("nombre", params[2])
                    .build().toString();
            HttpPost post = new HttpPost(url);
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
                    new RequestConsultaRest().execute(params[0], params[1]);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }

    /**
     * Clase auxiliar para invocar eliimado REST
     */
    private class RequestDeleteRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.eliminando));
                }
            });
        }

        @Override
        protected Integer doInBackground(String... params) {
            final String authorization = "Basic " + UtilPassword.encodeBase64(params[0] + ":" + params[1]);
            int status = 406;

            for (ListaReproduccionBean bean : listaReproduccionBeansEliminar){
                String url = Uri.parse(Constantes.ELIMINAR_LISTA_REPRODUCCION_ENDPOINT)
                        .buildUpon()
                        .appendQueryParameter("id", ""+bean.getId())
                        .build().toString();
                HttpPost post = new HttpPost(url);
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

                    if(estatus != 200){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mostrarNotificacion(mensaje);
                            }
                        });
                    }

                    status = estatus;
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
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
            new RequestConsultaRest().execute(getArguments().getString(Constantes.USUARIO), getArguments().getString(Constantes.TOKEN));
        }
    }

    /**
     * Clase auxiliar para invocar editar REST
     */
    private class RequestEditarRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.guardando));
                }
            });
        }

        @Override
        protected Integer doInBackground(String... params) {
            final String authorization = "Basic " + UtilPassword.encodeBase64(params[0] + ":" + params[1]);
            String url = Uri.parse(Constantes.EDITAR_LISTA_REPRODUCCION_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("id", params[2])
                    .appendQueryParameter("nombre", params[3])
                    .build().toString();
            HttpPost post = new HttpPost(url);
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
                    new RequestConsultaRest().execute(params[0], params[1]);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }
}
