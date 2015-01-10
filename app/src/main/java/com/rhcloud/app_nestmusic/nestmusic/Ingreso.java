package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

import java.io.IOException;

/**
 * @author Jose Luis
 */
public class Ingreso extends Activity {

    private EditText usuario;
    private EditText password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        usuario = (EditText) findViewById(R.id.et_usuario);
        password = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ingresar(View view){
        String usuarioTxt = usuario.getText().toString();
        String passwordTxt = password.getText().toString();

        new RequestRest().execute(usuarioTxt, passwordTxt);
    }

    public void recuperarPass(View view){
        Intent intent = new Intent(this, RecuperarPasswordActivity.class);
        startActivity(intent);
    }

    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(this, mensaje);
    }

    private class RequestRest extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Ingreso.this, "", getString(R.string.cargando));
        }

        @Override
        protected Integer doInBackground(String... params) {
            String pass = UtilPassword.encodePassword(params[1]);
            String url = Uri.parse(Constantes.INGRESO_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("usuario", params[0])
                    .appendQueryParameter("password", pass)
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

                    SesionSQLiteHelper sesionSQLiteHelper = new SesionSQLiteHelper(Ingreso.this, Constantes.BASE_DATOS_NOMBRE, null, 1);
                    SQLiteDatabase db = sesionSQLiteHelper.getWritableDatabase();

                    if(db != null){
                        ContentValues registro = new ContentValues();
                        registro.put("USUARIO", usuario.getText().toString());
                        registro.put("TOKEN", token);
                        db.insert(Constantes.NOMBRE_TABLA_SESION, null, registro);
                        db.close();
                        Intent intent = new Intent(Ingreso.this, HomeActivity.class);
                        intent.putExtra(Constantes.USUARIO, usuario.getText().toString());
                        intent.putExtra(Constantes.TOKEN, token);
                        startActivity(intent);
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
