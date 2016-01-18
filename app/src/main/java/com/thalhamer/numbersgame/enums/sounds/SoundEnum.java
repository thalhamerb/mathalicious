package com.thalhamer.numbersgame.enums.sounds;import android.media.MediaPlayer;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;/** * Created by Brian on 10/16/2015. */public enum SoundEnum {    NEGATIVE_BEEP(R.raw.negative_beep), POSITIVE_BEEP_01(R.raw.positive_beep01),    POSITIVE_BEEP_02(R.raw.success1), BOUGHT_POWER(R.raw.bought_coins),    CLICK1(R.raw.click1), EXPLOSION(R.raw.explosion);    private int resourceId;    private MediaPlayer mediaPlayer;    SoundEnum(int resourceId) {        this.resourceId = resourceId;        this.mediaPlayer = MediaPlayer.create(App.getContext(), resourceId);    }    public int getResourceId() {        return resourceId;    }    public MediaPlayer getMediaPlayer() {        return mediaPlayer;    }}