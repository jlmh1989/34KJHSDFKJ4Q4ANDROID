package com.rhcloud.app_nestmusic.nestmusic.servicio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.MediaController;

import com.rhcloud.app_nestmusic.nestmusic.HomeActivity;
import com.rhcloud.app_nestmusic.nestmusic.R;
import com.rhcloud.app_nestmusic.nestmusic.bean.CancionBean;
import com.rhcloud.app_nestmusic.nestmusic.musica.MusicaController;

import java.io.IOException;
import java.util.ArrayList;
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
    private Random rand;
    private Handler mHandler = new Handler();

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
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
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

    }

    public class MusicaBinder extends Binder{
        public MusicaService getService(){
            return MusicaService.this;
        }
    }

    public void playSong(){
        Log.w("MusicaService.playSong", "Ejecutado");
        player.reset();
        CancionBean cancion = canciones.get(cancionPosicion);
        try {
            player.setDataSource(getApplicationContext(), cancion.getPathMusica());
            songTitle = cancion.getTitulo();
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0){
            Log.w("MusicaService.onCompletion", "Ejecutado");
            player.reset();
            playNext();
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

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.w("MusicaService.pausePlayer", "Ejecutado");
                player.pause();
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
    public void playPrev(){
        Log.w("MusicaService.playPrev", "Ejecutado");
        cancionPosicion--;
        if(cancionPosicion < 0) cancionPosicion=canciones.size()-1;
        playSong();
    }

    public void playNext(){
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
