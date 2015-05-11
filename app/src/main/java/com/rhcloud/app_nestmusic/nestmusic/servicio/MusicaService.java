package com.rhcloud.app_nestmusic.nestmusic.servicio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.MediaController;

import com.rhcloud.app_nestmusic.nestmusic.CallIntentReceiver;
import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.MusicIntentReceiver;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.adaptadores.ListaMusicaAdapterAbstract;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;
import com.rhcloud.app_nestmusic.nestmusic.musica.MusicaController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by joseluis on 08/02/15.
 */
public class MusicaService extends Service
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    private MediaPlayer player;
    private ArrayList<CancionBean> canciones;
    private int cancionPosicion;
    private final IBinder musicBind = new MusicaBinder();
    private String songTitle = "";
    private static final int NOTIFY_ID = 1;
    private boolean shuffle=false;
    private boolean isPause = false;
    private Random rand;
    private Handler mHandler = new Handler();
    private ListaMusicaAdapterAbstract adapterAbstract;
    private CancionBean cancion;
    MediaMetadataRetriever metadataRetriever;

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void onCreate(){
        super.onCreate();
        cancionPosicion = 0;
        rand = new Random();
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        MusicIntentReceiver receiver = new MusicIntentReceiver(this);
        //CallIntentReceiver callIntentReceiver = new CallIntentReceiver();
        registerReceiver(receiver, receiverFilter);
        //registerReceiver(callIntentReceiver, receiverFilter);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setAdapterAbstract(ListaMusicaAdapterAbstract adapterAbstract){
        this.adapterAbstract = adapterAbstract;
    }

    public void setListaCaciones(ArrayList<CancionBean> canciones){
        this.canciones = canciones;
    }

    public ArrayList<CancionBean> getListaCanciones(){
        return this.canciones;
    }

    public void setCancion(int songIndex){
        cancionPosicion=songIndex;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                Log.w("AUDIOFOCUS_GAIN", "Ejecutado");
                if (player == null) initMusicPlayer();
                else if (!isPng()) go();
                player.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Log.w("AUDIOFOCUS_LOSS", "Ejecutado");
                if (isPng()) player.stop();
                player.release();
                player = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                Log.w("AUDIOFOCUS_LOSS_TRANSIENT", "Ejecutado");
                if (isPng()) pausePlayer();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                Log.w("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK", "Ejecutado");
                if (isPng()) player.setVolume(0.1f, 0.1f);
                break;
        }
    }

    public class MusicaBinder extends Binder{
        public MusicaService getService(){
            return MusicaService.this;
        }
    }

    public void playSong() throws IOException{
        Log.w("MusicaService.playSong", "Ejecutado");
        player.reset();
        cancion = canciones.get(cancionPosicion);
        Log.w("CancionLeido", cancion.toString());
        try {
            if(cancion.isOnline()){
                player.setDataSource(cancion.getUrlMusica());
                /**
                if (cancion.getArtistaOrig().equals("null")) {
                    metadataRetriever = new MediaMetadataRetriever();
                    metadataRetriever.setDataSource(cancion.getUrlMusica(), new HashMap<String, String>());
                }
                 **/
            }else{
                player.setDataSource(getApplicationContext(), cancion.getPathMusica());
            }
            songTitle = cancion.getTitulo();
            adapterAbstract.setPlayIcon(cancionPosicion);
            player.prepare();
        } catch (IOException e) {
            Log.w("IOException", "Error, recurso no disponible.");
            throw e;
        }
    }

    @Override
    public void onDestroy() {
        if (player != null) player.release();
        stopForeground(true);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    @Override
    public void onCompletion(MediaPlayer mp){
        if(player.getCurrentPosition() > 0){
            Log.w("MusicaService.onCompletion", "Ejecutado");
            player.reset();
            try {
                playNext();
            }catch (IOException e){
                onCompletion(mp);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.w("MusicaService.onPrepared", "Ejecutado");
                player.start();
                isPause = false;
                Intent notIntent = new Intent(getApplicationContext(), HomeActivity.class);
                notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendInt = PendingIntent.getActivity(getApplicationContext(), 0,
                        notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(MusicaService.this);

                builder.setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.play_notif)
                        .setTicker(songTitle)
                        .setOngoing(true)
                        .setContentTitle(getString(R.string.reproduciendo))
                        .setContentText(songTitle);
                Notification not = builder.build();

                startForeground(NOTIFY_ID, not);

                Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
                LocalBroadcastManager.getInstance(MusicaService.this).sendBroadcast(onPreparedIntent);

                /**
                if (cancion.getArtistaOrig().equals("null")){
                    String artista = metadataRetriever.toString();//extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    Log.i("Artista recuperado", artista);
                    metadataRetriever.release();
                }
                 **/
            }
        });
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public boolean isPause() {
        return isPause;
    }

    public void pausePlayer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.w("MusicaService.pausePlayer", "Ejecutado");
                player.pause();
                isPause = true;
                Intent notIntent = new Intent(MusicaService.this, HomeActivity.class);
                notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendInt = PendingIntent.getActivity(MusicaService.this, 0,
                        notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(MusicaService.this);

                builder.setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.pause_notif)
                        .setTicker(songTitle)
                        .setOngoing(true)
                        .setContentTitle(getString(R.string.pausado))
                        .setContentText(songTitle);
                Notification not = builder.build();

                startForeground(NOTIFY_ID, not);

                Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
                LocalBroadcastManager.getInstance(MusicaService.this).sendBroadcast(onPreparedIntent);
            }
        });
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.w("MusicaService.go", "Ejecutado");
                player.start();
                isPause = false;
                Intent notIntent = new Intent(MusicaService.this, HomeActivity.class);
                notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendInt = PendingIntent.getActivity(MusicaService.this, 0,
                        notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(MusicaService.this);

                builder.setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.play_notif)
                        .setTicker(songTitle)
                        .setOngoing(true)
                        .setContentTitle(getString(R.string.reproduciendo))
                        .setContentText(songTitle);
                Notification not = builder.build();

                startForeground(NOTIFY_ID, not);

                Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
                LocalBroadcastManager.getInstance(MusicaService.this).sendBroadcast(onPreparedIntent);
            }
        });
    }
    public void playPrev() throws IOException{
        Log.w("MusicaService.playPrev", "Ejecutado");
        cancionPosicion--;
        if(cancionPosicion < 0) cancionPosicion=canciones.size()-1;
        playSong();
    }

    public void playNext() throws IOException{
        Log.w("MusicaService.playNext", "Ejecutado");
        if(shuffle){
            int newSong = cancionPosicion;
            while(newSong==cancionPosicion){
                newSong=rand.nextInt(canciones.size());
            }
            cancionPosicion=newSong;
        }else {
            cancionPosicion++;
            if (cancionPosicion >= canciones.size()) cancionPosicion = 0;
        }
        playSong();
    }
}
