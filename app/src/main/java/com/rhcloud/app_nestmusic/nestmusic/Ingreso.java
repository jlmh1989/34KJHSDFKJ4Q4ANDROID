package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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
        protected Integer doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            String pass = UtilPassword.encodePassword(params[1]);
            String url = Uri.parse(Constantes.INGRESO_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("usuario", params[0])
                    .appendQueryParameter("password", pass)
                    .build().toString();
            HttpPost post = new HttpPost(url);
            try {
                HttpResponse response = httpClient.execute(post);
                String respStr = EntityUtils.toString(response.getEntity());

                JSONObject respJSON = new JSONObject(respStr);

                int estatus = respJSON.getInt("codigo");
                final String mensaje = respJSON.getString("mensaje");

                if(estatus == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mostrarNotificacion("Sesion iniciado con exito.");
                        }
                    });
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
                        mostrarNotificacion("Error de conexion.");
                    }
                });
            }catch (IOException e){
                Log.e("IOException", e.getMessage(), e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion("No se puede conectar con el servidor.");
                    }
                });
            }catch (JSONException e){
                Log.e("JSONException", e.getMessage(), e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion("Error al recibir los datos.");
                    }
                });
            }
            return 406;
        }
    }


}
