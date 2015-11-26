package com.thalhamer.numbersgame.services;import android.media.MediaPlayer;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.enums.sounds.TileCountBeep;import java.util.List;import javax.inject.Inject;/** * Created by Brian on 10/16/2015. */public class SoundService {    @Inject  //here to bypass weird injection rule for dagger    public SoundService() {    }    public void playTileCountBeep(List<Tile> touchedTiles) {        TileCountBeep tileCountBeep = TileCountBeep.getTileCountBeepByTileCount(touchedTiles.size());        if (tileCountBeep != null) {            tileCountBeep.getMediaPlayer().start();        }    }    public void playSound(SoundEnum soundEnum) {        soundEnum.getMediaPlayer().start();    }    public void playSound(int resourceId) {        MediaPlayer mediaPlayer = MediaPlayer.create(App.getContext(), resourceId);        mediaPlayer.start();        //need finish listener before release        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {            @Override            public void onCompletion(MediaPlayer mediaPlayer) {                mediaPlayer.stop();                if (mediaPlayer != null) {                    mediaPlayer.release();                }            }        });    }}