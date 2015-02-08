package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.rhcloud.app_nestmusic.nestmusic.bd.SesionSQLiteHelper;
import com.rhcloud.app_nestmusic.nestmusic.util.Constantes;

/**
 * @author Jose Luis
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SesionSQLiteHelper sesionSQLiteHelper = new SesionSQLiteHelper(this, Constantes.BASE_DATOS_NOMBRE, null, 1);
        SQLiteDatabase db = sesionSQLiteHelper.getReadableDatabase();
        if(db != null){
            Cursor c = db.rawQuery(Constantes.CONSULTA_SESION_TODOS, null);
            int cantidadRegistro = c.getCount();
            if(cantidadRegistro > 0){
                Intent intent = new Intent(this, HomeActivity.class);
                c.moveToFirst();
                intent.putExtra(Constantes.USUARIO, c.getString(0));
                intent.putExtra(Constantes.TOKEN, c.getString(1));
                startActivity(intent);
                c.close();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
    }

    public void registroOnClick(View view){
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }

    public void ingresoOnClick(View view){
        Intent intent = new Intent(this, Ingreso.class);
        startActivity(intent);
    }

    public void solicitarValidacionOnClick(View view){
        Intent intent = new Intent(this, SolicitarValidacionActivity.class);
        startActivity(intent);
    }

    public void continuarSinRegistroOnClick(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
