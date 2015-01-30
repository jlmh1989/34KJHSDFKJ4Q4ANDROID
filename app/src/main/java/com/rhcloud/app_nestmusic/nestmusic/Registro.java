package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

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
import java.util.Calendar;

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
    private ProgressDialog progressDialog;

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
        if(Utils.conectadoInternet(this)) {
            final String usuario = this.usuario.getText().toString();
            final String password = this.password.getText().toString();
            final String nombre = this.nombre.getText().toString();
            final String apellidos = this.apellidos.getText().toString();
            final String correo = this.correo.getText().toString();
            final String fechaNac = this.fechaNacimiento.getText().toString();

            new RequestRest().execute(usuario, password, nombre, apellidos, correo, fechaNac);

        }else {
            mostrarNotificacion("Conexion no disponible.");
        }
    }

    public void seleccionarFechaNacimiento(View view){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fechaNacimiento.setText(year+"-"+(monthOfYear - 1)+"-"+dayOfMonth);
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(this, mensaje);
    }

    /**
     * Ejecutar request REST
     */
    private class RequestRest extends AsyncTask<String, Integer, Integer>{

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Registro.this, "", getString(R.string.cargando));
        }

        @Override
        protected Integer doInBackground(String... params) {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Registro.this);
                            dialog.setTitle(getString(R.string.titulo_dialog_info));
                            dialog.setMessage(getString(R.string.mensaje_registro));
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    NavUtils.navigateUpFromSameTask(Registro.this);
                                }
                            });
                            dialog.create();
                            dialog.show();
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
