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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaHDAdapter;
import com.rhcloud.app_nestmusic.nestmusic.bean.MusicaHDBean;
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
    private ArrayList<MusicaHDBean> listaMusica;
    private ListaMusicaHDAdapter listaMusicaAdapter;

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

        listaMusica = new ArrayList<MusicaHDBean>();
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
            }
        });

        cargando = (ProgressBar) rootView.findViewById(R.id.cargando);

        setHasOptionsMenu(true);

        new ObtenerListaMusica().execute((Void)null);

        return rootView;
    }

    public void leerMusicaSDCARD(){
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
                MusicaHDBean musica = new MusicaHDBean();
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
                }
            });
            musicaCursor.close();
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
