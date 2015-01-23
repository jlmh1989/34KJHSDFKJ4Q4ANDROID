package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.rhcloud.app_nestmusic.nestmusic.bd.SesionSQLiteHelper;
import com.rhcloud.app_nestmusic.nestmusic.util.Constantes;
import com.rhcloud.app_nestmusic.nestmusic.util.UtilPassword;
import com.rhcloud.app_nestmusic.nestmusic.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;


public class HomeActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ProgressDialog progressDialog;
    private int seccionActiva;

    private String usuario;
    private String token;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Obtener extras
        Intent intent = getIntent();
        usuario = intent.getStringExtra(Constantes.USUARIO);
        token = intent.getStringExtra(Constantes.TOKEN);
    }

    /**
     * Minimizar Activity al presionar el boton atras
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Controlar la navegacion del panel izquierdo
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case Constantes.INICIO:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, Inicio.newInstance(position))
                        .commit();
                break;
            case Constantes.FAVORITOS:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, Inicio.newInstance(position))
                        .commit();
                break;
            case Constantes.LISTA_REPROD:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, Inicio.newInstance(position))
                        .commit();
                break;
            case Constantes.HISTO_REPROD:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, Inicio.newInstance(position))
                        .commit();
                break;
            case Constantes.DESCARGAS:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new Descargas().newInstance(position))
                        .commit();
                break;
            case Constantes.CERRAR_SESION:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.cerrar_sesion))
                        .setTitle(getString(R.string.cerrar_sesion_titulo))
                        .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Cerrar sesion
                                new RequestRest().execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();;
                break;
        }
    }

    /**
     * Asignar titulo Activity dependiendo de la seccion seleccionada
     * @param number
     */
    public void onSectionAttached(int number) {
        seccionActiva = number;
        switch (number) {
            case Constantes.INICIO:
                mTitle = getString(R.string.menu_inicio);
                break;
            case Constantes.FAVORITOS:
                mTitle = getString(R.string.menu_favoritos);
                break;
            case Constantes.LISTA_REPROD:
                mTitle = getString(R.string.menu_lista_Rep);
                break;
            case Constantes.HISTO_REPROD:
                mTitle = getString(R.string.menu_historial_Rep);
                break;
            case Constantes.DESCARGAS:
                mTitle = getString(R.string.menu_descargas);
                break;
            case Constantes.CERRAR_SESION:
                mTitle = getString(R.string.menu_fin_sesion);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);

            // Associate searchable configuration with the SearchView
            //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            //SearchView searchView = (SearchView) menu.findItem(R.id.action_busqueda)
            //        .getActionView();
            //searchView.setSearchableInfo(searchManager
            //        .getSearchableInfo(getComponentName()));
            //searchView.setSubmitButtonEnabled(true);
            //searchView.setOnQueryTextListener(this);
            restoreActionBar();

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /*
    @Override
    public boolean onQueryTextChange(String newText)
    {
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        switch (seccionActiva) {
            case Constantes.INICIO:

                break;
            case Constantes.FAVORITOS:

                break;
            case Constantes.LISTA_REPROD:

                break;
            case Constantes.HISTO_REPROD:

                break;
            case Constantes.DESCARGAS:

                break;
        }
        Log.i("busqueda", query);
        mostrarNotificacion(query);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.action_salir:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.cerrar_app))
                        .setTitle(getString(R.string.cerrar_app_titulo))
                        .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Cerrar app
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //No hacer nada
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Monstrar mensaje toast
     * @param mensaje
     */
    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(this, mensaje);
    }

    /**
     * Clase para manejar el request al servicio REST
     */
    private class RequestRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(HomeActivity.this, "", getString(R.string.cargando));
        }

        @Override
        protected Integer doInBackground(String... params) {
            Log.i("Usuario", usuario);
            Log.i("Token", token);
            String url = Uri.parse(Constantes.CERRAR_SESION_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("usuario", usuario)
                    .build().toString();
            HttpPost post = new HttpPost(url);
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
                    JSONObject entity = new JSONObject(respJSON.getString("entity"));
                    String token = entity.getString("token");

                    SesionSQLiteHelper sesionSQLiteHelper = new SesionSQLiteHelper(HomeActivity.this, Constantes.BASE_DATOS_NOMBRE, null, 1);
                    SQLiteDatabase db = sesionSQLiteHelper.getWritableDatabase();

                    if(db != null){
                        db.delete(Constantes.NOMBRE_TABLA_SESION, "USUARIO=?", new String[]{usuario});
                        db.close();
                        //Cerrar app
                        finish();
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarNotificacion(mensaje);
                        }
                    });
                }

                return estatus;
            }catch (ClientProtocolException e){
                Log.e("ClientProtocolException", e.getMessage(), e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion(getString(R.string.error_conexion));
                    }
                });
            }catch (IOException e){
                Log.e("IOException", e.getMessage(), e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion(getString(R.string.error_servidor));
                    }
                });
            }catch (JSONException e){
                Log.e("JSONException", e.getMessage(), e);
                runOnUiThread(new Runnable() {
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
            progressDialog.dismiss();
        }
    }
}
