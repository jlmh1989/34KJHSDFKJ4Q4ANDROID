package com.rhcloud.app_nestmusic.nestmusic.fragmentos;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaAdapterAbstract;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaHDAdapter;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;
import com.rhcloud.app_nestmusic.nestmusic.util.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiMusica extends Fragment{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listaCancionView;
    private ProgressBar cargando;
    private EditText filtro;
    private ListaMusicaHDAdapter listaMusicaAdapter;
    private MiMusicaCallbacks listener;

    public static MiMusica newInstance(int sectionNumber){
        MiMusica fragment = new MiMusica();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MiMusica() {
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
        View rootView = inflater.inflate(R.layout.fragment_mi_musica, container, false);

        ArrayList<CancionBean> listaMusica = new ArrayList<CancionBean>();
        listaMusicaAdapter = new ListaMusicaHDAdapter(this.getActivity(), listaMusica);

        filtro = (EditText) rootView.findViewById(R.id.filtro);
        filtro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listaMusicaAdapter.filtrar(filtro.getText().toString().toLowerCase(Locale.getDefault()));
            }
        });

        listaCancionView = (ListView) rootView.findViewById(R.id.listaCancion);
        listaCancionView.setAdapter(listaMusicaAdapter);
        listaCancionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accion seleccion
                listener.setPosicionMusicaReproducir(position);
            }
        });

        listaCancionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch(action) {
                    case (MotionEvent.ACTION_MOVE) :
                        listener.onTouchListMiMusica();
                }
                return false;
            }
        });

        cargando = (ProgressBar) rootView.findViewById(R.id.cargando);

        setHasOptionsMenu(true);

        listener.setTituloActivityMiMusica(getString(R.string.menu_mi_musica));

        new ObtenerListaMusica().execute((Void)null);

        return rootView;
    }

    void leerMusicaSDCARD(){
        ContentResolver musicaResolver = getActivity().getContentResolver();
        Uri musicaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] select = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
        };
        String where = MediaStore.Audio.Media.IS_MUSIC + "=1";

        Cursor musicaCursor = musicaResolver.query(musicaUri, select, where, null, null);

        if(musicaCursor != null && musicaCursor.moveToFirst()){
            int tituloColumna = musicaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int idColumna = musicaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int artistaColumna = musicaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int duracionColumna = musicaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int albumIdColumna = musicaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            do {
                CancionBean musica = new CancionBean();
                musica.setId(musicaCursor.getLong(idColumna));
                musica.setTitulo(musicaCursor.getString(tituloColumna));
                musica.setArtista(musicaCursor.getString(artistaColumna));
                Date date = new Date(musicaCursor.getLong(duracionColumna));
                DateFormat formato = new SimpleDateFormat("mm:ss");
                musica.setDuracion(getString(R.string.duracion_cancion) + " " + formato.format(date));
                musica.setPathMusica(ContentUris.withAppendedId(musicaUri, musica.getId()));

                long albumId = musicaCursor.getLong(albumIdColumna);
                final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);

                try{
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    AssetFileDescriptor fileDescriptor = musicaResolver.openAssetFileDescriptor(albumArtUri, "r");
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                    musica.setImagen(bitmap);
                }catch (Exception e){
                    musica.setImagen(null);
                }
                listaMusicaAdapter.addMusica(musica);
            }while (musicaCursor.moveToNext());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listaCancionView.setVisibility(View.VISIBLE);
                    filtro.setVisibility(View.VISIBLE);
                    listaMusicaAdapter.notifyDataSetChanged();
                    listener.setListaCancionesMiMusica(listaMusicaAdapter.getListaMusica());
                    listener.setAdapterAbstractMiMusica(listaMusicaAdapter);
                }
            });
            musicaCursor.close();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            listener = (MiMusicaCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity no implementa iterface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void mostrarNotificacion(String mensaje){
        Utils.mostrarNotificacion(getActivity(), mensaje);
    }

    public static interface MiMusicaCallbacks{
        void setListaCancionesMiMusica(ArrayList<CancionBean> canciones);
        void setTituloActivityMiMusica(String titulo);
        void setPosicionMusicaReproducir(int posicion);
        void setAdapterAbstractMiMusica(ListaMusicaAdapterAbstract adapterAbstract);
        void onTouchListMiMusica();
    }

    private class ObtenerListaMusica extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            cargando.setVisibility(View.VISIBLE);
            listaCancionView.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                leerMusicaSDCARD();
                return true;
            }catch (Exception e){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mostrarNotificacion(getString(R.string.error_leer_sd_externo));
                    }
                });
                return  false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cargando.setVisibility(View.GONE);
        }

    }

}
