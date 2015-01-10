package com.rhcloud.app_nestmusic.nestmusic.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rhcloud.app_nestmusic.nestmusic.util.Constantes;

/**
 * Created by joseluis on 27/12/14.
 */
public class SesionSQLiteHelper extends SQLiteOpenHelper {

    public SesionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constantes.SQL_CREAR_TABLA_SESION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
