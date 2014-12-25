package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
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
public class Registro extends Activity {

    private EditText usuario;
    private EditText password;
    private EditText nombre;
    private EditText apellidos;
    private EditText correo;
    private EditText fechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        usuario = (EditText) findViewById(R.id.et_usuario);
        password = (EditText) findViewById(R.id.et_password);
        nombre = (EditText) findViewById(R.id.et_nombre);
        apellidos = (EditText) findViewById(R.id.et_apellidos);
        correo = (EditText) findViewById(R.id.et_correo);
        fechaNacimiento = (EditText) findViewById(R.id.et_fechaNac);
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

    public void guardarRegistro(View view){
        String usuario = this.usuario.getText().toString();
        String password = this.password.getText().toString();
        String nombre = this.nombre.getText().toString();
        String apellidos = this.apellidos.getText().toString();
        String correo = this.correo.getText().toString();
        String fechaNac = this.fechaNacimiento.getText().toString();

        new RequestRest().execute(usuario, password, nombre, apellidos, correo, fechaNac);
    }

    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(this, mensaje);
    }

    /**
     * Ejecutar request REST
     */
    private class RequestRest extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            String pass = UtilPassword.encodePassword(params[1]);
            String url = Uri.parse(Constantes.REGISTRO_ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("usuario",params[0])
                    .appendQueryParameter("password",pass)
                    .appendQueryParameter("nombre",params[2])
                    .appendQueryParameter("apellidos",params[3])
                    .appendQueryParameter("correo",params[4])
                    .appendQueryParameter("fechaNacimiento",params[5])
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
                            mostrarNotificacion("Usuario creado exitosamente");
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
