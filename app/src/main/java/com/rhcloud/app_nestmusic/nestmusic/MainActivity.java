package com.rhcloud.app_nestmusic.nestmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author Jose Luis
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
