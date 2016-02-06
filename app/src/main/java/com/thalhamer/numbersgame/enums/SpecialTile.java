package com.thalhamer.numbersgame.enums;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.media.MediaPlayer;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;/** * Special Tile enum * * Created by Brian on 10/27/2015. */public enum SpecialTile implements GameExplanation {    LEPRECHAUN(3, R.drawable.leprechaun_head, R.raw.leprechaun,            "Leprechaun - catch by including in valid tile sequence.  ", R.drawable.explain_leprechaun);    private int mapValue;    private int resourceId;    private Bitmap bitmap;    private int soundResourceId;    private MediaPlayer mediaPlayer;    private String description;    private int explanationId;    private String gameExplanationTitle;    SpecialTile(int mapValue, int resourceId, int soundResourceId, String description, int explanationId) {        this.mapValue = mapValue;        this.resourceId = resourceId;        this.bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), resourceId);        this.soundResourceId = soundResourceId;        this.mediaPlayer = MediaPlayer.create(App.getContext(), soundResourceId);        this.description = description;        this.explanationId = explanationId;        this.gameExplanationTitle = "Other";    }    public static SpecialTile getSpecialTileByMappedValue(Integer value) {        for (SpecialTile specialTile : SpecialTile.values()) {            if (specialTile.mapValue == value) {                return specialTile;            }        }        return null;    }    public int getMapValue() {        return mapValue;    }    public int getResourceId() {        return resourceId;    }    public Bitmap getBitmap() {        return bitmap;    }    public int getSoundResourceId() {        return soundResourceId;    }    public MediaPlayer getMediaPlayer() {        return mediaPlayer;    }    @Override    public String getDescription() {        return description;    }    @Override    public int getExplanationId() {        return explanationId;    }    @Override    public String getGameExplanationTitle() {        return gameExplanationTitle;    }}